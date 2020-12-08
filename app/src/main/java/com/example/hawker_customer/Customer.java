package com.example.hawker_customer;

public class Customer {

    private String uid, storeId, tableNo;
    private double latitude, longitude;

    public Customer() {
    }

    public Customer(String uid, String storeId, String tableNo, double latitude, double longitude) {
        this.uid = uid;
        this.storeId = storeId;
        this.tableNo = tableNo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "uid='" + uid + '\'' +
                ", storeId='" + storeId + '\'' +
                ", tableNo='" + tableNo + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
