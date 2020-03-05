package com.iSales.remote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApiConnectionUrl {
    private static final String TAG = ApiConnectionUrl.class.getSimpleName();

    private Context context;

    public ApiConnectionUrl(Context context){
        this.context = context;
    }

    public String sendUrl(String method, String serverSelected, String path, String attributs){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = serverSelected + path + attributs;
        JsonObjectRequest arrayObject;
        final String[] result = {null};



        if (method.equals("post") || method.equals("POST") || method.equals("Post")){
            arrayObject = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Volley POST response", "JsonObject Response : "+response);
                    try {
                        result[0] = response.getString("newRef");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, " POST JsonObject Erreur : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                    Log.e(TAG, " POST JsonObject Erreur :\n"+error.getMessage());
                }
            });
            queue.add(arrayObject);
        }

        if (method.equals("get") || method.equals("GET") || method.equals("Get")) {
            //for get method
        }

        return result[0];
    }
}
