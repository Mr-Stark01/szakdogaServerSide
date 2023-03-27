package com.szakdogaServer.network;

import com.szakdogaServer.BusinessLogic.ServerLogic;
import org.datatransferobject.DTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class GameClientHandler implements Callable<Integer> {//TODO tesztelés tényleges androidon futó alkalmazás nélkül setupolni valamit
    private final Socket clientSocket;
    private static ObjectOutputStream objectOutputStream;
    private DTO dto;
    private List<DTO> DTOListOut = new ArrayList<>();
    private BlockingQueue<DTO>  blockingQueueOut;
    private BlockingQueue<ArrayList<DTO>> blockingQueueIn;
    private static ObjectInputStream objectInputStream;
    //TODO blocking quet olvasni csak és onnan sendData
    public GameClientHandler(Socket socket, BlockingQueue<DTO> blockingQueueOut,BlockingQueue<ArrayList<DTO>> blockingQueueIn) throws InterruptedException {
        this.clientSocket = socket;
        this.blockingQueueOut = blockingQueueOut;
        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());//Needs to be created first
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer call() {
        while(true){//TODO szétszedni kétfelé
            try {
                receiveData();
                blockingQueueOut.add(dto);
                sendData();
            }
            catch (ClassNotFoundException e){
                //e.printStackTrace();
            }
            catch (IOException e){
                //e.printStackTrace();
            }
        }
    }
    public void receiveData() throws IOException, ClassNotFoundException {//TODO csak deseralizal és berakja blocking queue ba
        DTO dto  = (DTO) objectInputStream.readObject(); //TODO csak adja ki a másik osztálynak + küllön szálra kiteni
    }// TODO id alapján lehet kiolvasni esetleg converter osztály felismeri azt is hogy pontosan milyen adat érkezet és lekezelni
    public void sendData() throws IOException {//TODO csak seralizel és kiolvasa blocking queue ba
        try{
            DTOListOut=blockingQueueIn.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(DTO dto:DTOListOut){
            objectOutputStream.writeObject(dto);
        }
        DTOListOut=null;
        dto = null;
    }
}
