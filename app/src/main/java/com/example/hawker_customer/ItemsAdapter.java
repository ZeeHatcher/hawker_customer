package com.example.hawker_customer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ItemsAdapter extends FirestoreRecyclerAdapter<Item, ItemsAdapter.ItemViewHolder> {

    private static final String TAG = "ItemsAdapter";

    private Context context;
    private FirebaseStorage storage;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ItemsAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        storage = FirebaseStorage.getInstance();
        context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull final Item model) {
        holder.name.setText(model.getName());
        holder.hawkerName.setText(model.getHawkerName());
        holder.price.setText(String.format("RM%.2f", model.getPrice()));
        holder.currentStock.setText("Current Stock: " + model.getCurrentStock());

        StorageReference storageReference = storage.getReference().child(model.getImagePath());
        GlideApp.with(context)
                .load(storageReference)
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:" + model.toString());
            }
        });
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, hawkerName, price, currentStock;
        public ImageView image;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            hawkerName = itemView.findViewById(R.id.hawker_name);
            price = itemView.findViewById(R.id.price);
            currentStock = itemView.findViewById(R.id.current_stock);
            image = itemView.findViewById(R.id.image);
        }
    }
}
