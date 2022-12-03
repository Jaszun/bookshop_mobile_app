package com.example.zaliczeniesklep.database_entity;

import androidx.annotation.Nullable;

public class CartItem extends DatabaseEntity{
    private int user_id;
    private int product_id;
    private int quantity;

    public CartItem(){}

    public CartItem(int user_id, int product_id, int quantity) {
        this.user_id = user_id;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public CartItem(int id, int user_id, int product_id, int quantity) {
        super(id);
        this.user_id = user_id;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemForOrder(){
        return product_id + ":" + quantity;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof CartItem)){
            return false;
        }

        else {
            if (((CartItem) obj).user_id == this.user_id && ((CartItem) obj).product_id == this.product_id){
                return true;
            }

            return false;
        }
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "user_id=" + user_id +
                ", product_id=" + product_id +
                ", quantity=" + quantity +
                '}';
    }
}