package com.szakdogaServer.DataBase;

import com.szakdogaServer.BusinessLogic.PathFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Set;

public class DB {
    Connection conn;
    private Logger logger;
    public DB() {
        logger = LogManager.getLogger(PathFinder.class);
        try {
            // db parameters
            String url = "jdbc:sqlite:src/main/resources/db/game.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(true);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            logger.error("SQLite hasn't succeded in establishing a connection");
            logger.trace(e.getMessage());
            System.exit(-1);
        }
    }

    public int getPlayerPositionX(int playerCount) {
        logger.info("get player possition");
        String prep = "SELECT X FROM MAPINFO WHERE PLAYER = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(prep);
            preparedStatement.setInt(1,playerCount);
            ResultSet resultSet=preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("X");
        } catch (SQLException e){
            logger.error("an error occured while handling the querry");
            logger.trace(e.getMessage());
        }
        return -1;
    }

    /**
     *
     * @param playerCount indexing start from 1
     * @return
     */
    public int getPlayerPositionY(int playerCount) {
        logger.info("get player possition");
        String prep = "SELECT Y FROM MAPINFO WHERE PLAYER = ?"; //TODO this table doesn't exist
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(prep);
            preparedStatement.setInt(1,playerCount);
            ResultSet resultSet=preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("Y");
        } catch (SQLException e){
            logger.error("an error occured while handling the querry");
            logger.trace(e.getMessage());
        }
        return -1;
    }

    public void addNameToDb(String name,float point,int wins,int losses){
        try {
            String prepTest = "INSERT INTO PLAYER(name,points,wins,losses) values(?,?,?,?);";
            PreparedStatement preparedStatement = conn.prepareStatement(prepTest);
            preparedStatement.setString(1, name);
            preparedStatement.setFloat(2, point);
            preparedStatement.setInt(3, wins);
            preparedStatement.setInt(4, losses);
            int row = preparedStatement.executeUpdate();
        } catch (SQLException e){
            logger.error("an error occured while handling the querry");
            logger.trace(e.getMessage());
        }
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
            String prepTest2 = "SELECT * FROM PLAYER";
            PreparedStatement preparedStatement1 = conn.prepareStatement(prepTest2);
            ResultSet resultSet = preparedStatement1.executeQuery();
        } catch (SQLException e){
            logger.error("an error occured while handling the querry");
            logger.trace(e.getMessage());
        }
    }
    public void disconnect(){
        logger.info("Disconnecting from DB");
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            logger.error("an error occured while disconnecting");
            logger.trace(ex.getMessage());
        }
    }
}

