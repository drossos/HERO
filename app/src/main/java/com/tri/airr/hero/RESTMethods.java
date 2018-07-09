package com.tri.airr.hero;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class RESTMethods extends AppCompatActivity {
    static String tempURL;
    static JSONObject dbContent;
    static JSONArray dbArrContent;
    //Used for all requests to the server
    //call request GET with JSON object to fetch single post + addExtension to the url of the id  (for GET all make sure no value is passed in for the JSON)
    //call request PUT with JSON with object to edit + addExtension to the url of the id
    //call request GET with no object to fetch all
    //call request POST with JSON object to add new object

    //MAKE SURE WHEN CALLING DATA THAT THE RIGHT METHOD IS CALLED
    public  void JSONObjectRequest(RequestQueue requestQueue, String baseUrl, JSONObject jsonObject, int method) {
        tempURL = baseUrl;
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                method,
                baseUrl,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Rest", "in the onResponse for JSONObjects");
                        Log.e("RestResponse", response.toString());

                        dbContent = response;

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RestResponse", error.toString());
                    }
                }

        );
        requestQueue.add(objectRequest);

    }

    public  void JSONArrayRequest(RequestQueue requestQueue, String baseUrl, JSONArray jsonObject, int method) {
        tempURL = baseUrl;
        JsonArrayRequest objectRequest = new JsonArrayRequest(
                method,
                baseUrl,
                jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Rest", "in the onResponse for JSONArray");
                        Log.e("RestResponse", response.toString());

                        dbArrContent = response;

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RestResponse", error.toString());
                    }
                }

        );
        requestQueue.add(objectRequest);

    }

}