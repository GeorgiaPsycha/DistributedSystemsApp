package com.example.instasent;


import com.example.instasent.ClientInput;
import com.example.instasent.ClientOutput;
import com.example.instasent.Profile;
import com.example.instasent.Topic;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private static Map<Integer, Topic> topic_names = new HashMap();
    private static int topicId = 0;

    public Client() {
    }
//main
    public static void main(String[] args) {
        try {
            readTopics();
            Profile profile = createProfile();
            Socket requestSocket = new Socket("127.0.0.1", profile.getBrokerNumber());
            (new ClientOutput(requestSocket, profile)).start();
            (new ClientInput(requestSocket)).start();
        } catch (IOException | NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        }

    }
//initialized profile of the user
    public static Profile createProfile() throws FileNotFoundException, NoSuchAlgorithmException {
        // find the ip+port hash
        File file = new File("topic_infos.txt");
        Scanner scanner = new Scanner(file);
        int min=1000000000;
        int mid=0;
        int max=-111111111;
        int min_port=0;
        int max_port=0;
        int mid_port=0;
        int i = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (i % 2 != 0) {
                //  hashing MD5
                String value=line+"+127.0.0.1";
                MessageDigest md = MessageDigest.getInstance("MD5");
                // digest() method is called to calculate message digest
                //  of an input digest() return array of byte
                byte[] messageDigest = md.digest(value.getBytes());
                // Convert byte array into signum representation
                BigInteger I = new BigInteger(1, messageDigest);
                //mod 300
                BigInteger three00 = new BigInteger("300");
                int ip_port= I.mod(three00).intValue();
                if(ip_port>max){
                    max=ip_port;
                    max_port =Integer.parseInt(line) ;
                }else if (ip_port<min){
                    min=ip_port;
                    min_port =Integer.parseInt(line) ;
                }else if( ip_port<max && ip_port>min){
                    mid=ip_port;
                    mid_port=Integer.parseInt(line) ;
                }
            }
            i=i+1;
        }
        //
        int port_number = 4328;
        String topic = "initial";
        System.out.print("Give me a Profile Name:");
        Scanner input = new Scanner(System.in);
        String name = input.nextLine();
        System.out.print("Create Topic(1)\nEnter existing Topic(2)\nYour Choice:");
        int choose = input.nextInt();
        Scanner inInt;
        if (choose == 1) {
            ++topicId;
            System.out.print("Give me a Topic Name:");
            inInt = new Scanner(System.in);
            topic = inInt.nextLine();
            int responsible_broker = hash_topic(topic);
            System.out.println(responsible_broker);
            if (responsible_broker==0){
                port_number=min_port;
            }else if (responsible_broker==1){
                port_number=mid_port;
            }else if (responsible_broker==2){
                port_number=max_port;
            }
            writeTopic(topic, port_number);
            topic_names.put(topicId, new Topic(topic, port_number));
        }

        if (choose == 2) {
            System.out.println(topic_names);
            inInt = new Scanner(System.in);
            System.out.println("Give id of the topic u want to enter:");
            readTopics();
            int id = inInt.nextInt();
            topic = topic_names.get(id).getTopic_name();
            port_number = topic_names.get(id).getPortNumber();
        }

        return new Profile(name, port_number, topic);
    }
//read topics from file
    public static void readTopics() throws FileNotFoundException {
        File file = new File("topic_infos.txt");
        Scanner sc = new Scanner(file);
        String topic_name = "noName";
        int portNumber = 4328;
        int i = 0;
        for(boolean act = false; sc.hasNextLine(); ++i) {
            String line = sc.nextLine();
            if (i % 2 == 0) {
                topic_name = line;
            } else {
                portNumber = Integer.parseInt(line);
                act = true;
            }
            if (act) {
                topic_names.put(topicId, new Topic(topic_name, portNumber));
                ++topicId;
                act = false;
            }
        }
    }
//write the new topic on the existing file
    public static void writeTopic(String topic_name, int portNumber) {
        try {
            File file = new File("topic_infos.txt");
            PrintWriter out = new PrintWriter(new FileWriter(file, true));
            out.append("\n").append(topic_name).append("\n").append(String.valueOf(portNumber));
            out.close();
        } catch (IOException var4) {
            System.out.println("could not write to file");
        }

    }


//hast method for hash things
        public static int hash_topic(String name) throws NoSuchAlgorithmException {
            //  hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(name.getBytes());
            // Convert byte array into signum representation
            BigInteger I = new BigInteger(1, messageDigest);
            //mod 3
            BigInteger three = new BigInteger("3");
            int broker_resposnible = I.mod(three).intValue();
            return  broker_resposnible;
        }




    }

