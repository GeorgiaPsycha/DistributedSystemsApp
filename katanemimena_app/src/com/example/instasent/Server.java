package com.example.instasent;
import com.example.instasent.Broker;
import com.example.instasent.Profile;
import com.example.instasent.Topic;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private final static List<Broker> brokerList = new ArrayList<>();
    static int port;
    public static void main(String[] args) throws IOException {
        brokerList.add(new Broker(4328));
        brokerList.add(new Broker(4329));
        brokerList.add(new Broker(4330));
        isActive();
        //create new broker

        new Server().openServer(4330);
    }
//in order to rotate between 3 brokers so we can open the process more easily
    public static void isActive() throws IOException {
        File file = new File("Servers.txt");
        Scanner sc = new Scanner(file);
        String line = sc.nextLine();
        FileWriter myWriter = new FileWriter("Servers.txt");
        if(line.equals("1,F")){
            myWriter.write("2,F");
            myWriter.close();
            port=4328;
            System.out.println(line);}
        else if(line.equals("2,F")){
            myWriter.write("3,F");
            myWriter.close();
            port=4329;
            System.out.println(line);
            }
        else if(line.equals("3,F")){
            myWriter.write("1,F");
            myWriter.close();
            port=4330;
            System.out.println(line);
        }
            }

    void openServer(int port) {
//try-with resources
        try (ServerSocket providerSocket = new ServerSocket(port)) {
            /* Create Server Socket */
            System.out.println("Server is up and running");
            while (providerSocket.isBound()) {
                /* Accept the connection */
                System.out.println("Awaiting for new connection...");
                /* Define the socket that is used to handle the connection */
                Socket connection = providerSocket.accept();
                System.out.println("Got a connection!");
                try {
                    ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                    Object userInitialPayload = in.readObject();
                    //initialize user and fine his broker
                    if (userInitialPayload instanceof Profile) {
                        Profile profile = (Profile)userInitialPayload;
                        System.out.println("JUST CONNECTED"+profile);
                        int indexOfBroker = brokerList.indexOf(new Broker(profile.getBrokerNumber()));
                        brokerList.get(indexOfBroker).getProfileList().add(profile);
                        //add new topic in the brokers
                        if (!brokerList.get(indexOfBroker).getTopic_list().contains(new Topic(profile.getTopic()))){
                        brokerList.get(indexOfBroker).Add_topic(profile.getTopic());}
                        Thread t = new ServerThread(connection,brokerList.get(indexOfBroker),profile);
                        t.start();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }
}

	
