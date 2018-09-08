package com.mediaproject.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class InitializeTwitterDB {
	
	private static String url;
    private static String user;
    private static String password;
    
    
    public InitializeTwitterDB() throws IOException {
    	Properties prop = new Properties();  
    	InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("./twitterdb.properties");
    	prop.load(inStream);
    	url = prop.getProperty("hostname");
    	user = prop.getProperty("user");
    	password = prop.getProperty("password");
    	inStream.close();
    }
    
    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Initialized Connection to PostgreSQL server");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
 
        return conn;
    }
	
}
