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

import static com.szakdogaServer.BusinessLogic.IdCreator.getNewId;

public class GameClientHandler implements Runnable {//TODO tesztelés tényleges androidon futó alkalmazás nélkül setupolni valamit
    private final Socket clientSocket;
    private ObjectOutputStream objectOutputStream;
    private DTO dto;
    private List<DTO> DTOListOut = new ArrayList<>();
    private BlockingQueue<DTO>  blockingQueueOut;
    private BlockingQueue<ArrayList<DTO>> blockingQueueIn;
    private ObjectInputStream objectInputStream;
    private int id = getNewId();
    //TODO blocking quet olvasni csak és onnan sendData
    public GameClientHandler(Socket socket, BlockingQueue<DTO> blockingQueueOut,BlockingQueue<ArrayList<DTO>> blockingQueueIn) throws InterruptedException {
        this.clientSocket = socket;
        this.blockingQueueOut = blockingQueueOut;
        this.blockingQueueIn=blockingQueueIn;
        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());//Needs to be created first
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){//TODO szétszedni kétfelé
            try {
                receiveData();
                blockingQueueOut.offer(dto);
                sendData();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void receiveData() {//TODO csak deseralizal és berakja blocking queue ba
        synchronized (objectInputStream) {
            System.out.println("receive1");
            try {
                 dto = (DTO) objectInputStream.readObject();
            }
            catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
            System.out.println("receive2");

        }//TODO csak adja ki a másik osztálynak + küllön szálra kiteni
    }// TODO id alapján lehet kiolvasni esetleg converter osztály felismeri azt is hogy pontosan milyen adat érkezet és lekezelni
    public void sendData() throws IOException {//TODO csak seralizel és kiolvasa blocking queue ba
        synchronized (objectOutputStream) {
            try{
                DTOListOut=blockingQueueIn.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("send1");
            synchronized (DTOListOut) {
                if (DTOListOut.get(0).getId() == id) {
                    objectOutputStream.writeObject(DTOListOut.get(0));
                    objectOutputStream.flush();
                    objectOutputStream.writeObject(DTOListOut.get(1));
                }
                else {
                    objectOutputStream.writeObject(DTOListOut.get(1));
                    objectOutputStream.flush();
                    objectOutputStream.writeObject(DTOListOut.get(0));
                }
            }
            System.out.println("send2");
            objectOutputStream.flush();
            DTOListOut.clear();
            dto = null;
        }
    }
}
