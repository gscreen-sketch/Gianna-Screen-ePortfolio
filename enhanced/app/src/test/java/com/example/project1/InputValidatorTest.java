package com.example.project1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

// CS499 Capstone enhancement: Tests for quantity input validation 
public class InputValidatorTest {

    @Test
    public void wholeNumberIsAccepted() {
        InputValidator.QuantityResult result = InputValidator.parseQuantity("12");
        assertTrue(result.valid);
        assertEquals(12, result.value);
    }

    @Test
    public void zeroIsAccepted() {
        assertTrue(InputValidator.parseQuantity("0").valid);
    }

    @Test
    public void textIsRejected() {
        assertFalse(InputValidator.parseQuantity("abc").valid);
    }

    @Test
    public void decimalIsRejected() {
        assertFalse(InputValidator.parseQuantity("2.5").valid);
    }

    @Test
    public void negativeNumberIsRejected() {
        assertFalse(InputValidator.parseQuantity("-3").valid);
    }

    @Test
    public void emptyOrNullIsRejected() {
        assertFalse(InputValidator.parseQuantity("").valid);
        assertFalse(InputValidator.parseQuantity("   ").valid);
        assertFalse(InputValidator.parseQuantity(null).valid);
    }

    @Test
    public void numberTooLargeForAnIntIsRejected() {
        assertFalse(InputValidator.parseQuantity("99999999999").valid);
    }
}
