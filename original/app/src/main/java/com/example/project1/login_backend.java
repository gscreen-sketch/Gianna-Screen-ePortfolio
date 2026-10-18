package com.example.project1;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// login screen
public class login_backend extends AppCompatActivity {

    // variables
    EditText username;
    EditText password;
    Button loginBtn;
    Button createAccBtn;
    database_helper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load login 
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        loginBtn = findViewById(R.id.Loginbutton);
        createAccBtn = findViewById(R.id.CreateAccbutton);
        db = new database_helper(this);

        // Login button clicked
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredUsername = username.getText().toString();
                String enteredPassword = password.getText().toString();

                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(login_backend.this, "Enter your username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check database match
                if (db.checkUser(enteredUsername, enteredPassword)) {
                    Intent goToDatabase = new Intent(login_backend.this, database_backend.class);
                    startActivity(goToDatabase);
                } else {
                    Toast.makeText(login_backend.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Create account clicked
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredUsername = username.getText().toString();
                String enteredPassword = password.getText().toString();

                // Check for empty fields
                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(login_backend.this, "Enter a username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check username taken
                if (db.userExists(enteredUsername)) {
                    Toast.makeText(login_backend.this, "Invalid - username is already taken", Toast.LENGTH_SHORT).show();
                } else {
                    db.addUser(enteredUsername, enteredPassword);
                    Toast.makeText(login_backend.this, "Account creation successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}