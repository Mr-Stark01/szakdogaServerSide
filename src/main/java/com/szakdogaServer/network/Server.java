package com.szakdogaServer.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {
    private ServerSocket serverSocket;
    private ArrayList<Socket> players;
    private ExecutorService executor = Executors.newFixedThreadPool(20);

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        players = new ArrayList<>();
        //System.out.println(serverSocket.getLocalPort());
        while(players.size()<2){
            players.add(serverSocket.accept());
        }
        Future<Integer> future = executor.submit(new FileClientHandler(players.get(0)));
        Future<Integer> future2 = executor.submit(new FileClientHandler(players.get(1)));
        future.isDone(); future2.isDone();
        try{
            executor.submit(new GameClientHandler(players.get(0)));
            executor.submit(new GameClientHandler(players.get(1)));
        }catch (InterruptedException e){

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
