package com.example.zaliczeniesklep.database_entity;

public class Product extends DatabaseEntity{
    private String name;
    private int category_id;
    private String author;
    private float price;
    private int count;
    private String tags;
    private int drawableImageId;

    public Product(){}

    public Product(String name, int category_id, String author, float price, int count, String tags, int drawableImageId) {
        this.name = name;
        this.category_id = category_id;
        this.author = author;
        this.price = price;
        this.count = count;
        this.tags = tags;
        this.drawableImageId = drawableImageId;
    }

    public Product(int id, String name, int category_id, String author, float price, int count, String tags, int drawableImageId) {
        super(id);
        this.name = name;
        this.category_id = category_id;
        this.author = author;
        this.price = price;
        this.count = count;
        this.tags = tags;
        this.drawableImageId = drawableImageId;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getDrawableImageId() {
        return drawableImageId;
    }

    public void setDrawableImageId(int drawableImageId) {
        this.drawableImageId = drawableImageId;
    }

    public String[] getTagsArray(){
        return tags.split(":");
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + super.getId() + '\'' +
                "name='" + name + '\'' +
                ", category_id=" + category_id +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", count=" + count +
                ", tags='" + tags + '\'' +
                ", drawableImageId=" + drawableImageId +
                '}';
    }
}
