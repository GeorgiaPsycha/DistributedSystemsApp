package com.example.instasent;



import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Profile implements Serializable
{
    private static final long serialVersionUID = -1L;
    private String profileName;
    private int brokerNumber;
    private boolean firstTime;
    private String topic;


    public Profile(String profileName, int broker,String topic) {
        this.profileName = profileName;
        this.brokerNumber=broker;
        this.topic=topic;
        this.firstTime=true;

    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getBrokerNumber() {
        return brokerNumber;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setBrokerNumber(int brokerNumber) {
        this.brokerNumber = brokerNumber;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }
//public Socket getConnection() {return connection;}

    //public void setConnection(Socket connection) {this.connection = connection;}

    @Override
    public String toString() {
        return "Profile{" +
                "profileName='" + profileName + '\'' +
                ", brokerNumber=" + brokerNumber +
                ", topic='" + topic + '\'' +
                '}';
    }
}
