package com.example.hawker_customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Setup";
    private static final int REQUEST_PLACES = 1;

    private DatabaseHandler handler;
    private FirebaseAuth auth;
    private Place place;
    private TextInputEditText etStore, etTable;
    private TextInputLayout tlStore, tlTable;

    public SetupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SetupFragment.
     */
    public static SetupFragment newInstance() {
        SetupFragment fragment = new SetupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        handler = new DatabaseHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup, container, false);

        Button btConfirm = view.findViewById(R.id.button_confirm);
        etStore = view.findViewById(R.id.edit_text_store);
        etTable = view.findViewById(R.id.edit_text_table);
        tlStore = view.findViewById(R.id.text_layout_store);
        tlTable = view.findViewById(R.id.text_layout_table);

        btConfirm.setOnClickListener(this);
        etStore.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_confirm:
                if (!validate()) return;

                auth.signInAnonymously()
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = authResult.getUser();

                                Customer customer = new Customer(
                                        authResult.getUser().getUid(),
                                        place.getId(),
                                        etTable.getText().toString(),
                                        place.getLatLng().latitude,
                                        place.getLatLng().longitude
                                );

                                handler.setCustomer(customer);

                                ((NavigationHost) getContext()).navigateTo(MainFragment.newInstance(), false);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "signInAnonymously:fail", e);
                            }
                        });

                break;

            case R.id.edit_text_store:
                Intent intent = new Intent(getActivity(), PlacesActivity.class);
                startActivityForResult(intent, REQUEST_PLACES);

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PLACES && resultCode == Activity.RESULT_OK) {
            place = data.getExtras().getParcelable(PlacesActivity.KEY_PLACE);

            etStore.setText(place.getName());
        }
    }

    private boolean validate() {
        boolean isValid = true;

        tlStore.setError(null);
        tlTable.setError(null);

        if (etTable.getText().toString().isEmpty()) {
            tlTable.setError(getString(R.string.error_table));
            isValid = false;
        }

        if (place == null) {
            tlStore.setError(getString(R.string.error_place));
            isValid = false;
        }

        return isValid;
    }
}