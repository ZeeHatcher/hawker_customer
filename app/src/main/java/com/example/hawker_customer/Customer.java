package com.example.hawker_customer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;

public class Customer implements Parcelable {

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

    protected Customer(Parcel in) {
        uid = in.readString();
        storeId = in.readString();
        tableNo = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(storeId);
        parcel.writeString(tableNo);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
