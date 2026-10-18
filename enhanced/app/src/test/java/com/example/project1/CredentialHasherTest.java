package com.example.project1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

// CS499 Capstone enhancement: Tests for password hashing 
public class CredentialHasherTest {

    @Test
    public void samePasswordAndSaltGiveTheSameKey() {
        String salt = CredentialHasher.generateSalt();
        assertEquals(CredentialHasher.HashPassword("password", salt),
                CredentialHasher.HashPassword("password", salt));
    }

    @Test
    public void differentPasswordsGiveDifferentKeys() {
        String salt = CredentialHasher.generateSalt();
        assertNotEquals(CredentialHasher.HashPassword("password", salt),
                CredentialHasher.HashPassword("Password", salt));
    }

    @Test
    public void sameSaltIsNeverReused() {
        assertNotEquals(CredentialHasher.generateSalt(), CredentialHasher.generateSalt());
    }

    @Test
    public void samePasswordWithDifferentSaltsGivesDifferentKeys() {
        assertNotEquals(CredentialHasher.HashPassword("password", CredentialHasher.generateSalt()),
                CredentialHasher.HashPassword("password", CredentialHasher.generateSalt()));
    }

    @Test
    public void storedKeyIsNotThePlainPassword() {
        String key = CredentialHasher.HashPassword("password", CredentialHasher.generateSalt());
        assertFalse(key.contains("password"));
    }
}
