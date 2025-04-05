package com.example.notescapture;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.example.notescapture.models.Note;

import com.google.android.material.button.MaterialButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class NotesActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;

    private TextView extractedTextView;
    private MaterialButton clearButton;
    private MaterialButton captureButton;
    private MaterialButton copyButton;
    private MaterialButton saveButton;
    private MaterialButton viewNotesButton;
    private TextInputEditText titleEditText;
    private TextRecognizer textRecognizer;
    private ActivityResultLauncher<Void> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        initializeViews();
        setupButtons();
        initializeCameraLauncher();
    }

    private void initializeViews() {
        extractedTextView = findViewById(R.id.extractedTextView);
        clearButton = findViewById(R.id.clearButton);
        captureButton = findViewById(R.id.captureButton);
        copyButton = findViewById(R.id.copyButton);
        saveButton = findViewById(R.id.saveButton);
        viewNotesButton = findViewById(R.id.viewNotesButton);
        titleEditText = findViewById(R.id.titleEditText);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    private void setupButtons() {
        clearButton.setOnClickListener(v -> {
            extractedTextView.setText("");
            titleEditText.setText("");
        });

        captureButton.setOnClickListener(v -> checkCameraPermission());

        saveButton.setOnClickListener(v -> saveNote());

        viewNotesButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotesListActivity.class);
            startActivity(intent);
        });

        copyButton.setOnClickListener(v -> {
            String text = extractedTextView.getText().toString();
            if (!text.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Extracted Text", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeCameraLauncher() {
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap != null) {
                    processImage(bitmap);
                }
            });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        cameraLauncher.launch(null);
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = extractedTextView.getText().toString().trim();

        if (title.isEmpty()) {
            titleEditText.setError("Please enter a title");
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "Please capture some text first", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note(title, content);
        // TODO: Save note to database
        Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void processImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        textRecognizer.process(image)
                .addOnSuccessListener(text -> {
                    String extractedText = text.getText();
                    extractedTextView.setText(extractedText);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to extract text", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
