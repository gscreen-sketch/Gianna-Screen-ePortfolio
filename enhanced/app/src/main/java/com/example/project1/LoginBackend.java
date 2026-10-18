package com.example.project1;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

// Handles login screen
public class LoginBackend extends AppCompatActivity {

    // Declare screen variables
    EditText username;
    EditText password;
    Button loginBtn;
    Button createAccBtn;
    // CS499 Capstone enhancement: Repository pattern
    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load login layout
        setContentView(R.layout.activity_login);

        // Connect to screen elements
        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        loginBtn = findViewById(R.id.Loginbutton);
        createAccBtn = findViewById(R.id.CreateAccbutton);

        // Start auth repository
        authRepository = new AuthRepository();

        // Login button clicked
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get typed text
                String enteredUsername = username.getText().toString();
                String enteredPassword = password.getText().toString();

                // Check for empty fields
                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(LoginBackend.this, "Enter your username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // CS499 Capstone enhancement: Async login check calls the server
                loginBtn.setEnabled(false);
                new Thread(() -> {
                    try {
                        boolean valid = authRepository.checkUser(enteredUsername, enteredPassword);
                        runOnUiThread(() -> {
                            loginBtn.setEnabled(true);
                            if (valid) {
                                // Go to inventory
                                Intent goToDatabase = new Intent(LoginBackend.this, DatabaseBackend.class);
                                startActivity(goToDatabase);
                            } else {
                                // Show login error
                                Toast.makeText(LoginBackend.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        runOnUiThread(() -> {
                            loginBtn.setEnabled(true);
                            Toast.makeText(LoginBackend.this, "Could not reach the server", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }
        });

        // CS499 Capstone enhancement: Dedicated create-account screen
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCreateAccount = new Intent(LoginBackend.this, CreateAccountBackend.class);
                startActivity(goToCreateAccount);
            }
        });
    }
}
