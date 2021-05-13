package ru.kpfu.itis.orm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/orm";
    static final String USER = "postgres";
    static final String PASSWORD = "password";
    static Connection connection = null;
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
        } catch (Exception e){
            throw new SQLException(e.getMessage());
        }
        return connection;
    }

}
