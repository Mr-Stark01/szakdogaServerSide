package com.szakdogaServer.network;

import com.szakdogaServer.network.DTO.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;

public class GameClientHandler implements Callable<Integer> {//TODO tesztelés tényleges androidon futó alkalmazás nélkül setupolni valamit
    private final Socket clientSocket;
    private static ObjectOutputStream objectOutputStream = null;
    private static ObjectInputStream objectInputStream = null;
    //TODO blocking quet olvasni csak és onnan sendData
    public GameClientHandler(Socket socket) throws InterruptedException {
        this.clientSocket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());//Needs to be created first
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("hereateleast");
        while(true){//TODO szétszedni kétfelé
            receiveData();
            sendData();
        }
    }
    public ArrayList receiveData() throws IOException, ClassNotFoundException {//TODO csak deseralizal és berakja blocking queue ba
        Date dto  = (Date) objectInputStream.readObject();
        System.out.println("Client:\t"+dto.getTime());
        System.out.println("This pc:\t"+new Date().getTime());
        //DTO dto2 = (DTO) objectInputStream.readObject();//TODO csak adja ki a másik osztálynak + küllön szálra kiteni
        return null;
    }// TODO id alapján lehet kiolvasni esetleg converter osztály felismeri azt is hogy pontosan milyen adat érkezet és lekezelni
    public void sendData() throws IOException {//TODO csak seralizel és kiolvasa blocking queue ba
        objectOutputStream.writeObject(new Date()); // TODO same here
    }
    public DTO getDto(){
        return null;
    }
}
