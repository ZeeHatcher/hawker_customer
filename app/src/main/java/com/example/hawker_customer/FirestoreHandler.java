package com.example.hawker_customer;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class FirestoreHandler {

    private static FirestoreHandler instance;

    private static final String COL_HAWKERS = "hawkers";
    private static final String COL_ITEMS = "items";
    private static final String KEY_CURRENT_STOCK = "currentStock";

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

    public Task<Void> incrementItemQuantity(String hawkerId, String itemId, int qty) {
        return db.collection(COL_HAWKERS)
                .document(hawkerId)
                .collection(COL_ITEMS)
                .document(itemId)
                .update(KEY_CURRENT_STOCK, FieldValue.increment(qty));
    }

}
