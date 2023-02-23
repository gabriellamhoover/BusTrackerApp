package com.example.appdev2assign2;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Route implements Comparable<Route>, Serializable {
    public String number;
    public String name;
    public String color;
    public ArrayList<String> directions;
    public String direction;

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Route(String number, String name, String color) {
        this.number = number;
        this.name = name;
        this.color = color;
    }

    @Override
    public int compareTo(Route route) {
        return number.compareTo(route.number);
    }

    @NonNull
    @Override
    public String toString() {
        return "Route{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
