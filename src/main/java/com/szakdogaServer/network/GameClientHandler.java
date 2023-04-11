package com.szakdogaServer.network;

import com.szakdogaServer.BusinessLogic.ServerLogic;
import org.datatransferobject.DTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.szakdogaServer.BusinessLogic.IdCreator.getNewId;

public class GameClientHandler implements Callable {//TODO tesztelés tényleges androidon futó alkalmazás nélkül setupolni valamit
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
    public Integer call() {
        while(true){//TODO szétszedni kétfelé

                receiveData();
            System.out.println("received");
                blockingQueueOut.offer(dto);
                sendData();
            System.out.println("snet");
        }
    }
    public void receiveData() {//TODO csak deseralizal és berakja blocking queue ba
        try {
            try {
                //clientSocket.setSoTimeout(1000);
                dto = (DTO) objectInputStream.readObject();
                dto.setId(id);
            }
            catch (SocketException e){
                e.printStackTrace();

            }
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }//TODO csak adja ki a másik osztálynak + küllön szálra kiteni
// TODO id alapján lehet kiolvasni esetleg converter osztály felismeri azt is hogy pontosan milyen adat érkezet és lekezelni
    public void sendData() {//TODO csak seralizel és kiolvasa blocking queue ba
        try{
            DTOListOut=blockingQueueIn.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            if (DTOListOut.get(0).getId() == id) {
                ArrayList<DTO> tmp = new ArrayList<>();
                tmp.add(DTOListOut.get(0));
                tmp.add(DTOListOut.get(1));
                objectOutputStream.writeObject(tmp);
                objectOutputStream.flush();
            } else {
                ArrayList<DTO> tmp = new ArrayList<>();
                tmp.add(DTOListOut.get(1));
                tmp.add(DTOListOut.get(0));
                objectOutputStream.writeObject(tmp);
                objectOutputStream.flush();
            }
            objectOutputStream.reset();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        DTOListOut.clear();
        dto = null;
    }
}
