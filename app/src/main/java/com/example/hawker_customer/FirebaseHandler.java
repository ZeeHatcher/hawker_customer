package com.example.hawker_customer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Map;

public class FirebaseHandler {

    public static final FirebaseHandler instance = new FirebaseHandler();

    private static final String KEY_ORDERS = "orders";
    private static final String KEY_CUSTOMER_ID = "customerId";
    private static final String KEY_COMPLETION = "completion";

    public static FirebaseHandler getInstance() {
        return instance;
    }

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    public FirebaseHandler() {
    }

    public Query getOrders(String uid) {
        return db.getReference()
                .child(KEY_ORDERS)
                .orderByChild(KEY_CUSTOMER_ID)
                .equalTo(uid);
    }

    public void addOrder(Map<String, Object> data) {
        DatabaseReference reference = db.getReference().child(KEY_ORDERS);

        String key = reference.push().getKey();
        reference.child(key)
                .setValue(data);
    }

    public void deleteOrder(String orderId) {
        db.getReference()
                .child(KEY_ORDERS)
                .child(orderId)
                .removeValue();
    }
}
