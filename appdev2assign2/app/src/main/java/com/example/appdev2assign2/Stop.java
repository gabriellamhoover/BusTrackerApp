package com.example.appdev2assign2;

import java.io.Serializable;

public class Stop implements Serializable, Comparable<Stop> {
    public String id;
    public String name;

    public double lat;
    public double lon;
    public String direction;
    public String color;
    public float dist;
    public String rtNum;

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setRtNum(String rtNum) {
        this.rtNum = rtNum;
    }

    public void setDist(float dist) {
        this.dist = dist;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Stop(String id, String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.id = id;
    }

    @Override
    public int compareTo(Stop o) {
       if(this.dist > o.dist){
           return 1;
       }
       else if(this. dist < o.dist){
           return -1;
       }
       else{
           return 0;
       }
    }
}
