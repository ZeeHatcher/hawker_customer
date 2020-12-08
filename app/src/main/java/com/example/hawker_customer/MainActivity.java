package com.example.hawker_customer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements NavigationHost, ItemsAdapter.OnItemClickListener {

    private static String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigateTo(LoginFragment.newInstance(), false);
    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void pop() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onItemClick(Item item, Customer customer) {
        QuantityDialogFragment dialog = new QuantityDialogFragment(item, customer);
        dialog.show(getSupportFragmentManager(), "quantity");
    }
}