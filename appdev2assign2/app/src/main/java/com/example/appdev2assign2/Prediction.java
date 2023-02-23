package com.example.appdev2assign2;


public class Prediction {
    public String vid;
    public String dir;
    public String des;
    public String predtime;
    public boolean delay;
    public String pred;
    public String color;

    public void setColor(String color) {
        this.color = color;
    }

    public Prediction(String vid, String dir, String des, String predtime, boolean delay, String pred) {
        this.vid = vid;
        this.dir = dir;
        this.des = des;
        this.predtime = predtime;
        this.delay = delay;
        this.pred = pred;
    }
}
