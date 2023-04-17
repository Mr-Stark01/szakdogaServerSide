package com.szakdogaServer.DataBase;

import java.sql.*;
import java.util.Set;

public class DB {
    Connection conn;
    public DB() {
        try {
            // db parameters
            String url = "jdbc:sqlite:src/main/resources/db/game.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(true);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getPlayerPositionX(int playerCount) {
        String prep = "SELECT X FROM MAPINFO WHERE PLAYER = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(prep);
            preparedStatement.setInt(1,playerCount);
            ResultSet resultSet=preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("X");
        } catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    /**
     *
     * @param playerCount indexing start from 1
     * @return
     */
    public int getPlayerPositionY(int playerCount) {
        String prep = "SELECT Y FROM MAPINFO WHERE PLAYER = ?"; //TODO this table doesn't exist
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(prep);
            preparedStatement.setInt(1,playerCount);
            ResultSet resultSet=preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("Y");
        } catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    private void test(){
        try {
            String prepTest = "INSERT INTO PLAYER(name,points,wins,losses) values(?,?,?,?);";
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

