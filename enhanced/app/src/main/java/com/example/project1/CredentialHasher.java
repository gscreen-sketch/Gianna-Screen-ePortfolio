package com.example.project1;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// CS499 Capstone enhancement: Password hashing - PBKDF2WithHmacSHA256 with a random salt per user
public class CredentialHasher {

    private static final int STRETCH_ROUNDS = 210000;
    private static final int DERIVED_KEY_BITS = 256;
    private static final int SALT_SIZE_BYTES = 16;

    // Prevents class instantiation
    private CredentialHasher() {
    }

    // Creates a new random salt
    public static String generateSalt() {
        byte[] randomSaltBytes = new byte[SALT_SIZE_BYTES];
        new SecureRandom().nextBytes(randomSaltBytes);
        return Base64.getEncoder().encodeToString(randomSaltBytes);
    }

    // Derives the key stored in place of the password
    public static String HashPassword(String typedPassword, String encodedSalt) {
        PBEKeySpec derivationRequest = new PBEKeySpec(typedPassword.toCharArray(),
                Base64.getDecoder().decode(encodedSalt), STRETCH_ROUNDS, DERIVED_KEY_BITS);
        try {
            SecretKeyFactory keyStretcher = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.getEncoder().encodeToString(keyStretcher.generateSecret(derivationRequest).getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password hashing unavailable", e);
        } finally {
            derivationRequest.clearPassword();
        }
    }
}
