package com.example.zaliczeniesklep.database_entity;

import android.util.Log;

public class Order extends DatabaseEntity{
    private String date;
    private int userId;
    private String buyer;
    private String items;
    private float price;
    private String email;
    private String phoneNumber;
    private int isExecuted;

    public Order(){}

    public Order(String date, int userId, String buyer, String items, float price, String email, String phoneNumber) {
        this.date = date;
        this.userId = userId;
        this.buyer = buyer;
        this.items = items;
        this.price = price;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isExecuted = 0;
    }

    public Order(int id, String date, int userId, String buyer, String items, float price, String email, String phoneNumber, int isExecuted) {
        super(id);
        this.date = date;
        this.userId = userId;
        this.buyer = buyer;
        this.items = items;
        this.price = price;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isExecuted = isExecuted;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getIsExecuted() {
        return isExecuted;
    }

    public void setIsExecuted(int isExecuted) {
        this.isExecuted = isExecuted;
    }

    public int[][] decodeItems(){
        String[] itemsAsList = items.split("&");

        int[][] decodedItems = new int[itemsAsList.length][];

        // decodedItems[i][0] => item id
        // decodedItems[i][1] => quantity

        for (int i = 0; i < itemsAsList.length; i++){
            String[] splitted = itemsAsList[i].split(":");

            decodedItems[i] = new int[]{Integer.valueOf(splitted[0]), Integer.valueOf(splitted[1])};
        }

        return decodedItems;
    }

    @Override
    public String toString() {
        return "Order{" +
                "date=" + date +
                ", userId=" + userId +
                ", buyer='" + buyer + '\'' +
                ", items='" + items + '\'' +
                ", price=" + price +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isExecuted=" + isExecuted +
                '}';
    }
}
