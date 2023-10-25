package com.example.instasent;

import com.example.instasent.Broker;
import com.example.instasent.MyFileChunk;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientInput extends Thread{

    private final Socket requestSocket;
    private final ArrayList<MyFileChunk> chunkList = new ArrayList<>();

    public ClientInput(Socket requestSocket) {
        this.requestSocket = requestSocket;
    }

    public void run() {
            try (ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream())) {
                out.flush();
                while (requestSocket.isConnected()) {
                    Object userPayload =in.readObject();
                    pull(userPayload);
                    //reconstruction();
                }

        } catch(IOException | ClassNotFoundException ioException){
            ioException.printStackTrace();
        }
        }
        //pull the incoming message or file
        public  void  pull(Object userPayload){
            if (userPayload instanceof MyFileChunk) {
                MyFileChunk chunk = (MyFileChunk) userPayload;
                chunkList.add(chunk);
                if (chunk.isEndOfFile()) {
                    System.out.println(chunkList);
                }

            }
            if (userPayload instanceof String) {
                String message = (String) userPayload;
                System.out.println(message);
            }
        }
        //save file is not used
        public void reconstruction() throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            for (MyFileChunk chunk:chunkList) {
                outputStream.write(chunk.getBytes());
            }
            byte[] file=outputStream.toByteArray();
            try(final FileOutputStream fileOutputStream = new FileOutputStream("userFiles/" + "test.jpg")){
                    fileOutputStream.write(file);
                  }
        }
    }








