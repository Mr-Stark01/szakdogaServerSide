package com.szakdogaServer.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.szakdogaServer.BusinessLogic.ServerLogic;
import org.datatransferobject.DTO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Server {
    private ServerSocket serverSocket;
    private ServerSocket serverSocket2;
    private ArrayList<Socket> players;
    private ExecutorService executor = Executors.newFixedThreadPool(20);

    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        BlockingQueue<DTO> blockingQueueToLogicFromClients= new LinkedBlockingQueue<>(2);
        BlockingQueue<ArrayList<DTO>> blockingQueueToClientsFromLogic= new LinkedBlockingQueue<>(2);
        ServerLogic serverLogic = new ServerLogic(blockingQueueToLogicFromClients,blockingQueueToClientsFromLogic);


        System.out.println(serverSocket.getLocalSocketAddress());
        players = new ArrayList<>();
        while(players.size()<2){
            players.add(serverSocket.accept());
        }
        System.out.println(players.get(0));
        System.out.println(players.get(1));
        System.out.println("Both player have connected.");
        //Future<Integer> future = executor.submit(new FileClientHandler(players.get(0)));
        //Future<Integer> future2 = executor.submit(new FileClientHandler(players.get(1)));
        //future.isDone();
        //future2.isDone();
        try{
            //executor.submit(serverLogic);
            executor.submit(new GameClientHandler(players.get(0),blockingQueueToLogicFromClients,blockingQueueToClientsFromLogic));
            executor.submit(new GameClientHandler(players.get(1),blockingQueueToLogicFromClients,blockingQueueToClientsFromLogic));
            Thread.sleep(200000);
            System.out.println("over");
        }catch (InterruptedException e){
            e.printStackTrace();
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
