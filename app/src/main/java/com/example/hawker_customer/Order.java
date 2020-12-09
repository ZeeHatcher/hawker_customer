package com.example.hawker_customer;

public class Order {
    private String id, hawkerId, itemId, itemName;
    private float total;
    private int itemQty, completion;

    public Order() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getHawkerId() {
        return hawkerId;
    }

    public void setHawkerId(String hawkerId) {
        this.hawkerId = hawkerId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public int getItemQty() {
        return itemQty;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }

    public int getCompletion() {
        return completion;
    }

    public void setCompletion(int completion) {
        this.completion = completion;
    }

    public Order(String id, String hawkerId, String itemId, String itemName, float total, int itemQty, int completion) {
        this.id = id;
        this.hawkerId = hawkerId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.total = total;
        this.itemQty = itemQty;
        this.completion = completion;
    }

}
