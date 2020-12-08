package com.example.hawker_customer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ItemsAdapter extends FirestoreRecyclerAdapter<Item, ItemsAdapter.ItemViewHolder> {

    private static final String TAG = "ItemsAdapter";

    private Context context;
    private Customer customer;
    private FirebaseStorage storage;

    public interface OnItemClickListener {
        void onItemClick(Item item, Customer customer);
    }

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ItemsAdapter(@NonNull FirestoreRecyclerOptions<Item> options, Context context) {
        super(options);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseHandler handler = new DatabaseHandler(context);
        customer = handler.getCustomer(auth.getCurrentUser().getUid());

        storage = FirebaseStorage.getInstance();
        this.context = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        if (model.getCurrentStock() > 0) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick:" + model.toString());

                    ((OnItemClickListener) context).onItemClick(model, customer);
                }
            });
        } else {
            holder.currentStock.setTextColor(ContextCompat.getColor(context, R.color.colorNegative));
        }
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
