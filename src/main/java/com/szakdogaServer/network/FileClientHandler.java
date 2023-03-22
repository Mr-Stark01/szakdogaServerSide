package com.szakdogaServer.network;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class FileClientHandler implements Callable<Integer> {
    private final Socket clientSocket;
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;


    public FileClientHandler(Socket socket){
        this.clientSocket=socket;
        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private static void sendFile(String path) throws IOException {
        int bytes;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        dataOutputStream.writeLong(file.length());

        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("New Client connected.");
        try {
            sendFile("src/assets/Base.tmx");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataInputStream.close();
            dataOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
