package com.securecomplaintbox.util;

public class ConfigUtil {

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("Missing required environment variable: " + key);
        }
        return value;
    }

    // ---- Database Configuration ----
    public static String getDatabaseUrl() {
        return getEnv("DB_URL");
    }

    public static String getDatabaseUser() {
        return getEnv("DB_USER");
    }

    public static String getDatabasePassword() {
        return getEnv("DB_PASSWORD");
    }

    // ---- Email Configuration ----
    public static String getEmailUser() {
        return getEnv("EMAIL_USER");
    }

    public static String getEmailPassword() {
        return getEnv("EMAIL_PASSWORD");
    }

    public static String getSmtpHost() {
        return getEnv("SMTP_HOST");
    }

    public static String getSmtpPort() {
        return getEnv("SMTP_PORT");
    }

    // ---- Encryption Configuration ----
    public static String getEncryptionSecret() {
        return getEnv("ENCRYPTION_SECRET");
    }

    // ---- Environment Type (Optional) ----
    public static boolean isProduction() {
        return "production".equalsIgnoreCase(System.getenv("ENVIRONMENT"));
    }

    // ---- Debug Method ----
    public static void printConfiguration() {
        System.out.println("=== Configuration Debug ===");
        System.out.println("Environment: " + (isProduction() ? "PRODUCTION" : "UNKNOWN"));
        System.out.println("Database URL: " + getDatabaseUrl());
        System.out.println("Database User: " + getDatabaseUser());
        System.out.println("Email User: " + getEmailUser());
        System.out.println("SMTP Host: " + getSmtpHost());
        System.out.println("SMTP Port: " + getSmtpPort());
        System.out.println("Encryption Secret Length: " + getEncryptionSecret().length());
        System.out.println("==========================");
    }
}
