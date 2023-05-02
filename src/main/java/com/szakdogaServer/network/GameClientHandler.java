package com.szakdogaServer.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datatransferobject.DTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

import static com.szakdogaServer.BusinessLogic.IdCreator.getNewId;

public class GameClientHandler implements Callable<Integer> {
    private final int PLAYER_ONE = 0;
    private final int PLAYER_TWO = 1;
    private final Socket clientSocket;
    private final int id = getNewId();
    private ObjectOutputStream objectOutputStream;
    private DTO dto;
    private List<DTO> DTOListOut = new ArrayList<>();
    private BlockingQueue<DTO> blockingQueueOut;
    private BlockingQueue<ArrayList<DTO>> blockingQueueIn;
    private ObjectInputStream objectInputStream;
    private Logger logger;
    private boolean setup = true;
    private boolean finished = false;
    private CyclicBarrier barrier;

    public GameClientHandler(Socket socket, BlockingQueue<DTO> blockingQueueOut, BlockingQueue<ArrayList<DTO>> blockingQueueIn, CyclicBarrier barrier) throws InterruptedException {
        this.barrier = barrier;
        this.clientSocket = socket;
        this.blockingQueueOut = blockingQueueOut;
        this.blockingQueueIn = blockingQueueIn;
        logger = LogManager.getLogger(GameClientHandler.class);
        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());//Needs to be created first
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            logger.error("Creating Streams for the clientSocket throws error probably closed connection");
            logger.trace(e.getMessage());
        }
        logger.info("Socket Handler created");
    }

    @Override
    public Integer call() {
        while (true) {
            try {
                logger.info("Awaiting to received data");
                receiveData();
                barrier.await();
                logger.info("Data received");
                blockingQueueOut.offer(dto);
                sendData();
                barrier.await();
                logger.info("Data sent");
                if (finished) {
                    return 0;
                }
            } catch (InterruptedException | BrokenBarrierException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
                logger.trace(e.getMessage());
                System.exit(-1);
            }
        }
    }

    public void receiveData() throws IOException, ClassNotFoundException {
        logger.debug("waiting for message");
        dto = (DTO) objectInputStream.readObject();
        if (setup) {
            dto.setId(id);
            setup = false;
        }
        logger.debug("message received");
    }

    public void sendData() throws InterruptedException, IOException {
        DTOListOut = blockingQueueIn.take();
        if (DTOListOut.get(PLAYER_ONE).getId() == id) {
            ArrayList<DTO> tmp = new ArrayList<>();
            logger.info("Server Data sent:1" + DTOListOut.get(PLAYER_ONE).toString() + "\nData 2:" + DTOListOut.get(PLAYER_TWO).toString());
            tmp.add(DTOListOut.get(PLAYER_ONE));
            tmp.add(DTOListOut.get(PLAYER_TWO));
            objectOutputStream.writeObject(tmp);
            objectOutputStream.flush();
        } else {
            ArrayList<DTO> tmp = new ArrayList<>();
            logger.info("Server Data sent:1" + DTOListOut.get(PLAYER_TWO).toString() + "\nData 2:" + DTOListOut.get(PLAYER_ONE).toString());
            tmp.add(DTOListOut.get(PLAYER_TWO));
            tmp.add(DTOListOut.get(PLAYER_ONE));
            objectOutputStream.writeObject(tmp);
            objectOutputStream.flush();
        }
        finished = DTOListOut.get(PLAYER_ONE).getPlayerDTO().getHealth() <= 0 || DTOListOut.get(PLAYER_TWO).getPlayerDTO().getHealth() <= 0;
        objectOutputStream.flush();
        DTOListOut.clear();
        dto = null;
    }
}
