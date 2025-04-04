package com.example.notescapture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnSignup;
    private TextView tvLoginPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLoginPrompt = findViewById(R.id.tvLoginPrompt);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (validateInput(name, email, password, confirmPassword)) {
                    // TODO: Implement actual signup logic
                    Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, NotesActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        tvLoginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
