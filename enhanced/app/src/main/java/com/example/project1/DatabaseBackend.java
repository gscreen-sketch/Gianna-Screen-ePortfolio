package com.example.project1;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Handles inventory screen
public class DatabaseBackend extends AppCompatActivity {

    // Declare screen variables
    EditText itemName;
    EditText quantity;
    Button addItemBtn;
    RecyclerView inventoryRecyclerView;
    // CS499 Capstone enhancement: Repository pattern - InventoryRepository/AuthRepository
    InventoryRepository inventoryRepository;
    // CS499 Capstone enhancement: low stock alert - AlertManager
    AlertManager alertManager;
    // CS499 Capstone enhancement: RecyclerView-based inventory list - InventoryAdapter
    InventoryAdapter inventoryAdapter;
    // CS499 Capstone enhancement: Direct low stock SMS alert
    private static final int SMS_PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load database layout
        setContentView(R.layout.activity_database);

        // Connect to screen elements
        itemName = findViewById(R.id.ItemName);
        quantity = findViewById(R.id.Quantity);
        addItemBtn = findViewById(R.id.buttonAddItem);
        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView);
        inventoryRepository = new InventoryRepository();
        alertManager = new AlertManager();

        // Set up the inventory list
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        inventoryAdapter = new InventoryAdapter(new ArrayList<>(), new InventoryAdapter.Listener() {
            @Override
            public void onQuantityCommitted(InventoryRepository.InventoryItem item, int newQuantity) {

                new Thread(() -> {
                    try {
                        // Save updated quantity
                        inventoryRepository.updateItemQuantity(item.id, newQuantity);

                        runOnUiThread(() -> {
                            if (alertManager.isLowStockCrossing(item.quantity, newQuantity)) {
                                Toast.makeText(DatabaseBackend.this, item.name + " is low on stock!", Toast.LENGTH_SHORT).show();
                                // CS499 Capstone enhancement: Sends the alert directly instead of opening the Sms screen
                                sendLowStockAlert();
                            }
                        });

                        // Reload so the row's background and stored quantity stay in sync
                        loadItems();
                    } catch (IOException e) {
                        showServerError();
                    }
                }).start();
            }

            @Override
            public void onInvalidQuantity(String errorMessage) {
                Toast.makeText(DatabaseBackend.this, errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClicked(InventoryRepository.InventoryItem item) {
                new Thread(() -> {
                    try {
                        // Remove via the repository
                        inventoryRepository.deleteItem(item.id);
                        loadItems();
                    } catch (IOException e) {
                        showServerError();
                    }
                }).start();
            }
        });
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        // Add item clicked
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get typed text
                String name = itemName.getText().toString();
                String qty = quantity.getText().toString();

                // Check for empty fields
                if (name.isEmpty() || qty.isEmpty()) {
                    Toast.makeText(DatabaseBackend.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // CS499 Capstone enhancement: Input validation
                InputValidator.QuantityResult result = InputValidator.parseQuantity(qty);
                if (!result.valid) {
                    Toast.makeText(DatabaseBackend.this, result.errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    try {
                        // CS499 Capstone enhancement: Duplicate checker
                        inventoryRepository.addOrUpdateItem(name, result.value);

                        // Clear fields
                        runOnUiThread(() -> {
                            itemName.setText("");
                            quantity.setText("");
                        });

                        // Reload the list
                        loadItems();
                    } catch (IOException e) {
                        showServerError();
                    }
                }).start();
            }
        });

        // Show the current inventory
        loadItems();
    }

    // Loads all items on screen
    private void loadItems() {
        new Thread(() -> {
            try {
                // CS499 Capstone enhancement: Sorted inventory view
                List<InventoryRepository.InventoryItem> sorted = InventoryOrganizer.getSortedItems(
                        inventoryRepository.getAllItems(), InventoryOrganizer.SortKey.NAME);
                runOnUiThread(() -> inventoryAdapter.setItems(sorted));
            } catch (IOException e) {
                showServerError();
            }
        }).start();
    }

    // CS499 Capstone enhancement: Notification to user when  server can't be reached
    private void showServerError() {
        runOnUiThread(() -> Toast.makeText(DatabaseBackend.this, "Could not reach the server", Toast.LENGTH_SHORT).show());
    }

    // CS499 Capstone enhancement: Direct low stock SMS alert
    private void sendLowStockAlert() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(AppConfig.ALERT_PHONE_NUMBER, null, "ATTENTION: Low inventory", null, null);
                Toast.makeText(this, "Low stock SMS sent", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "SMS failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendLowStockAlert();
        }
    }
}
