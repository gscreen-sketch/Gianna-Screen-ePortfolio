package com.example.project1;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// SMS permission screen
public class sms_backend extends AppCompatActivity {


    private static final int SMS_PERMISSION_CODE = 100;

    TextView statusText;
    Button enableBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        // Connect to screen elements
        statusText = findViewById(R.id.Status);
        enableBtn = findViewById(R.id.Enable);

        // Enable button clicked
        enableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check existing permission
                if (ContextCompat.checkSelfPermission(sms_backend.this,
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                    // Already have permission
                    sendSms();

                } else {

                    // Ask for permission
                    ActivityCompat.requestPermissions(sms_backend.this,
                            new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                }
            }
        });
    }

    // Runs after permission response
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {

            // User said yes
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                statusText.setText("Status: enabled");
                sendSms();

            } else {

                // User said no
                statusText.setText("Status: disabled");
                Toast.makeText(sms_backend.this, "SMS disabled", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Sends SMS
    private void sendSms() {
        statusText.setText("Status: enabled");
        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("5554", null, "ATTENTION: Low inventory", null, null);
            Toast.makeText(sms_backend.this, "SMS alert sent", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // SMS failed
            Toast.makeText(sms_backend.this, "SMS failed", Toast.LENGTH_SHORT).show();
        }
    }
}