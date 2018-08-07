package com.tri.airr.hero;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RESTMethods extends AppCompatActivity {
    static String tempURL;
    static JSONObject dbContent;
    static JSONArray dbArrContent;
    static String currID;
    public RequestQueue req;
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

    //TODO ADD BETTER WAY ON SERVER TO GET A SPECIFIC ENTRY


    public static JSONObject  getPrevDBEntry(JSONArray arr, String name) throws JSONException {
        for (int i =0 ; i < arr.length(); i++){
            if (arr.getJSONObject(i).getString("name").equals(name)){
                currID = arr.getJSONObject(i).getString("_id");
                return arr.getJSONObject(i);
            }
        }
        return null;
    }

    //TODO THIS TRY CATCH SYSTEM MESSY
    public JSONObject incrimentGraspDB(String dbEntryName, int grabChange){
        JSONObject prevEntry;
        JSONObject updatedEntry;
        try {
            prevEntry = getPrevDBEntry(dbArrContent, dbEntryName);
            //createNewJSON(prevEntry);
            return updatedEntry = createNewJSON(prevEntry);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject createNewJSON(JSONObject prev) throws JSONException {
        Map<String, Object> temp = new HashMap();
        temp.put("metric3", BluetoothConnect.battery);
        temp.put("metric2", prev.getDouble("metric2"));
        temp.put("metric1", prev.getDouble("metric1") + 1 );
        temp.put("name", prev.getString("name"));
        JSONObject tempJSon = new JSONObject(temp);

        return tempJSon;
    }
}