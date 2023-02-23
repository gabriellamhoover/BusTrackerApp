package com.example.appdev2assign2;

import static android.os.SystemClock.sleep;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.appdev2assign2.databinding.ActivityPredictionsBinding;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PredictionsActivity  extends AppCompatActivity implements View.OnClickListener, IUnityAdsInitializationListener{
    public ArrayList<Prediction> preds;
    PredAdapter pAdapter;
    private ActivityPredictionsBinding binding;
    public String color;
    public Stop s;
    public String rtNum;
    public String name;
    public String stopId;
    public String dir;
    PredictionsDownloader predDownloader;
    private SwipeRefreshLayout swiper;
    RecyclerView pRecycler;
    private static final String bannerPlacement = "Banner_Android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preds = new ArrayList<>();
        binding = ActivityPredictionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showBanner();
        Intent intent = getIntent();
        if (intent.hasExtra(Stop.class.getName())) {
            s = (Stop) intent.getSerializableExtra(Stop.class.getName(), Stop.class);
            this.rtNum = s.rtNum;
            this.color = s.color;
            this.name = s.name;
            this.stopId = s.id;
            setTitle("Route "+ this.rtNum + " - " + s.name);
            this.dir = s.direction;
            updateText();
        }
        pAdapter = new PredAdapter(preds, this);
        pAdapter.notifyItemRangeChanged(0, preds.size());
        predDownloader.getSourceData(this, rtNum,stopId);
        pRecycler = findViewById(R.id.predRecycler);
        binding.predRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.predRecycler.setAdapter(pAdapter);
        int col = Color.parseColor(color);
        if (Color.luminance(col) < 0.25) {
            binding.predNameTextView.setTextColor(Color.WHITE);
        } else {
            binding.predNameTextView.setTextColor(Color.BLACK);
        }
        binding.predNameTextView.setBackgroundColor(col);
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(this::doRefresh);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.bus_icon);
        }

    }
    public void updateData(ArrayList<Prediction> predictions){
        this.preds.clear();
        for(Prediction pred: predictions){
            pred.setColor(this.color);
        }
        this.preds.addAll(predictions);
        pAdapter.notifyItemRangeChanged(0, preds.size());
    }

    private void doRefresh() {
        if (!checkNet()) {
            dialogNoNetwork();
            swiper.setRefreshing(false);
        } else {
            updateText();
            PredictionsDownloader.getSourceData(this, this.rtNum, this.stopId);
            pAdapter.notifyItemRangeChanged(0, preds.size());
            swiper.setRefreshing(false);
        }

    }
    public boolean checkNet() {
        if (hasNetworkConnection())
            return true;
        else
            return false;
    }
    public void updateText(){
        Date d = new Date();
        String time = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(d);
        binding.predNameTextView.setText(s.name + " (" + s.direction + ")\n" + time);
        //sleep(1000);
        //updateText();
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

        builder.setMessage("Unable to contact Bus Tracker API due to network problem. Please check your network connection.");
        builder.setTitle("Bus Tracker - CTA");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void showBanner() {
        RelativeLayout layout = binding.layout;

        BannerView bottomBanner = new BannerView(
                this, bannerPlacement, UnityBannerSize.getDynamicSize(this));

        layout.addView(bottomBanner);
        bottomBanner.load();
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onInitializationComplete() {
        showBanner();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {

    }
}
