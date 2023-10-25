package com.example.instasent;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread{

    private final Socket connection;
    private final Broker broker;
    private final Profile client;
    private String filename;
    private String message;
    private final ArrayList<MyFileChunk> chunkList = new ArrayList<>();
    static public ArrayList<ServerThread> threads = new ArrayList<>();

    ObjectOutputStream out;
    ObjectInputStream in;
    public ServerThread(Socket connection,Broker broker,Profile client)  {
        this.connection = connection;
        this.broker = broker;
        this.client=client;
        try {
            this.out=new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            this.in=new ObjectInputStream(connection.getInputStream());
            threads.add(this);
        }catch  (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        // try-with-resources
        try {
            /*
             *
             *
             *
             */
           // System.out.println(broker);
            while (connection.isConnected()) {
                //send history to new client
                history();
                //get message from clients
               Object userPayload = in.readObject();
                //push the payload to the clients on the same broker
                push(userPayload);
                //save file if received
                if( !chunkList.isEmpty()&& chunkList.get(chunkList.size()-1).isEndOfFile()){
                reconstruction();}
            }


        } catch  (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    //multicast the message to the other users
    private void multicastToUsers(int code) {
        for (ServerThread th : threads) {
            if(th.client!=this.client&&threads.contains(this)&&th.client.getTopic().equals(this.client.getTopic())){
            if(code==1){
            for (MyFileChunk myFileChunk : chunkList) {
                try {
                    th.out.writeObject(myFileChunk);
                   // System.out.println(myFileChunk.getChunkId()+"send");
                    th.out.flush();
                } catch (IOException e) {
                   e.printStackTrace();
               }
            }}
            else if(code==2){ try {
                th.out.writeObject(client.getProfileName()+":"+message);
                th.out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        }}
        }
        //save file in serverFiles for history purposes
    public void reconstruction() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        for (MyFileChunk chunk:chunkList) {
            outputStream.write(chunk.getBytes());
        }
        byte[] file=outputStream.toByteArray();
        try(final FileOutputStream fileOutputStream = new FileOutputStream("serverFiles/"+broker.getBrokerName()+"/"+filename)){
            fileOutputStream.write(file);
        }
        //clear the file list so we can receive new file after saving this one
        chunkList.clear();
    }
    //push the incoming message
    public void push(Object userPayload){
    if (userPayload instanceof MyFileChunk) {
       MyFileChunk chunk = (MyFileChunk) userPayload;
        chunkList.add(chunk);
        //add the chunk file
        if(chunk.isEndOfFile()){
            multicastToUsers(1);
        }
    } else if (userPayload instanceof String) {
        message = (String) userPayload;
        if(threads.contains(this)){
            Topic topic=broker.getTopic_list().get(broker.getTopic_list().indexOf(new Topic(client.getTopic())));
            topic.getHistory().add(client.getProfileName()+":"+message);}
        if(message.contains("/file="))
        {
            filename=message.substring(6);
        }
        //check if clients is new so we show history
        if(client.isFirstTime()){client.setFirstTime(false);}
        multicastToUsers(2);
        commands(message);
        }
    }
    //show history only if it is first time
    public synchronized void history()  {
        try {
        if(client.isFirstTime()){
            out.writeObject("");
        }
        if (client.isFirstTime() && broker.getTopic_list().get(broker.getTopic_list().indexOf(new Topic(client.getTopic()))).getHistory()!= null) {
            Topic topic = broker.getTopic_list().get(broker.getTopic_list().indexOf(new Topic(client.getTopic())));
            for (String history : topic.getHistory()) {
                if (history.contains("/file=")) {
                    String[] fname = history.split("");
                    String tempFilename = "";
                    boolean tempBool = false;
                    for (String ch : fname) {
                        if (tempBool) {
                            tempFilename = tempFilename.concat(ch);
                        }
                        if (ch.equals("=")) {
                            tempBool = true;
                        }

                    }
                    System.out.println(tempFilename);
                    ArrayList<MyFileChunk> chunkHistory = chopFile(tempFilename);
                    for (MyFileChunk myFileChunk : chunkHistory) {
                        try {
                            out.writeObject(myFileChunk);
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    continue;
                }

                out.writeObject(history);
            }
        }} catch (IOException e) {
            e.printStackTrace();
    }
    }
    //commands the user can use to do stuff
    public void commands(String userPayload){
        //for disconnect
    if (userPayload.equals("/dc")){
        System.out.println(client.getProfileName()+"Disconnected");
        threads.remove(this);
    }
    //for reconnection shows topic in the system
    if (userPayload.startsWith("/con")){
        try{
            out.writeObject("Type /topic: and then the name of the topic");
            for (Topic topic :broker.getTopic_list()){
                out.writeObject(topic.getTopic_name());
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    //in order to enter new topic
    if(userPayload.startsWith("/topic:")&&!threads.contains(this)){
        String newTopic=userPayload.substring(7);
        for(Topic topic:broker.getTopic_list()){
            if (newTopic.equals(topic.getTopic_name())){
                this.client.setTopic(newTopic);
                this.client.setFirstTime(true);
                threads.add(this);
            }else{
                try {
                    out.writeObject("There is not such a topic try again");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    if (userPayload.startsWith("/commands")){
        try {
            out.writeObject("commands u can use /dc , /topic:, /con /commands.");
        }catch (IOException E) {
            E.printStackTrace();
        }
    }
}
//chop file se we can send it for history use
    private ArrayList<MyFileChunk> chopFile(String filename) {
        String filePath = "serverFiles/"+broker.getBrokerName()+"/"+filename;
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
                if (k == 512000) {
                    id++;
                    MyFileChunk fileChunk = new MyFileChunk(chunk, id,fileBytes.length);
                    chunkList.add(fileChunk);
                    chunk = new byte[512000];
                    k = 0;
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
            System.out.println("Give the right file name");
        }
        return chunkList;}


}