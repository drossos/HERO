package com.tri.airr.hero;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static com.tri.airr.hero.RESTMethods.dbArrContent;


public class RESTTest extends AppCompatActivity {

    JSONObject testJSon;
    RESTMethods rm = new RESTMethods();
    public RequestQueue request;
    public static final String BASE_URL = "https://hero-rehab-web.herokuapp.com/api/contacts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Widget_Holo);
        setContentView(R.layout.activity_resttest);

        Map<String, Object> test = new HashMap();
        test.put("metric3", (Math.random()*100));
        test.put("metric2", (Math.random()*100));
        test.put("metric1", (Math.random()*100));
        test.put("name", "AndroidPhone" + Math.random());
        JSONObject testJSon = new JSONObject(test);

       request = Volley.newRequestQueue(this);
        rm.JSONArrayRequest(request, BASE_URL, null, Request.Method.GET);


    }

    //TODO SERVER SIDE FIX GET METHOD TO RETURN ENTIRE DB
    public void sendData(View view){
        Map<String, Object> test = new HashMap();
        test.put("metric3", (Math.random()*100));
        test.put("metric2", (Math.random()*100));
        test.put("metric1", (Math.random()*100));
        test.put("name", "AndroidPhone" + Math.random());
        JSONObject testJSon = new JSONObject(test);

        request = Volley.newRequestQueue(this);

        rm.JSONObjectRequest(request, BASE_URL, testJSon, Request.Method.POST );
        rm.JSONArrayRequest(request, BASE_URL, null, Request.Method.GET);
        Toast.makeText(this, "Test data has been sent", Toast.LENGTH_LONG).show();
    }

    public void receiveData(View v) throws JSONException, InterruptedException {
       /* RequestQueue request = Volley.newRequestQueue(this);*/

        JSONArray testJSon = new JSONArray();


        rm.JSONArrayRequest(request, BASE_URL, null, Request.Method.GET);
        Toast.makeText(this, "There are " + dbArrContent.length()+" people in the database", Toast.LENGTH_LONG).show();


    }
}
