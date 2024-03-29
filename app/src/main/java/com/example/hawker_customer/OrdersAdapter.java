package com.example.hawker_customer;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class OrdersAdapter extends FirebaseRecyclerAdapter<Order, OrdersAdapter.OrderViewHolder> {

    private static final String TAG = "OrdersAdapter";
    private FirebaseHandler firebaseHandler;
    private FirestoreHandler firestoreHandler;
    private Context context;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public OrdersAdapter(@NonNull FirebaseRecyclerOptions<Order> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final OrderViewHolder holder, int position, @NonNull final Order model) {
        holder.tvItem.setText(model.getItemName());
        holder.tvQty.setText(String.valueOf(model.getItemQty()));
        holder.tvPrice.setText(String.format("%.2f", model.getTotal()));

        holder.itemView.setOnLongClickListener(null);
        holder.tvEta.setText("-");

        switch (model.getCompletion()) {
            case -1:
                holder.tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary));

                final Calendar eta = Calendar.getInstance();
                eta.setTimeInMillis(model.getEta());
                holder.tvEta.setText(String.format(
                        "%d:%02d %s",
                        eta.get(Calendar.HOUR),
                        eta.get(Calendar.MINUTE),
                        eta.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault())
                ));

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Log.d(TAG, "order click: " + model.toString());

                        new MaterialAlertDialogBuilder(context)
                                .setTitle(R.string.cancel_order)
                                .setMessage(R.string.message_cancel_order)
                                .setPositiveButton(R.string.cancel_it, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        firebaseHandler.deleteOrder(model.getId());
                                        firestoreHandler.incrementItemQuantity(model.getHawkerId(), model.getItemId(), model.getItemQty());
                                    }
                                })
                                .setNegativeButton(R.string.back, null)
                                .show();

                        return true;
                    }
                });

                break;

            case 0:
                holder.tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorPositive));
                break;

            case 1:
                holder.tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorNegative));
                break;
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseHandler = FirebaseHandler.getInstance();
        firestoreHandler = FirestoreHandler.getInstance();
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_order, parent, false);

        return new OrderViewHolder(view);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItem, tvEta, tvQty, tvPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItem = itemView.findViewById(R.id.item);
            tvEta = itemView.findViewById(R.id.eta);
            tvQty = itemView.findViewById(R.id.qty);
            tvPrice = itemView.findViewById(R.id.price);
        }
    }
}
