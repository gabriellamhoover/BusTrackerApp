package com.example.appdev2assign2;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdev2assign2.databinding.ActivityMainBinding;
import com.example.appdev2assign2.databinding.ActivityStopBinding;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class StopActivity extends AppCompatActivity implements View.OnClickListener{
    private Route r;
    public String direction;
    public String number;
    public ArrayList<Stop> stops;
    StopAdapter sAdapter;
    private ActivityStopBinding binding;
    public String color;
    Location currLocation;
    public Double lat;
    public Double lon;
    public RecyclerView stopRecycler;
    static SimpleDateFormat timeFormat;
    private View selectedView;
    private static final String bannerPlacement = "Banner_Android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        if (intent.hasExtra(Route.class.getName())) {
            r = (Route) intent.getSerializableExtra(Route.class.getName(), Route.class);
            this.direction = r.direction;
            this.number = r.number;
            this.color = r.color;
            setTitle("Route "+ r.number + " - " + r.name);
            binding.stopNameTextView.setText(r.direction + " Stops");
        }
        if(intent.hasExtra(Location.class.getName())){
            currLocation= (Location) intent.getParcelableExtra(Location.class.getName(), Location.class);
            this.lat = currLocation.getLatitude();
            this.lon = currLocation.getLongitude();
        }
        int col = Color.parseColor(color);
        if (Color.luminance(col) < 0.25) {
            binding.stopNameTextView.setTextColor(Color.WHITE);
        } else {
            binding.stopNameTextView.setTextColor(Color.BLACK);
        }
        binding.stopNameTextView.setBackgroundColor(col);
        this.stops = new ArrayList<>();
        sAdapter = new StopAdapter(stops, this);
        sAdapter.notifyItemRangeChanged(0, stops.size());
        timeFormat = new SimpleDateFormat("MM-dd-yyyy-HH:mm", Locale.getDefault());
        StopDownloader.getSourceData(this, number, direction);
        stopRecycler = findViewById(R.id.sRecycler);
        binding.sRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.sRecycler.setAdapter(sAdapter);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.bus_icon);
        }
        showBanner();
        //bannerListener = new com.example.appdev2assign2.BannerViewListener(this);

        // Initialize the SDK:

}
    public void updateData(ArrayList<Stop> stops){
        for(Stop stop : stops){
            stop.setColor(this.color);
            Location stopLoc = new Location(currLocation);
            stopLoc.setLatitude(stop.lat);
            stopLoc.setLongitude(stop.lon);
            Float dist = currLocation.distanceTo(stopLoc);
            if(dist <= 1000) {
                stop.setDist(dist);
                stop.setRtNum(this.number);
                stop.setDirection(this.direction);
                this.stops.add(stop);
            }
        }
        Collections.sort(this.stops);
        sAdapter.notifyItemRangeChanged(0, stops.size());
    }

    @Override
    public void onClick(View v) {
        selectedView = v;
        int pos = stopRecycler.getChildLayoutPosition(v);
        Stop s = stops.get(pos);
        Intent intent = new Intent(StopActivity.this, PredictionsActivity.class);
        intent.putExtra(Stop.class.getName(), s);
        startActivity(intent);
    }
    public void showBanner() {
        RelativeLayout layout = binding.layout;

        BannerView bottomBanner = new BannerView(
                this, bannerPlacement, UnityBannerSize.getDynamicSize(this));

        layout.addView(bottomBanner);
        bottomBanner.load();
    }

}
