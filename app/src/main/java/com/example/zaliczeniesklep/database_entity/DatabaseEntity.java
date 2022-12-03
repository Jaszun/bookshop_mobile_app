package com.example.zaliczeniesklep.database_entity;

public abstract class DatabaseEntity {
    private int id;

    public DatabaseEntity(){}

    public DatabaseEntity(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DatabaseEntity{" +
                "id=" + id +
                '}';
    }
}
