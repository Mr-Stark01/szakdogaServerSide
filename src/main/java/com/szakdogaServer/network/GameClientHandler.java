package com.szakdogaServer.network;

import com.szakdogaServer.BusinessLogic.ServerLogic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

public class GameClientHandler implements Callable {
    private final Socket clientSocket;
    private ObjectOutputStream objectOutputStream;
    private DTO dto;
    private List<DTO> DTOListOut = new ArrayList<>();
    private BlockingQueue<DTO>  blockingQueueOut;
    private BlockingQueue<ArrayList<DTO>> blockingQueueIn;
    private ObjectInputStream objectInputStream;
    private int id = getNewId();
    private Logger logger;
    public GameClientHandler(Socket socket, BlockingQueue<DTO> blockingQueueOut,BlockingQueue<ArrayList<DTO>> blockingQueueIn) throws InterruptedException {
        this.clientSocket = socket;
        this.blockingQueueOut = blockingQueueOut;
        this.blockingQueueIn=blockingQueueIn;
        logger = LogManager.getLogger(GameClientHandler.class);
        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());//Needs to be created first
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            logger.error("Creating Streams for the clientSocket throws error probably closed connection");
            logger.trace(e.getMessage());
        }
    }

    @Override
    public Integer call() {
        while(true){
            try {
                logger.info("Awaiting to received data");
                receiveData();
                logger.info("Data received");
                blockingQueueOut.offer(dto);
                sendData();
                logger.info("Data sent");
            }
            catch (InterruptedException | IOException | ClassNotFoundException e){
                logger.trace(e.getMessage());
                return -1;
            }
        }
    }
    public void receiveData() throws IOException, ClassNotFoundException {
        dto = (DTO) objectInputStream.readObject();
        dto.setId(id);

    }
    public void sendData() throws InterruptedException, IOException {
        DTOListOut=blockingQueueIn.take();
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
        objectOutputStream.flush();
        DTOListOut.clear();
        dto = null;
    }
}
