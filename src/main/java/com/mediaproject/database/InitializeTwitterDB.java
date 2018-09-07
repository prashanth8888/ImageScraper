package com.mediaproject.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class InitializeTwitterDB {
	
	private String url;
    private String user;
    private String password;
    
    
    public InitializeTwitterDB() throws IOException {
    	Properties prop = new Properties();  
    	InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("./twitterdb.properties");
    	prop.load(inStream);
    	this.url = prop.getProperty("hostname");
    	this.user = prop.getProperty("user");
    	this.password = prop.getProperty("password");
    	inStream.close();
    }
    
    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
 
        return conn;
    }
	
}
