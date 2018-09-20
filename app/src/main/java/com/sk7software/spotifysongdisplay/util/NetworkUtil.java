package com.sk7software.spotifysongdisplay.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();

    private static RequestQueue queue;

    private synchronized static RequestQueue getQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    public interface NetworkCallback {
        public void onRequestCompleted(Map<String, Object> callbackData);

        public void onError(Exception e);
    }

    public static void makeCall(Context context, String fullURL) {
        StringRequest stringRequest = new StringRequest
                (Request.Method.GET, fullURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Sent");
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error => " + error.toString());
                            }
                        }
                );
        getQueue(context).add(stringRequest);
    }

}
