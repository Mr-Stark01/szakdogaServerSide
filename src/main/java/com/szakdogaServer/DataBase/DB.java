package com.szakdogaServer.DataBase;

import java.sql.*;
import java.util.Set;

public class DB {
    Connection conn;
    public DB() {
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/Users/gydan/Desktop/szakdoga everything/szakdogaServer/src/main/resources/db/game.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(true);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void test(){
        try {
            String prepTest = "INSERT INTO PLAYER(name,points,wins,losses) values(?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(prepTest);
            preparedStatement.setString(1, "testS");
            preparedStatement.setFloat(2, 21.23f);
            preparedStatement.setInt(3, 12);
            preparedStatement.setInt(4, 32);
            int row = preparedStatement.executeUpdate();
            System.out.println(row);
            String prepTest2 = "SELECT * FROM PLAYER";
            PreparedStatement preparedStatement1 = conn.prepareStatement(prepTest2);
            ResultSet resultSet = preparedStatement1.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name"));
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    public void disconnect(){
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

