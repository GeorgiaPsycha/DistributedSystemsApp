package com.example.instasent;

import com.example.instasent.MyFileChunk;
import com.example.instasent.Profile;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientOutput extends Thread {

    private final Socket requestSocket;
    private final Profile client;
    public ClientOutput(Socket requestSocket,Profile client)  {
        this.requestSocket = requestSocket;
        this.client=client;

        }

    public void run() {
        System.out.println("Attempting to connect to the server...");
        try (ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream()))
         {
            out.flush();
            out.writeObject(client);
            out.flush();
            ArrayList<MyFileChunk> fileChunkList;
            Scanner scanner = new Scanner(System.in);
            String userMessage;
            System.out.print("Connected you can write messages:"+client.getProfileName()+"\n");
            while (requestSocket.isConnected()) {
                //System.out.print("Write message "+client.getProfileName()+":");
                userMessage = scanner.nextLine();
                //check if the user wants to send a file
                if (userMessage.startsWith("/file=")) {
                    out.writeObject(userMessage);
                    fileChunkList = chopFile(userMessage.substring(6));
                    for (MyFileChunk fileChunk : fileChunkList) {
                        out.writeObject(fileChunk);
                        out.reset();
                        out.flush();}
                } else {
                    // else writes string message and sends to server
                    out.writeObject(userMessage);

                }

            }

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//chop file in chunks in order to send them
    private ArrayList<MyFileChunk> chopFile(String filename) {
        String filePath = "userFiles/" + filename;
        ArrayList<MyFileChunk> chunkList = new ArrayList<>();
        try (final FileInputStream reader = new FileInputStream(filePath);
             final BufferedInputStream bufferedReader = new BufferedInputStream(reader, 512000)) {
            File file = new File(filePath);
            final byte[] fileBytes = new byte[(int) file.length()];
            byte[] chunk = new byte[512000];
            int k = 0;
            int id = 0;
            for (int i = 0; i < fileBytes.length; i++) {
                chunk[k] = (byte) bufferedReader.read();
                k++;
                if (k == 512000&& i!=fileBytes.length-1) {
                    id++;
                    MyFileChunk fileChunk = new MyFileChunk(chunk, id,fileBytes.length);
                    chunkList.add(fileChunk);
                    chunk = new byte[512000];
                    k = 0;
                }
                if(i==fileBytes.length-1){
                    id++;
                    MyFileChunk fileChunk = new MyFileChunk(chunk, id,fileBytes.length);
                    chunkList.add(fileChunk);
                    System.out.println(fileChunk.getBytes().length);
                    System.out.println(id);
                }
            }

            // if no chunks were added because the file is less than 512kb
            if(chunkList.size() == 0){
                id++;
                MyFileChunk fileChunk = new MyFileChunk(chunk, id,fileBytes.length);
                chunkList.add(fileChunk);
            }
            // Get the last chunk and set a flag so that the server knows that the end of file has been reached
            chunkList.get(chunkList.size()-1).setEndOfFile(true);

        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("DWSE SWSTO ONOMA KAKE");
        }
        return chunkList;
    }



}