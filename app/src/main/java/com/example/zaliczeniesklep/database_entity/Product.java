package com.example.zaliczeniesklep.database_entity;

import android.util.Log;

import java.util.List;

public class Product extends DatabaseEntity{
    private String name;
    private int category_id;
    private String author;
    private float price;
    private int quantity;
    private String tags;
    private String image;

    public Product(){}

    public Product(String name, int category_id, String author, float price, int quantity, String tags, String image) {
        this.name = name;
        this.category_id = category_id;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
        this.tags = tags;
        this.image = image;
    }

    public Product(String name, int category_id, String author, float price, int quantity, String tags, int image) {
        this.name = name;
        this.category_id = category_id;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
        this.tags = tags;
        this.image = String.valueOf(image);
    }

    public Product(int id, String name, int category_id, String author, float price, int quantity, String tags, String image) {
        super(id);
        this.name = name;
        this.category_id = category_id;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
        this.tags = tags;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String[] getTagsArray(){
        return tags.split(":");
    }

    public static String convertListToTags(List<String> tagsList){
        String tags = "";
        for (String s : tagsList){
            tags += s + ":";
        }

        tags = tags.substring(0, tags.length() - 1);

        Log.i("123321123", tags);

        return tags;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + super.getId() + '\'' +
                "name='" + name + '\'' +
                ", category_id=" + category_id +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", tags='" + tags + '\'' +
                ", drawableImageId=" + image +
                '}';
    }
}
