package com.example.appdev2assign2;

import android.util.Log;

import com.example.appdev2assign2.MainActivity;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;

public class BannerViewListener implements BannerView.IListener {

    private final String TAG = getClass().getSimpleName();
    private final MainActivity mainActivity;


    public BannerViewListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onBannerLoaded(BannerView bannerView) {
        Log.d(TAG, "onBannerLoaded: ");
    }

    @Override
    public void onBannerClick(BannerView bannerView) {
        Log.d(TAG, "onBannerClick: ");
    }

    @Override
    public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
        Log.d(TAG, "onBannerFailedToLoad: ");
    }

    @Override
    public void onBannerLeftApplication(BannerView bannerView) {
        Log.d(TAG, "onBannerLeftApplication: ");
    }
}
