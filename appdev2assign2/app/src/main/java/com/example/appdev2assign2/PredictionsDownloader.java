package com.example.appdev2assign2;

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

public class PredictionsDownloader {
    private static final String TAG = "PredDownload";
    private static final String routeURL = "http://www.ctabustracker.com/bustime/api/v2/getpredictions?key=";

    private static final String yourAPIKey = "rMyPptA3R5Re7F2pnfV3haQhQ";
    //
    //////////////////////////////////////////////////////////////////////////////////

    public static void getSourceData(PredictionsActivity activity, String route, String stopid) {
        RequestQueue queue = Volley.newRequestQueue(activity);

        Uri.Builder buildURL = Uri.parse(routeURL + yourAPIKey + "&format=json&rt=" + route + "&stpid=" + stopid).buildUpon();
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

    private static void handleResults(PredictionsActivity activity, String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            //mainActivity.downloadFailed();
            return;
        }
        ArrayList<Prediction> preds = parseJSON(s);
        if (preds != null) {
            activity.updateData(preds);
        }
    }

    private static ArrayList<Prediction> parseJSON(String s) {

        ArrayList<Prediction> predList = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONObject bustime = jObjMain.getJSONObject("bustime-response");
            JSONArray prd = bustime.getJSONArray("prd");
            for(int k = 0; k < prd.length(); k++){
                JSONObject obj = prd.getJSONObject(k);
                String vid = obj.getString("vid");
                String rtdir = obj.getString("rtdir");
                String des = obj.getString("des");
                String prdtm = obj.getString("prdtm");
                Boolean delay = obj.getBoolean("dly");
                String prdtime = obj.getString("prdctdn");
                Prediction pred = new Prediction(vid, rtdir, des, prdtm,delay, prdtime );
                predList.add(pred);
            }
            return predList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
