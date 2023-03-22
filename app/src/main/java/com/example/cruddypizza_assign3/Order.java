package com.example.cruddypizza_assign3;

public class Order {
    int id;
    String name;
    int size;
    int topp1;
    int topp2;
    int topp3;
    String date;

    Order(int id, String name, int size, int topp1, int topp2, int topp3, String date){
        this.id = id;
        this.name = name;
        this.size = size;
        this.topp1 = topp1;
        this.topp2 = topp2;
        this.topp3 = topp3;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getTopp1() {
        return topp1;
    }

    public int getTopp2() {
        return topp2;
    }

    public int getTopp3() {
        return topp3;
    }

    public String getDate() {
        return date;
    }
}
