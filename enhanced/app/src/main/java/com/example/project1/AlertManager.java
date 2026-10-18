package com.example.project1;

// CS499 enhancement: Edge-triggered low stock alert that fires only when quantity crosses below the threshold.
public class AlertManager {

    private final int threshold;

    // Uses default threshold
    public AlertManager() {
        this(AppConfig.LOW_STOCK_THRESHOLD);
    }

    // Sets custom threshold
    public AlertManager(int threshold) {
        this.threshold = threshold;
    }

    // Detects threshold crossing
    public boolean isLowStockCrossing(int previousQuantity, int newQuantity) {
        boolean wasOkBefore = previousQuantity >= threshold;
        boolean isLowNow = newQuantity < threshold;
        return wasOkBefore && isLowNow;
    }
}
