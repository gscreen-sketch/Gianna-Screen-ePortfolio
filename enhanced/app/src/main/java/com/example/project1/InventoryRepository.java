package com.example.project1;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// CS499 Capstone enhancement: Repository pattern sits between DatabaseBackend and the data source
public class InventoryRepository {

    public static class InventoryItem {
        public final int id;
        public final String name;
        public final int quantity;

        // Stores inventory item data
        public InventoryItem(int id, String name, int quantity) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
        }
    }

    // Throws if the server did not accept the request
    private void requireSuccess(ApiClient.Response response) throws IOException {
        if (response.code >= 300) {
            throw new IOException("Server returned " + response.code);
        }
    }

    // Inserts new inventory item
    public void addItem(String itemName, int quantity) throws IOException {
        try {
            JSONObject body = new JSONObject();
            body.put("name", itemName);
            body.put("quantity", quantity);
            requireSuccess(ApiClient.request("POST", "/items", body));
        } catch (JSONException e) {
            throw new IOException("Bad request", e);
        }
    }

    // CS499 Capstone enhancement: Duplicate checker - HashMap index
    public void addOrUpdateItem(String itemName, int quantity) throws IOException {
        Map<String, InventoryItem> byName = new HashMap<>();
        for (InventoryItem item : getAllItems()) {
            byName.put(item.name, item);
        }

        InventoryItem existing = byName.get(itemName);
        if (existing != null) {
            updateItemQuantity(existing.id, existing.quantity + quantity);
        } else {
            addItem(itemName, quantity);
        }
    }

    // Returns all inventory items
    public List<InventoryItem> getAllItems() throws IOException {
        ApiClient.Response response = ApiClient.request("GET", "/items", null);
        requireSuccess(response);
        List<InventoryItem> items = new ArrayList<>();
        try {
            JSONArray rows = new JSONArray(response.body);
            for (int i = 0; i < rows.length(); i++) {
                JSONObject row = rows.getJSONObject(i);
                items.add(new InventoryItem(
                        row.getInt("id"),
                        row.getString("name"),
                        row.getInt("quantity")));
            }
        } catch (JSONException e) {
            throw new IOException("Bad server reply", e);
        }
        return items;
    }

    // Updates inventory item quantity
    public void updateItemQuantity(int id, int newQuantity) throws IOException {
        try {
            JSONObject body = new JSONObject();
            body.put("quantity", newQuantity);
            requireSuccess(ApiClient.request("PUT", "/items/" + id, body));
        } catch (JSONException e) {
            throw new IOException("Bad request", e);
        }
    }

    // Deletes inventory item by id
    public void deleteItem(int id) throws IOException {
        requireSuccess(ApiClient.request("DELETE", "/items/" + id, null));
    }
}
