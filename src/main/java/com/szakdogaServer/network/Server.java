package com.szakdogaServer.network;

import com.badlogic.gdx.Gdx;
import com.szakdogaServer.BusinessLogic.ServerLogic;
import com.szakdogaServer.DataBase.DB;
import org.apache.logging.log4j.LogManager;
import org.datatransferobject.DTO;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Server {
    private ServerSocket serverSocket;
    private ArrayList<Socket> players;
    private final ExecutorService executor = Executors.newFixedThreadPool(20);
    private Logger logger;
    private DB db= new DB();
    private CyclicBarrier barrier;
    public Server(){
        logger = LogManager.getLogger(Server.class);
    }

    public void start(int port){
        try{
        serverSocket = new ServerSocket(port);
        logger.info("Server socket created");
        BlockingQueue<DTO> blockingQueueToLogicFromClients= new LinkedBlockingQueue<>(2);
        BlockingQueue<ArrayList<DTO>> blockingQueueToClientsFromLogic= new LinkedBlockingQueue<>(2);
        barrier=new CyclicBarrier(2);
        ServerLogic serverLogic = new ServerLogic(blockingQueueToLogicFromClients,blockingQueueToClientsFromLogic,db);
        System.out.println(serverSocket.getLocalSocketAddress());
        players = new ArrayList<>();
        while(players.size()<2){
            players.add(serverSocket.accept());
        }
        logger.info("Both player have connected");
        //Future<Integer> future = executor.submit(new FileClientHandler(players.get(0)));
        //Future<Integer> future2 = executor.submit(new FileClientHandler(players.get(1)));
        //future.isDone();
        //future2.isDone();
        try{
            executor.submit(serverLogic);
            Future<Integer> f1=executor.submit(new GameClientHandler(players.get(0),blockingQueueToLogicFromClients,blockingQueueToClientsFromLogic,barrier));
            Future<Integer> f2=executor.submit(new GameClientHandler(players.get(1),blockingQueueToLogicFromClients,blockingQueueToClientsFromLogic,barrier));
            logger.info("Awaiting end of threads");
            f1.get();
            f2.get();
            logger.info("Both thread finished");
            executor.shutdown();
        }catch (InterruptedException e){
            logger.error("One thread throw a unexpected exceptions shutting down");
            logger.trace(e.getMessage());
            stop();
            //TODO probably throw exception and that why it finishes
        }
        }
        catch (Exception e) {
            logger.error("An error occured on one of the threads");
            logger.trace(e.getMessage());
            System.exit(-1);
        }
        finally {
            stop();
        }
    }
    public void stop() {
        System.out.println("Server shutting down.");
        for(Socket socket: players){
            try {
                socket.close();
            }
            catch (IOException e){
                logger.error("IO exception occured probably already closed socket");
                logger.trace(e.getMessage());
            }
        }
        try {
            serverSocket.close();
        }
        catch (IOException e){
            logger.error("IO exception occured probably already closed socket");
            logger.trace(e.getMessage());
        }
        finally {
            db.disconnect();
            Gdx.app.exit();
        }
    }

}
