package com.example.feihe.healthcare;

import java.io.Serializable;

/**
 * Created by feihe on 2015/12/7.
 */
public class DataInfo implements Serializable{
    private int id;
    private String name;
    private float data;
    private String time;

    public DataInfo(String name){
        this.name = name;
    }

    public DataInfo(String name, float data, String time){
        this.name = name;
        this.data = data;
        this.time = time;
    }
    public DataInfo(int id, String name, float data, String time){
        this.id = id;
        this.name = name;
        this.data = data;
        this.time = time;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public float getData(){
        return data;
    }
    public void setData(float data){
        this.data=data;
    }
    public String getTime(){
        return time;
    }
}
