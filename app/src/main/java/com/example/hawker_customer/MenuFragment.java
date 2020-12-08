package com.example.hawker_customer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {

    private static final String TAG = "Menu";
    private Customer customer;
    private DatabaseHandler databaseHandler;
    private FirebaseAuth auth;
    private FirestoreHandler firestoreHandler;
    private ItemsAdapter adapter;
    private RecyclerView recyclerView;
    private ListenerRegistration registration;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MenuFragment.
     */
    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        databaseHandler = new DatabaseHandler(getContext());
        firestoreHandler = FirestoreHandler.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        customer = databaseHandler.getCustomer(auth.getCurrentUser().getUid());

        registration = firestoreHandler.getHawkers(customer.getStoreId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "getHawkers:fail", error);
                            return;
                        }

                        if (value != null) {
                            ArrayList<String> hawkerIds = new ArrayList<>();

                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                boolean isOpen = snapshot.get("isOpen", Boolean.class);

                                if (isOpen) hawkerIds.add(snapshot.getId());
                            }

                            Log.d(TAG, "getHawkers:" + hawkerIds.toString());

                            if (!hawkerIds.isEmpty()) {
                                Query query = firestoreHandler.getHawkerItems(hawkerIds);

                                FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                                        .setQuery(query, new SnapshotParser<Item>() {
                                            @NonNull
                                            @Override
                                            public Item parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                                Item item = snapshot.toObject(Item.class);
                                                item.setId(snapshot.getId());

                                                return item;
                                            }
                                        })
                                        .build();

                                adapter = new ItemsAdapter(options);
                                recyclerView.setAdapter(adapter);

                                adapter.startListening();
                            }
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) adapter.stopListening();
        registration.remove();
    }
}