package com.example.appdev2assign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.appdev2assign2.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    private static final String unityGameID = "5154761";
    private static final boolean testMode = true;
    private static final String bannerPlacement = "Banner_Android";
    private BannerView.IListener bannerListener;
    private RecyclerView recyclerView; // Layout's recyclerview
    private RouteAdapter mAdapter; // Data to recyclerview adapter
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    public ArrayList<Route> RouteList;
    public ArrayList<Route> allRouteList;
    private ActivityMainBinding binding;
    public Location loc;
    public Double lat;
    public Double lon;
    public HashMap<String, ArrayList<String>> routeDirections;

    public static SimpleDateFormat timeFormat;
    private View selectedView;
    public static int nightModeFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Bus Tracker - CTA");
        nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        this.RouteList = new ArrayList<>();
        this.allRouteList = new ArrayList<>();
        this.routeDirections = new HashMap<>();
        timeFormat =
                new SimpleDateFormat("MM-dd-yyyy-HH:mm", Locale.getDefault());
        recyclerView = findViewById(R.id.recycler);

        mAdapter = new RouteAdapter(RouteList, this);

        mAdapter.notifyItemRangeChanged(0, RouteList.size());
        RouteList.clear();
        if(checkNet()){
            RouteDownloader.getSourceData(this);
        }
        else{
            dialogNoNetwork();
        }

        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        determineLocation();
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(mAdapter);


        binding.listFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<Route> temp = new ArrayList<>();
                for (Route r : allRouteList) {
                    if (r.name.toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            r.number.toLowerCase().contains(charSequence.toString().toLowerCase()))
                        temp.add(r);
                }
                int size = RouteList.size();
                MainActivity.this.RouteList.clear();
                mAdapter.notifyItemRangeRemoved(0, size);

                MainActivity.this.RouteList.addAll(temp);
                mAdapter.notifyItemRangeChanged(0, RouteList.size());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.bus_icon);
        }
        bannerListener = new com.example.appdev2assign2.BannerViewListener(this);

        // Initialize the SDK:
        UnityAds.initialize(this, unityGameID, testMode,
                new UnityInitializationListener(this));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    private void determineLocation() {
        // Check perm - if not then start the  request and return
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some situations this can be null.
                    if (location != null) {
                        loc = location;
                        lat = loc.getLatitude();
                        lon = loc.getLongitude();
                    }
                })
                .addOnFailureListener(this, e ->
                        dialogNoLocation()
                                );
    }

    public void clearSearch(View v) {
        binding.listFilter.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                }
                else{
                    dialogNoLocation();
                }
            }
        }
    }
    public void showBanner() {
        RelativeLayout layout = binding.layout;

        BannerView bottomBanner = new BannerView(
                this, bannerPlacement, UnityBannerSize.getDynamicSize(this));
        bottomBanner.setListener(bannerListener);

        layout.addView(bottomBanner);
        bottomBanner.load();
    }

    public void updateData(ArrayList<Route> routes) {
        allRouteList.clear();
        allRouteList.addAll(routes);
        RouteList.clear();
        RouteList.addAll(routes);
        mAdapter.notifyItemRangeChanged(0, RouteList.size());
    }
    public void buildPopup(String number, ArrayList<String> directions) {
        PopupMenu popupMenu = new PopupMenu(this, selectedView);
        this.routeDirections.put(number, directions);
            for (int i = 0; i < directions.size(); i++) {
                popupMenu.getMenu().add(Menu.NONE, i + 1, Menu.NONE, directions.get(i));
            }
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int pos = recyclerView.getChildLayoutPosition(selectedView);
                Route r = RouteList.get(pos);
                String dir = (String) menuItem.getTitle();
                r.setDirection(dir);
                Intent intent = new Intent(MainActivity.this, StopActivity.class);
                intent.putExtra(Route.class.getName(), r);
                if(this.loc != null) {
                    intent.putExtra(Location.class.getName(), loc);
                    startActivity(intent);
                    return true;
                }
                dialogNoLocation();
                return false;
            });

        popupMenu.show();

    }


    @Override
    public void onClick(View v) {
        selectedView = v;

        int pos = recyclerView.getChildLayoutPosition(v);
        Route r= RouteList.get(pos);
        if(this.routeDirections.get(r.number) == null) {
                DirectionDownloader.getSourceData(this, r.number);
        }
        else{
            buildPopup(r.number, this.routeDirections.get(r.number));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            if (item.getItemId() == R.id.about) {
                dialogAttribute();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkNet() {
        if (hasNetworkConnection())
            return true;
        else
            return false;
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            connectivityManager = getSystemService(ConnectivityManager.class);
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
    public void dialogNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("OK", (dialog, id) -> {});
        builder.setMessage("Unable to contact Bus Tracker API due to network problem. Please check your network connection.");
        builder.setTitle("Bus Tracker - CTA");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogNoLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("OK", (dialog, id) -> {});

        builder.setMessage("Unable to determine device location. Please allow this app to access device location.");
        builder.setTitle("Bus Tracker - CTA");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogAttribute() {
        final TextView message = new TextView(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SpannableString s = new SpannableString("https://www.transitchicago.com/developers/bustracker/");
        Linkify.addLinks(s, Linkify.WEB_URLS);
        message.setText(s);
        message.setGravity(Gravity.CENTER);
        message.setTextColor(nightModeFlags == Configuration.UI_MODE_NIGHT_YES
                ? Color.WHITE : Color.BLUE);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        builder.setMessage("CTA Bus Tracker data provided by Chicago Transit Authority \n" );
        builder.setTitle("Bus Tracker - CTA");
        builder.setView(message);
        builder.setIcon(nightModeFlags == Configuration.UI_MODE_NIGHT_YES
                ? R.drawable.bus_icon : R.drawable.bus_icon_black);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}