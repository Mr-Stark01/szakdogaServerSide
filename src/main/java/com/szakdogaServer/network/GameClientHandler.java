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

public class GameClientHandler implements Callable<Integer> {//TODO tesztelés tényleges androidon futó alkalmazás nélkül setupolni valamit
    private final Socket clientSocket;
    private static ObjectOutputStream objectOutputStream;
    private DTO dto;
    private List<DTO> DTOListOut = new ArrayList<>();
    private BlockingQueue<DTO>  blockingQueueOut;
    private BlockingQueue<ArrayList<DTO>> blockingQueueIn;
    private static ObjectInputStream objectInputStream;
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
    public Integer call() {
        while(true){//TODO szétszedni kétfelé
            try {
                System.out.println("here");
                receiveData();
                blockingQueueOut.offer(dto);
                System.out.println("here");
                sendData();
            }
            catch (ClassNotFoundException e){
                e.printStackTrace();
                //throw new RuntimeException("Game Client");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void receiveData() throws IOException, ClassNotFoundException {//TODO csak deseralizal és berakja blocking queue ba
        synchronized (objectInputStream) {
            dto = (DTO) objectInputStream.readObject();//TODO csak adja ki a másik osztálynak + küllön szálra kiteni
        }
        System.out.println("received data");
    }// TODO id alapján lehet kiolvasni esetleg converter osztály felismeri azt is hogy pontosan milyen adat érkezet és lekezelni
    public void sendData() throws IOException {//TODO csak seralizel és kiolvasa blocking queue ba
        System.out.println("inside send data");
        try{
            DTOListOut=new ArrayList<>(blockingQueueIn.take());
            System.out.println("Dtolistout size:"+DTOListOut.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Dtolistout size:"+DTOListOut.size());
        System.out.println("inside send data after take");
        if(DTOListOut.get(0).getId()==id){
            objectOutputStream.writeObject(DTOListOut.get(0));
            objectOutputStream.writeObject(DTOListOut.get(1));
        }
        else{
            objectOutputStream.writeObject(DTOListOut.get(1));
            objectOutputStream.writeObject(DTOListOut.get(0));

        }
        System.out.println("data sent");
        DTOListOut.clear();
        dto = null;
    }
}
