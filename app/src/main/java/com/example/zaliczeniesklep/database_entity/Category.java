package com.example.zaliczeniesklep.database_entity;

public class Category extends DatabaseEntity{
    private String name;
    private int drawableImageId;

    public Category(){}

    public Category(String name, int drawableImageId) {
        this.name = name;
        this.drawableImageId = drawableImageId;
    }

    public Category(int id, String name, int drawableImageId) {
        super(id);
        this.name = name;
        this.drawableImageId = drawableImageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawableImageId() {
        return drawableImageId;
    }

    public void setDrawableImageId(int drawableImageId) {
        this.drawableImageId = drawableImageId;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + super.getId() + '\'' +
                "name='" + name + '\'' +
                ", drawableImageId=" + drawableImageId +
                '}';
    }
}
