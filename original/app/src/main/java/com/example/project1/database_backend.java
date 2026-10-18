package com.example.project1;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Invenry Screen
public class database_backend extends AppCompatActivity {

    // Variables
    EditText itemName;
    EditText quantity;
    Button addItemBtn;
    LinearLayout inventoryBox;
    database_helper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Database layout
        setContentView(R.layout.activity_database);
        itemName = findViewById(R.id.ItemName);
        quantity = findViewById(R.id.Quantity);
        addItemBtn = findViewById(R.id.buttonAddItem);
        inventoryBox = findViewById(R.id.inventoryBox);
        db = new database_helper(this);

        loadItems();

        // Add item clicked
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = itemName.getText().toString();
                String qty = quantity.getText().toString();

                // Check for empty fields
                if (name.isEmpty() || qty.isEmpty()) {
                    Toast.makeText(database_backend.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save item
                db.addItem(name, Integer.parseInt(qty));
                itemName.setText("");
                quantity.setText("");
                loadItems();
            }
        });
    }

    // Load all items
    private void loadItems() {

        inventoryBox.removeAllViews();

        // Get all items
        Cursor cursor = db.getAllItems();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int qty = cursor.getInt(2);

            // Create row
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(10, 10, 10, 10);

            // Display item name
            TextView nameView = new TextView(this);
            nameView.setText(name);
            nameView.setTextColor(0xFF000000);
            nameView.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 2));

            // Display item quantity
            EditText qtyView = new EditText(this);
            qtyView.setText(String.valueOf(qty));
            qtyView.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            qtyView.setTextColor(0xFF000000);
            qtyView.setGravity(Gravity.CENTER);
            qtyView.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            // Delete button
            Button deleteBtn = new Button(this);
            deleteBtn.setText("Del");
            deleteBtn.setTextColor(0xFFFFFFFF);
            deleteBtn.setBackgroundColor(0xFF000000);

            // Quantity Checker
            qtyView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {

                        String newQty = qtyView.getText().toString();

                        if (!newQty.isEmpty()) {
                            int newQtyInt = Integer.parseInt(newQty);

                            // Save updated quantity
                            db.updateItem(id, newQtyInt);

                            // Check low stock (SMS Link)
                            if (newQtyInt < 5) {
                                Toast.makeText(database_backend.this, name + " is low on stock!", Toast.LENGTH_SHORT).show();
                                Intent goToSms = new Intent(database_backend.this, sms_backend.class);
                                startActivity(goToSms);
                            }
                        }
                    }
                }
            });

            // Delete button clicked
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove from database
                    db.deleteItem(id);
                    loadItems();
                }
            });

            row.addView(nameView);
            row.addView(qtyView);
            row.addView(deleteBtn);
            inventoryBox.addView(row);
        }

        cursor.close();
    }
}