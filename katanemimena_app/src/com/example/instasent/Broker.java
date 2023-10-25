package com.example.instasent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Broker {
    private Integer portNumber;
    private List<Profile> profileList = new ArrayList<>();
    private String brokerName;
    public  List<Topic> topic_list=new ArrayList<>();

//initialize brokers with some topics and names
    public Broker(Integer portNumber) throws FileNotFoundException {
        this.portNumber = portNumber;
        if (portNumber==4328){
            this.brokerName="broker1";
            readTopics(portNumber);
        }
        else if(portNumber==4329){
            this.brokerName="broker2";
            readTopics(portNumber);

        }else{
            this.brokerName="broker3";
            readTopics(portNumber);
        }
    }
    public void readTopics(int portNumber) throws FileNotFoundException {
        File file = new File("topic_infos.txt");
        Scanner sc = new Scanner(file);
        String topic_name = "noName";
        int port =0;
        int i = 0;
        for(boolean act = false; sc.hasNextLine(); ++i) {
            String line = sc.nextLine();
            if (i % 2 == 0) {
                topic_name = line;
            } else {
                port = Integer.parseInt(line);
                act = true;
            }
            if (act&&port==portNumber) {
                this.topic_list.add( new Topic(topic_name));
                act = false;
            }
        }
    }
    public List<Topic> getTopic_list() {
        return topic_list;
    }

    public void setTopic_list(List<Topic> topic_list) {
        this.topic_list = topic_list;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    public List<Profile> getProfileList() {
        return profileList;
    }

    public void  Add_topic(String topic){ this.topic_list.add(new Topic(topic)); }


    public void setProfileList(List<Profile> profileList) {
        this.profileList = profileList;
    }

    public String getBrokerName() {
        return brokerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Broker broker = (Broker) o;
        return portNumber.equals(broker.portNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portNumber);
    }

    @Override
    public String toString() {
        return "Broker{" +
                "portNumber=" + portNumber +
                ", profileList=" + profileList +
                '}';
    }
}
