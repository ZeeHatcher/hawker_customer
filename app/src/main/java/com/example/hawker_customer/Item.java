package com.example.hawker_customer;

public class Item {

    private String id, name, imagePath, hawkerId, hawkerName;
    private float price;
    private int currentStock;

    public Item() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getHawkerId() {
        return hawkerId;
    }

    public void setHawkerId(String hawkerId) {
        this.hawkerId = hawkerId;
    }

    public String getHawkerName() {
        return hawkerName;
    }

    public void setHawkerName(String hawkerName) {
        this.hawkerName = hawkerName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", hawkerId='" + hawkerId + '\'' +
                ", storeName='" + hawkerName + '\'' +
                ", price=" + price +
                ", currentStock=" + currentStock +
                '}';
    }

}
