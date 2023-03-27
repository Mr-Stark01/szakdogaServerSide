package com.szakdogaServer.network;

import com.szakdogaServer.BusinessLogic.ServerLogic;
import org.datatransferobject.DTO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Server {
    private ServerSocket serverSocket;
    private ArrayList<Socket> players;
    private ExecutorService executor = Executors.newFixedThreadPool(20);

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        BlockingQueue<DTO> blockingQueueToLogicFromClients= new LinkedBlockingQueue<>(2);
        BlockingQueue<ArrayList<DTO>> blockingQueueToClientsFromLogic= new LinkedBlockingQueue<>(2);
        ServerLogic serverLogic = new ServerLogic(blockingQueueToLogicFromClients,blockingQueueToClientsFromLogic);


        System.out.println(serverSocket.getLocalSocketAddress());
        players = new ArrayList<>();
        while(players.size()<1){
            System.out.println(players.size());
            players.add(serverSocket.accept());
        }
        System.out.println("Both player have connected.");
        //Future<Integer> future = executor.submit(new FileClientHandler(players.get(0)));
        //Future<Integer> future2 = executor.submit(new FileClientHandler(players.get(1)));
        //future.isDone();
        //future2.isDone();
        try{
            Future<Integer> future = executor.submit(new GameClientHandler(players.get(0),blockingQueueToLogicFromClients,blockingQueueToClientsFromLogic));
            //Future<Integer> future2 = executor.submit(new GameClientHandler(players.get(1)));
            future.get();
            System.out.println("over");
        }catch (InterruptedException | ExecutionException e){
            //TODO probably throw exception and that why it finishes
        }

        stop();
    }
    public void stop() throws IOException {
        System.out.println("Server shutting down.");
        for(Socket socket: players){
            socket.close();
        }
        serverSocket.close();
    }

}
