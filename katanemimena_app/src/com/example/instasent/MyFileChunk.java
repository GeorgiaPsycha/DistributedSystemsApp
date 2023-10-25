package com.example.instasent;

import java.io.Serializable;
import java.util.Arrays;

public class MyFileChunk implements Serializable {
    private static final long serialVersionUID = -1L;
    private byte[] bytes;
    private final int chunkId;
    private boolean endOfFile;
   // private String fileName;
    private int size;

    public MyFileChunk(byte[] bytes,int id,int size) {
        this.bytes = bytes;
        this.chunkId=id;
        this.size=size;
    }

    public boolean isEndOfFile() {
        return endOfFile;
    }

    public void setEndOfFile(boolean endOfFile) {
        this.endOfFile = endOfFile;
    }

    public int getChunkId(){return chunkId;}



    public int getSize() {
        return size;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "gr.uni.app.common.MyFileChunk{" +
                "bytes=" + Arrays.toString(bytes) +
                ", chunkId=" + chunkId +
                '}';
    }
}
