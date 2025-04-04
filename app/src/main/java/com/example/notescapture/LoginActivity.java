package com.example.notescapture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvSignUpPrompt;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUpPrompt = findViewById(R.id.tvSignUpPrompt);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (validateInput(email, password)) {
                    // TODO: Implement actual login logic
                    Intent intent = new Intent(LoginActivity.this, NotesActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        tvSignUpPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
