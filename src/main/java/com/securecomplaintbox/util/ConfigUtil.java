package com.securecomplaintbox.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    
    private static Properties properties;
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = ConfigUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Error loading application.properties: " + e.getMessage());
        }
    }
    
    // Database Configuration
    public static String getDatabaseUrl() {
        return System.getenv("DB_URL") != null ? 
               System.getenv("DB_URL") : 
               properties.getProperty("db.url", "jdbc:mysql://localhost:3306/secure_complaint_box");
    }
    
    public static String getDatabaseUser() {
        return System.getenv("DB_USER") != null ? 
               System.getenv("DB_USER") : 
               properties.getProperty("db.user", "root");
    }
    
    public static String getDatabasePassword() {
        return System.getenv("DB_PASSWORD") != null ? 
               System.getenv("DB_PASSWORD") : 
               properties.getProperty("db.password", "root123");
    }
    
    // Email Configuration
    public static String getEmailUser() {
        return System.getenv("EMAIL_USER") != null ? 
               System.getenv("EMAIL_USER") : 
               properties.getProperty("email.user", "hairiyadarshit@gmail.com");
    }
    
    public static String getEmailPassword() {
        return System.getenv("EMAIL_PASSWORD") != null ? 
               System.getenv("EMAIL_PASSWORD") : 
               properties.getProperty("email.password", "otre saxp avya gpum");
    }
    
    public static String getSmtpHost() {
        return System.getenv("SMTP_HOST") != null ? 
               System.getenv("SMTP_HOST") : 
               properties.getProperty("smtp.host", "smtp.gmail.com");
    }
    
    public static String getSmtpPort() {
        return System.getenv("SMTP_PORT") != null ? 
               System.getenv("SMTP_PORT") : 
               properties.getProperty("smtp.port", "587");
    }
    
    // Encryption Configuration
    public static String getEncryptionSecret() {
        return System.getenv("ENCRYPTION_SECRET") != null ? 
               System.getenv("ENCRYPTION_SECRET") : 
               properties.getProperty("encryption.secret", "S3cureCompl@int!");
    }
    
    // Environment check
    public static boolean isProduction() {
        String env = System.getenv("ENVIRONMENT");
        if (env != null) {
            return "production".equalsIgnoreCase(env);
        }
        return "production".equalsIgnoreCase(properties.getProperty("environment", "development"));
    }
    
    // Debug method to check configuration loading
    public static void printConfiguration() {
        System.out.println("=== Configuration Debug ===");
        System.out.println("Environment: " + (isProduction() ? "PRODUCTION" : "DEVELOPMENT"));
        System.out.println("Database URL: " + getDatabaseUrl());
        System.out.println("Database User: " + getDatabaseUser());
        System.out.println("Email User: " + getEmailUser());
        System.out.println("SMTP Host: " + getSmtpHost());
        System.out.println("SMTP Port: " + getSmtpPort());
        System.out.println("Encryption Secret Length: " + getEncryptionSecret().length());
        System.out.println("==========================");
    }
} 