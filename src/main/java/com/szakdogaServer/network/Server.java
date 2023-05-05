package com.szakdogaServer.network;

import com.badlogic.gdx.Gdx;
import com.szakdogaServer.businessLogic.ServerLogic;
import com.szakdogaServer.dataBase.DB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datatransferobject.DTO;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Server {
    private static Logger logger;
    private final int PARTIES = 2;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private ServerSocket serverSocket;
    private ServerSocket fileServerSocket;
    private ArrayList<Socket> players;
    private ArrayList<Socket> fileSockets;
    private DB db = DB.getInstance();
    private CyclicBarrier barrier = new CyclicBarrier(PARTIES);
    private BlockingQueue<DTO> blockingQueueToLogicFromClients = new LinkedBlockingQueue<>(PARTIES);
    private BlockingQueue<ArrayList<DTO>> blockingQueueToClientsFromLogic = new LinkedBlockingQueue<>(PARTIES);

    public Server() {
        logger = LogManager.getLogger(Server.class);
    }

    public void start(int port) {
        try {
            /*fileServerSocket = new ServerSocket(port);
            fileSockets = new ArrayList<>();
            while (fileSockets.size() < PARTIES) {
                fileSockets.add(fileServerSocket.accept());
            }
            try {
            Future<Integer> f1 = executor.submit(new FileClientHandler(fileSockets.get(0)));
                f1.get();
            Future<Integer> f2 = executor.submit(new FileClientHandler(fileSockets.get(1)));
                f2.get();
            }catch (ExecutionException|InterruptedException e){
                logger.error("One thread throw a unexpected exceptions shutting down");
                logger.trace(e.getMessage());
            }
            fileServerSocket.close();
            */
            try {
                serverSocket = new ServerSocket(port);
            }catch (BindException e){
                serverSocket = new ServerSocket(0);
                System.out.println("Given port wasn't avaible other port was used");
                System.out.println(serverSocket.getLocalPort());
            }
            logger.info("Server socket created");
            ServerLogic serverLogic = new ServerLogic(blockingQueueToLogicFromClients, blockingQueueToClientsFromLogic, db);
            players = new ArrayList<>();
            while (players.size() < PARTIES) {
                players.add(serverSocket.accept());
            }
            logger.info("Both player have connected");
            try {
                executor.submit(serverLogic);
                Future<Integer> f3 = executor.submit(new GameClientHandler(players.get(0), blockingQueueToLogicFromClients, blockingQueueToClientsFromLogic, barrier));
                Future<Integer> f4 = executor.submit(new GameClientHandler(players.get(1), blockingQueueToLogicFromClients, blockingQueueToClientsFromLogic, barrier));
                logger.info("Awaiting end of threads");
                f3.get();
                f4.get();
                logger.info("Both thread finished");
                serverSocket.close();
                executor.shutdownNow();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("One thread throw a unexpected exceptions shutting down");
                logger.trace(e.getMessage());
                stop();
            }
        } catch (IOException e) {
            logger.error("An error occured on one of the threads");
            logger.trace(e.getMessage());
            e.printStackTrace();
            stop();
            System.exit(-1);
        }
    }

    public void stop() {
        System.out.println("Server shutting down.");
        for (Socket socket : players) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("IO exception occured probably already closed socket");
                logger.trace(e.getMessage());
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("IO exception occured probably already closed socket");
            logger.trace(e.getMessage());
        } finally {
            db.disconnect();
            Gdx.app.exit();
        }
    }

}
