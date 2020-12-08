package com.example.hawker_customer;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class FirestoreHandler {

    private static FirestoreHandler instance;

    private static final String COL_HAWKERS = "hawkers";
    private static final String COL_ITEMS = "items";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirestoreHandler getInstance() {
        if (instance == null) {
            instance = new FirestoreHandler();
        }

        return instance;
    }

    private FirestoreHandler() {
    }

    public Query getHawkers(String storeId) {
        return db.collection(COL_HAWKERS)
                .whereEqualTo("storeId", storeId);
    }

    public Query getHawkerItems(List<String> hawkerIds) {
        return db.collectionGroup(COL_ITEMS)
                .whereIn("hawkerId", hawkerIds);
    }

}
