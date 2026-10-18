package com.example.project1;
import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

// CS499 capstone enhancement: User authentication screens go through the repository instead of directly to a database
public class AuthRepository {

    // Looks up the salt for a username, null if the user does not exist
    private String fetchSalt(String username) throws IOException {
        ApiClient.Response response = ApiClient.request("GET", "/users/" + Uri.encode(username), null);
        if (response.code == 404) {
            return null;
        }
        if (response.code != 200) {
            throw new IOException("Server returned " + response.code);
        }
        try {
            return new JSONObject(response.body).getString("salt");
        } catch (JSONException e) {
            throw new IOException("Bad server reply", e);
        }
    }

    // Validates user login credentials
    public boolean checkUser(String username, String password) throws IOException {
        String salt = fetchSalt(username);
        if (salt == null) {
            return false;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("hash", CredentialHasher.HashPassword(password, salt));
            ApiClient.Response response = ApiClient.request("POST", "/login", body);
            if (response.code == 200) {
                return true;
            }
            if (response.code == 401) {
                return false;
            }
            throw new IOException("Server returned " + response.code);
        } catch (JSONException e) {
            throw new IOException("Bad request", e);
        }
    }

    // Checks if username exists
    public boolean userExists(String username) throws IOException {
        return fetchSalt(username) != null;
    }

    // Inserts new user record, returns false if the username was already taken
    public boolean addUser(String username, String password) throws IOException {
        String salt = CredentialHasher.generateSalt();
        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("hash", CredentialHasher.HashPassword(password, salt));
            body.put("salt", salt);
            ApiClient.Response response = ApiClient.request("POST", "/users", body);
            if (response.code == 201) {
                return true;
            }
            if (response.code == 409) {
                return false;
            }
            throw new IOException("Server returned " + response.code);
        } catch (JSONException e) {
            throw new IOException("Bad request", e);
        }
    }
}
