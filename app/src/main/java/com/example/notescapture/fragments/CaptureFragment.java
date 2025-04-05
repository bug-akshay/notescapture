package com.example.notescapture.fragments;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.notescapture.NotesActivity;
import com.example.notescapture.R;
import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaptureFragment extends Fragment {
    private static final int CAMERA_PERMISSION_CODE = 100;

    private TextView extractedTextView;
    private MaterialButton clearButton;
    private MaterialButton captureButton;
    private MaterialButton copyButton;
    private PreviewView previewView;
    private TextRecognizer textRecognizer;
    private ProcessCameraProvider cameraProvider;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_capture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupButtons();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void initializeViews(View view) {
        extractedTextView = view.findViewById(R.id.extractedTextView);
        clearButton = view.findViewById(R.id.clearButton);
        captureButton = view.findViewById(R.id.captureButton);
        copyButton = view.findViewById(R.id.copyButton);
        previewView = view.findViewById(R.id.previewView);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void setupButtons() {
        clearButton.setOnClickListener(v -> extractedTextView.setText(""));

        captureButton.setOnClickListener(v -> captureImage());

        copyButton.setOnClickListener(v -> {
            String text = extractedTextView.getText().toString();
            if (!text.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Extracted Text", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCamera() {
        previewView.setVisibility(View.VISIBLE);
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) requireActivity(),
                        cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(requireContext(), "Failed to start camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void captureImage() {
        if (imageCapture == null) return;

        imageCapture.takePicture(cameraExecutor,
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                        processImage(imageProxy);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processImage(ImageProxy imageProxy) {
        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees());

        textRecognizer.process(image)
                .addOnSuccessListener(text -> {
                    String extractedText = text.getText();
                    requireActivity().runOnUiThread(() -> extractedTextView.setText(extractedText));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to extract text", Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
