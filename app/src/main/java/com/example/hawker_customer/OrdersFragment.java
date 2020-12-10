package com.example.hawker_customer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment {

    private DatabaseHandler databaseHandler;
    private FirebaseAuth auth;
    private FirebaseHandler firebaseHandler;
    private OrdersAdapter adapter;
    private TextView tvStore, tvTable, tvTotal;

    public OrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrdersFragment.
     */
    public static OrdersFragment newInstance() {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        databaseHandler = new DatabaseHandler(getContext());
        firebaseHandler = FirebaseHandler.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        tvStore = view.findViewById(R.id.store_name);
        tvTable = view.findViewById(R.id.table_no);
        tvTotal = view.findViewById(R.id.total);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        Customer customer = databaseHandler.getCustomer(currentUser.getUid());
        tvStore.setText(customer.getStoreName());
        tvTable.setText("Table No: " + customer.getTableNo());

        Query query = firebaseHandler.getOrders(currentUser.getUid());

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query, new SnapshotParser<Order>() {
                    @NonNull
                    @Override
                    public Order parseSnapshot(@NonNull DataSnapshot snapshot) {
                        Order order = snapshot.getValue(Order.class);
                        order.setId(snapshot.getKey());
                        return order;
                    }
                })
                .build();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float total = 0f;

                for (DataSnapshot s : snapshot.getChildren()) {
                    if (s.child("completion").getValue(Integer.class) < 1) {
                        total += s.child("total").getValue(Float.class);
                    }
                }

                tvTotal.setText(String.format("RM%.2f", total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new OrdersAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        adapter.stopListening();
    }
}