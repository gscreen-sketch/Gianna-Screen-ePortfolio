package com.example.project1;

// CS499 Capstone enhancement: Repository pattern & Added centralized config
public final class AppConfig {


    // Prevents class instantiation
    private AppConfig() {
    }

    // Minimum stock threshold
    public static final int LOW_STOCK_THRESHOLD = 5;

    public static final String ALERT_PHONE_NUMBER = "1111";

    // REST API address
    public static final String API_BASE_URL = "http://10.0.2.2:3000";

}
