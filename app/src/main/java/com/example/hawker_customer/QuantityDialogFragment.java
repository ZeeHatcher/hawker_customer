package com.example.hawker_customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class QuantityDialogFragment extends DialogFragment {

    private static final String TAG = "QuantityDialog";

    private Customer customer;
    private FirebaseHandler firebaseHandler = FirebaseHandler.getInstance();
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
        picker.setWrapSelectorWheel(false);

        return builder.setView(view)
                .setTitle(item.getName())
                .setMessage(R.string.message_order)
                .setPositiveButton(R.string.place_order, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int qty = picker.getValue();

                        Map<String, Object> docData = new HashMap<>();
                        docData.put("customerId", customer.getUid());
                        docData.put("hawkerId", item.getHawkerId());
                        docData.put("itemId", item.getId());
                        docData.put("itemName", item.getName());
                        docData.put("tableNo", customer.getTableNo());
                        docData.put("itemQty", qty);
                        docData.put("total", qty * item.getPrice());
                        docData.put("completion", -1);

                        firebaseHandler.addOrder(docData);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}
