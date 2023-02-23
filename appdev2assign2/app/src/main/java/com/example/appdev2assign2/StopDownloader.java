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

public class StopDownloader {
    private static final String TAG = "StopDownload";
    private static final String routeURL = "http://www.ctabustracker.com/bustime/api/v2/getstops?key=";

    private static final String yourAPIKey = "rMyPptA3R5Re7F2pnfV3haQhQ";
    private static SharedPreferences.Editor ctaCachedDataEditor;
    private static boolean usedCache = false;
    private static String sRoute;
    private static String sDir;
    public static ArrayList<Stop> stopList = new ArrayList<>();
    //
    //////////////////////////////////////////////////////////////////////////////////

    public static void getSourceData(StopActivity activity, String route, String direction) {
        sRoute = route;
        sDir = direction;
        SharedPreferences ctaCachedData = activity.getApplicationContext().getSharedPreferences("CTA_STOP_PREFS", 0);
        ctaCachedDataEditor = ctaCachedData.edit();
            String cachedTime = ctaCachedData.getString("STOP_TIME" + route + direction, null);
            if (cachedTime != null) {
                try {
                    Date dataTime = StopActivity.timeFormat.parse(cachedTime);
                    long delta = 0;
                    if (dataTime != null) {
                        delta = new Date().getTime() - dataTime.getTime();
                    }
                    //long lifetime = 8640;
                    long lifetime = 86400000;
                    if (delta < lifetime) {
                        String cachedData = ctaCachedData.getString(route + direction, null);
                        if (cachedData != null) {
                            try {
                                usedCache = true;
                                handleResults(activity, cachedData);
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
        RequestQueue queue = Volley.newRequestQueue(activity);

        Uri.Builder buildURL = Uri.parse(routeURL + yourAPIKey + "&format=json&rt=" + route + "&dir=" + direction).buildUpon();
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> handleResults(activity, response.toString());

        Response.ErrorListener error = error1 -> {
            Log.d(TAG, "getSourceData: ");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getSourceData: " + jsonObject);
                handleResults(activity, null);
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

    private static void handleResults(StopActivity activity, String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            //mainActivity.downloadFailed();
            return;
        }
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONObject bustime = jObjMain.getJSONObject("bustime-response");
            JSONArray stops = bustime.getJSONArray("stops");
            stopList.clear();
            for(int k = 0; k < stops.length(); k++){
                JSONObject obj = stops.getJSONObject(k);
                String id = obj.getString("stpid");
                String name = obj.getString("stpnm");
                Double lat = obj.getDouble("lat");
                Double lon = obj.getDouble("lon");
                Stop stop = new Stop(id, name, lat, lon);
                stopList.add(stop);
            }
            if (!usedCache) {
                String formattedDate = StopActivity.timeFormat.format(new Date());

                ctaCachedDataEditor.putString("STOP_TIME" +sRoute + sDir, formattedDate);
                ctaCachedDataEditor.putString(sRoute + sDir, jObjMain.toString());
                ctaCachedDataEditor.apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        if (stopList!= null)
           activity.runOnUiThread(() ->activity.updateData(stopList));
    }
}
