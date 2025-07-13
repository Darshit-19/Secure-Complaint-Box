package com.securecomplaintbox.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = ConfigUtil.getDatabaseUrl();
        String user = ConfigUtil.getDatabaseUser();
        String pass = ConfigUtil.getDatabasePassword();
        
        return DriverManager.getConnection(url, user, pass);
    }
}
