package com.example.project1;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// CS499 Capstone enhancement: All the reads/writes that used to live here moved into AuthRepository and InventoryRepository.
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    //prevents multiple connections to the same database file
    private static DatabaseHelper instance;

    // Returns shared singleton instance

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Sets up database configuration
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Runs on first creation
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create users table
        db.execSQL("CREATE TABLE users (username TEXT PRIMARY KEY, password TEXT NOT NULL)");

        // Create inventory table
        db.execSQL("CREATE TABLE inventory (id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT NOT NULL, quantity INTEGER NOT NULL)");
    }

    // Version Management
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS inventory");
        onCreate(db);
    }
}
