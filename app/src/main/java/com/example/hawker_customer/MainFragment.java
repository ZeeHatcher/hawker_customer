package com.example.hawker_customer;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements WidgetManager, MaterialToolbar.OnMenuItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private MaterialToolbar appBar;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        appBar = view.findViewById(R.id.top_app_bar);
        viewPager = view.findViewById(R.id.view_pager);

        PagerAdapter pagerAdapter = new PagerAdapter(getActivity());
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(pagerAdapter);

        appBar.setOnMenuItemClickListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        bottomNavigationView.setSelectedItemId(R.id.page_orders);

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // Go to login page if not authenticated
            ((NavigationHost) getContext()).navigateTo(LoginFragment.newInstance(), false);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.logout)
                        .setMessage(R.string.logout_message)
                        .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                auth.signOut();
                                ((NavigationHost) getContext()).navigateTo(LoginFragment.newInstance(), false);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();

                break;
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        setAppBarTitle(item.getTitle());

        switch (item.getItemId()) {
            case R.id.page_orders:
                viewPager.setCurrentItem(0);
                return true;

            case R.id.page_menu:
                viewPager.setCurrentItem(1);
                return true;
        }

        return false;
    }

    public void setAppBarTitle(CharSequence title) {
        appBar.setTitle(title);
    }
}