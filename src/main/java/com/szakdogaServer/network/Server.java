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
    private final ExecutorService executor = Executors.newFixedThreadPool(20);

    public void start(int port) throws IOException {
        try{
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
            executor.submit(serverLogic);
            Future<Integer> f1=executor.submit(new GameClientHandler(players.get(0),blockingQueueToLogicFromClients,blockingQueueToClientsFromLogic));
            Future<Integer> f2=executor.submit(new GameClientHandler(players.get(1),blockingQueueToLogicFromClients,blockingQueueToClientsFromLogic));
            /*while(!(f1.isDone() && f2.isDone())){
                Thread.sleep(1000);
                System.out.println("f1:"+f1.isDone());
                System.out.println("f2:"+f2.isDone());
            }*/
            f1.get();
            f2.get();
            System.out.println("over");
        }catch (InterruptedException e){
            e.printStackTrace();
            //TODO probably throw exception and that why it finishes
        }
        }
        catch (Exception e) {

        }
        finally {
            stop();
        }
    }
    public void stop() throws IOException {
        System.out.println("Server shutting down.");
        for(Socket socket: players){
            socket.close();
        }
        serverSocket.close();
    }

}
