package com.example.appdev2assign2;

import android.util.Log;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

public class UnityInitializationListener  implements IUnityAdsInitializationListener {
    private final String TAG = getClass().getSimpleName();
    private final MainActivity mainActivity;
    public UnityInitializationListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public void onInitializationComplete() {
        Log.d(TAG, "onInitializationComplete: ");
        if(mainActivity != null) {
            mainActivity.showBanner();
        }
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {
        Log.d(TAG, "onInitializationFailed: ");
    }
}
