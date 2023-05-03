package com.szakdogaServer.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.*;
import java.net.Socket;
import java.util.concurrent.Callable;

public class FileClientHandler implements Callable<Integer> {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private final Socket clientSocket;
    private static Logger logger = LogManager.getLogger(FileClientHandler.class);


    public FileClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {

        }
    }

    private static void sendFile(String path) throws IOException {
        int bytes;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        dataOutputStream.writeLong(file.length());

        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }

    @Override
    public Integer call() {
        System.out.println("New Client connected.");
        try {
            sendFile("src/main/resources/maps/defmap.tmx");
        } catch (IOException e) {
            System.err.println("There was an error with sending map");
            e.printStackTrace();
            logger.trace(e.getMessage());
            System.exit(-1);
        }
        return 0;
    }
}
