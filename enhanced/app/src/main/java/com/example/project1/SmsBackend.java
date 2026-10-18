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
import java.io.IOException;
import java.util.List;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// Handles SMS permission screen
public class SmsBackend extends AppCompatActivity {


    private static final int SMS_PERMISSION_CODE = 100;

    TextView statusText;
    Button enableBtn;
    // CS499 Capstone enhancement: Low stock list - InventoryOrganizer
    TextView lowStockListText;
    InventoryRepository inventoryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load SMS layout
        setContentView(R.layout.activity_sms);

        // Connect to screen elements
        statusText = findViewById(R.id.Status);
        enableBtn = findViewById(R.id.Enable);
        lowStockListText = findViewById(R.id.lowStockList);

        // CS499 Capstone enhancement: Low stock list
        inventoryRepository = new InventoryRepository();
        showLowStockItems();

        // Enable button clicked
        enableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check existing permission
                if (ContextCompat.checkSelfPermission(SmsBackend.this,
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                    // Already have permission
                    sendSms();

                } else {

                    // Ask for permission
                    ActivityCompat.requestPermissions(SmsBackend.this,
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
                Toast.makeText(SmsBackend.this, "SMS disabled. App will still work without it.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Sends SMS
    private void sendSms() {
        statusText.setText("Status: enabled");
        try {

            // CS499 Capstone enhancement: Repository pattern & centralized config
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(AppConfig.ALERT_PHONE_NUMBER, null, "ATTENTION: Low inventory", null, null);
            Toast.makeText(SmsBackend.this, "SMS alert sent!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // SMS failed
            Toast.makeText(SmsBackend.this, "SMS failed", Toast.LENGTH_SHORT).show();
        }
    }

    // CS499 Capstone enhancement:  Builds  low stock list off the current inventory & shows  item names
    private void showLowStockItems() {
        new Thread(() -> {
            try {
                List<InventoryRepository.InventoryItem> lowStock = InventoryOrganizer.getLowStockItems(
                        inventoryRepository.getAllItems(), AppConfig.LOW_STOCK_THRESHOLD);

                if (lowStock.isEmpty()) {
                    runOnUiThread(() -> lowStockListText.setText("Low stock items: none"));
                    return;
                }

                StringBuilder names = new StringBuilder("Low stock items: ");
                for (int i = 0; i < lowStock.size(); i++) {
                    names.append(lowStock.get(i).name);
                    if (i < lowStock.size() - 1) {
                        names.append(", ");
                    }
                }
                runOnUiThread(() -> lowStockListText.setText(names.toString()));
            } catch (IOException e) {
                runOnUiThread(() -> lowStockListText.setText("Low stock items: could not reach the server"));
            }
        }).start();
    }
}
