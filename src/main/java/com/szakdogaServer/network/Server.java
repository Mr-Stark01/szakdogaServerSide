package com.szakdogaServer.network;

import com.badlogic.gdx.Gdx;
import com.szakdogaServer.BusinessLogic.ServerLogic;
import com.szakdogaServer.DataBase.DB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datatransferobject.DTO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Server {
    private static Logger logger;
    private final int PARTIES = 2;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private ServerSocket serverSocket;
    private ArrayList<Socket> players;
    private DB db = new DB();
    private CyclicBarrier barrier = new CyclicBarrier(PARTIES);
    private BlockingQueue<DTO> blockingQueueToLogicFromClients = new LinkedBlockingQueue<>(PARTIES);
    private BlockingQueue<ArrayList<DTO>> blockingQueueToClientsFromLogic = new LinkedBlockingQueue<>(PARTIES);

    public Server() {
        logger = LogManager.getLogger(Server.class);
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Server socket created");
            ServerLogic serverLogic = new ServerLogic(blockingQueueToLogicFromClients, blockingQueueToClientsFromLogic, db);
            players = new ArrayList<>();
            while (players.size() < PARTIES) {
                players.add(serverSocket.accept());
            }
            logger.info("Both player have connected");
            try {
                executor.submit(serverLogic);
                Future<Integer> f1 = executor.submit(new GameClientHandler(players.get(0), blockingQueueToLogicFromClients, blockingQueueToClientsFromLogic, barrier));
                Future<Integer> f2 = executor.submit(new GameClientHandler(players.get(1), blockingQueueToLogicFromClients, blockingQueueToClientsFromLogic, barrier));
                logger.info("Awaiting end of threads");
                f1.get();
                f2.get();
                logger.info("Both thread finished");
                executor.shutdownNow();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("One thread throw a unexpected exceptions shutting down");
                logger.trace(e.getMessage());
                stop();
            }
        } catch (IOException e) {
            logger.error("An error occured on one of the threads");
            logger.trace(e.getMessage());
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
