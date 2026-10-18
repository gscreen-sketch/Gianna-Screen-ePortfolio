package com.example.project1;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

// CS499 enhancement: Dedicated create-account screen that is separate from the login screen
public class CreateAccountBackend extends AppCompatActivity {

    // Declare screen variables
    EditText username;
    EditText password;
    Button submitBtn;
    Button backToLoginBtn;
    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load create account layout
        setContentView(R.layout.activity_create_account);

        // Connect to screen elements
        username = findViewById(R.id.NewUsername);
        password = findViewById(R.id.NewPassword);
        submitBtn = findViewById(R.id.SubmitCreateAccount);
        backToLoginBtn = findViewById(R.id.BackToLogin);

        // Start auth repository
        authRepository = new AuthRepository();

        // Create account clicked
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredUsername = username.getText().toString();
                String enteredPassword = password.getText().toString();

                // Check for empty fields
                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(CreateAccountBackend.this, "Enter a username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // CS499 Capstone enhancement: Account is saved on the server
                submitBtn.setEnabled(false);
                new Thread(() -> {
                    try {
                        // Check username taken, then save new account, then password is hashed in  repository
                        boolean created = !authRepository.userExists(enteredUsername)
                                && authRepository.addUser(enteredUsername, enteredPassword);
                        runOnUiThread(() -> {
                            submitBtn.setEnabled(true);
                            if (created) {
                                Toast.makeText(CreateAccountBackend.this, "Account creation successful", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CreateAccountBackend.this, "Invalid - username is already taken", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        runOnUiThread(() -> {
                            submitBtn.setEnabled(true);
                            Toast.makeText(CreateAccountBackend.this, "Could not reach the server", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }
        });

        // Back to login clicked
        backToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
