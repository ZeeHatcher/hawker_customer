package com.example.hawker_customer;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements WidgetManager, MaterialToolbar.OnMenuItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_PERMISSIONS = 1;
    private static final String TAG = "Main";
    private static final String KEY_CUSTOMER = "customer";
    private static final int JOB_ID = 0;

    private BottomNavigationView bottomNavigationView;
    private Customer customer;
    private DatabaseHandler handler;
    private FirebaseAuth auth;
    private JobScheduler jobScheduler;
    private Location storeLocation;
    private LocationManager locationManager;
    private MaterialToolbar appBar;
    private ViewPager2 viewPager;
    private FirebaseUser currentUser;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "distance:" + location.distanceTo(storeLocation));
            if (location.distanceTo(storeLocation) > 100) {
                signOut();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d(TAG, "onStatusChanged: " + s);
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d(TAG, "onProviderEnabled: " + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d(TAG, "onProviderDisabled: " + s);
        }
    };

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

        if (getArguments() != null) {
            this.customer = getArguments().getParcelable(KEY_CUSTOMER);
        }

        jobScheduler = (JobScheduler) getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);

        auth = FirebaseAuth.getInstance();
        handler = new DatabaseHandler(getContext());


        currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // Go to login page if not authenticated
            signOut();
            return;
        }

        scheduleJob();
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

        customer = handler.getCustomer(currentUser.getUid());

        if (customer == null) {
            signOut();
            return;
        }

        startLocationCheck();

        bottomNavigationView.setSelectedItemId(R.id.page_orders);
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
                                signOut();
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

    private void signOut() {
        jobScheduler.cancelAll();
        handler.deleteCustomers();
        auth.signOut();
        ((NavigationHost) getContext()).navigateTo(LoginFragment.newInstance(), false);
    }

    private void startLocationCheck() {
        storeLocation = new Location(LocationManager.GPS_PROVIDER);
        storeLocation.setLatitude(customer.getLatitude());
        storeLocation.setLongitude(customer.getLongitude());

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        long minTime = 0;
        float minDistance = 100f; // 100 meters
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
    }

    private void scheduleJob() {
        ComponentName notificationJobService = new ComponentName(getContext().getPackageName(), NotificationJobService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, notificationJobService);
        JobInfo jobInfo = builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        jobScheduler.schedule(jobInfo);
    }
}