package com.example.appdev2assign2;

import android.content.SharedPreferences;
import android.net.ParseException;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class DirectionDownloader {
    private static final String TAG = "RouteDownload";
    private static final String routeURL = "http://www.ctabustracker.com/bustime/api/v2/getdirections?key=";

    private static final String yourAPIKey = "rMyPptA3R5Re7F2pnfV3haQhQ";
    private static SharedPreferences.Editor ctaCachedDataEditor;
    private static boolean usedCache = false;
    public static ArrayList<String> dirList = new ArrayList<>();
    private static String rtNum;

    //
    //////////////////////////////////////////////////////////////////////////////////

    public static void getSourceData(MainActivity mainActivity, String routeNum) {
        rtNum = routeNum;
        SharedPreferences ctaCachedData = mainActivity.getApplicationContext().getSharedPreferences("CTA_PREFS", 0);
        ctaCachedDataEditor = ctaCachedData.edit();

        String cachedTime = ctaCachedData.getString("TIME" + routeNum, null);
        if (cachedTime != null) {
            try {
                Date dataTime = MainActivity.timeFormat.parse(cachedTime);
                long delta = 0;
                if (dataTime != null) {
                    delta = new Date().getTime() - dataTime.getTime();
                }
                //long lifetime = 8640;
                long lifetime = 86400000;
                if (delta < lifetime) {
                    String cachedData = ctaCachedData.getString("DIR_DATA" + routeNum, null);
                    if (cachedData != null) {
                        try {
                            usedCache = true;
                            handleResults(mainActivity, cachedData);
                            return;
                        } catch (Exception e) {
                            Log.d(TAG, "downloadRoutes: " + e.getMessage());
                        }
                    }
                }

            } catch (ParseException | java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        RequestQueue queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(routeURL + yourAPIKey + "&format=json" + "&rt=" + routeNum).buildUpon();
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> handleResults(mainActivity, response.toString());

        Response.ErrorListener error = error1 -> {
            Log.d(TAG, "getSourceData: ");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getSourceData: " + jsonObject);
                handleResults(mainActivity, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void handleResults(MainActivity mainActivity, String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            return;
        } try {
            JSONObject jObjMain = new JSONObject(s);
            JSONObject bustime = jObjMain.getJSONObject("bustime-response");
            JSONArray directions = bustime.getJSONArray("directions");
            dirList.clear();
            for (int k = 0; k < directions.length(); k++) {
                JSONObject obj = directions.getJSONObject(k);
                String dir = obj.getString("dir");
                dirList.add(dir);
            }

            if (!usedCache) {
                String formattedDate = MainActivity.timeFormat.format(new Date());

                ctaCachedDataEditor.putString("TIME" + rtNum, formattedDate);
                ctaCachedDataEditor.putString("DIR_DATA" + rtNum, jObjMain.toString());
                ctaCachedDataEditor.apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

        if (dirList != null)
            mainActivity.runOnUiThread(() ->mainActivity.buildPopup(rtNum, dirList));

    }
}
