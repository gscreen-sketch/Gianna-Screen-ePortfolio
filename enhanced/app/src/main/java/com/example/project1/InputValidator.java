package com.example.project1;

// CS499 Capstone enhancement: Input validation - Safe quantity parsing
public class InputValidator {

    public static class QuantityResult {
        public final boolean valid;
        public final int value;
        public final String errorMessage;

        // Stores parse result fields
        private QuantityResult(boolean valid, int value, String errorMessage) {
            this.valid = valid;
            this.value = value;
            this.errorMessage = errorMessage;
        }

        // Creates success result
        static QuantityResult ok(int value) {
            return new QuantityResult(true, value, null);
        }

        // Creates error result
        static QuantityResult error(String message) {
            return new QuantityResult(false, -1, message);
        }
    }

    // Parses and validates quantity
    public static QuantityResult parseQuantity(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return QuantityResult.error("Quantity cannot be empty");
        }
        try {
            int parsed = Integer.parseInt(rawText.trim());
            if (parsed < 0) {
                return QuantityResult.error("Quantity cannot be negative");
            }
            return QuantityResult.ok(parsed);
        } catch (NumberFormatException e) {
            return QuantityResult.error("Quantity must be a whole number");
        }
    }
}
