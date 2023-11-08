package com.example.javaapplication2;

    import java.sql.*;

    public class Test {

        public static void main(String[] args) throws ClassNotFoundException, SQLException {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName); // here is the ClassNotFoundException

            String serverName = "localhost";
            String mydatabase = "jss";
            String url = "jdbc:mysql://" + serverName + "/" + mydatabase;

            String username = "root";
            String password = "root";
            Connection connection = DriverManager.getConnection(url, username, password);

            System.out.println("Connected to mysql successfully");
        }

    }

