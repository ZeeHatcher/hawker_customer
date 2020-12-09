package com.example.hawker_customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class QuantityDialogFragment extends DialogFragment {

    private static final String TAG = "QuantityDialog";

    private Customer customer;
    private FirebaseHandler firebaseHandler = FirebaseHandler.getInstance();
    private FirestoreHandler firestoreHandler = FirestoreHandler.getInstance();
    private Item item;

    public QuantityDialogFragment(Item item, Customer customer) {
        this.item = item;
        this.customer = customer;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.dialog_quantity, null);
        final NumberPicker picker = view.findViewById(R.id.number_picker);
        picker.setMaxValue(item.getCurrentStock());
        picker.setMinValue(1);
        picker.setWrapSelectorWheel(false);

        return builder.setView(view)
                .setTitle(item.getName())
                .setMessage(R.string.message_order)
                .setPositiveButton(R.string.place_order, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final int qty = picker.getValue();

                        final long now = Calendar.getInstance().getTimeInMillis();

                        final Map<String, Object> docData = new HashMap<>();
                        docData.put("customerId", customer.getUid());
                        docData.put("hawkerId", item.getHawkerId());
                        docData.put("itemId", item.getId());
                        docData.put("itemName", item.getName());
                        docData.put("tableNo", customer.getTableNo());
                        docData.put("itemQty", qty);
                        docData.put("total", qty * item.getPrice());
                        docData.put("completion", -1);
                        docData.put("orderTime", now);

                        firebaseHandler.getHawkerOrders(item.getHawkerId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        long max = now;
                                        for (DataSnapshot s : snapshot.getChildren()) {
                                            if (s.child("completion").getValue(Integer.class) == -1) {
                                                max = Long.max(max, s.child("eta").getValue(Long.class));
                                            }
                                        }

                                        docData.put("eta", max + (item.getPrepTime() * 60000 * qty));

                                        firebaseHandler.addOrder(docData);
                                        firestoreHandler.incrementItemQuantity(item.getHawkerId(), item.getId(), qty * -1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}
