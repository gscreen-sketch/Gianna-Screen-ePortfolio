package com.example.project1;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

// CS499 Capstone enhancement: Tests for the low stock alert that only fires when quantity crosses below the threshold 
public class AlertManagerTest {

    @Test
    public void alertFiresWhenQuantityDropsBelowThreshold() {
        AlertManager manager = new AlertManager(5);
        assertTrue(manager.isLowStockCrossing(5, 4));
        assertTrue(manager.isLowStockCrossing(10, 0));
    }

    @Test
    public void alertDoesNotRepeatWhileAlreadyLow() {
        AlertManager manager = new AlertManager(5);
        assertFalse(manager.isLowStockCrossing(4, 3));
        assertFalse(manager.isLowStockCrossing(3, 4));
    }

    @Test
    public void alertDoesNotFireWhenStockStaysHealthy() {
        AlertManager manager = new AlertManager(5);
        assertFalse(manager.isLowStockCrossing(6, 8));
        assertFalse(manager.isLowStockCrossing(5, 5));
    }

    @Test
    public void alertDoesNotFireWhenStockIsRestocked() {
        AlertManager manager = new AlertManager(5);
        assertFalse(manager.isLowStockCrossing(2, 20));
    }
}
