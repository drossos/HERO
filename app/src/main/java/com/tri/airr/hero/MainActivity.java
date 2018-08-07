package com.tri.airr.hero;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.tri.airr.hero.RESTTest.BASE_URL;

/**
 * Created by drossos on 7/26/2017.
 */


public class MainActivity extends AppCompatActivity {

    private RESTMethods rm = new RESTMethods();
    public static RequestQueue request;
    public static String entryName;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static FirebaseAuth mAuth;
    public String GA_TAG = "Google Analytics";
    //TODO remove just for development to test this one apps capability for data analysis
    public static final String dbEntry = "numtest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Widget_Holo);
        setContentView(R.layout.activity_main);



        /*request = Volley.newRequestQueue(this);
        rm.JSONArrayRequest(request, BASE_URL, null, Request.Method.GET);
        entryName = "AndroidPhone" + Math.random();*/

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public void onStart(){
        super.onStart();

        //check for login
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //FirebaseUser currentUser =null;
       // String str =  currentUser.getEmail();
        updateUI(currentUser);
    }

    //todo finish
    //check to see if user signed in
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null){
            Intent intent = new Intent(MainActivity.this, Authentication.class);
            startActivity(intent);
        } else
           Authentication.ENTRY_NAME =  PreferenceManager.getDefaultSharedPreferences(this).getString("username", "def");
    }

    public void goToDaily(View v) {
        Intent intent = new Intent(MainActivity.this, Daily.class);
        startActivity(intent);

    }

    public void goToExercise(View v) {
        Intent intent = new Intent(MainActivity.this, Exercise.class);
        startActivity(intent);
    }

    public void goToResults(View v) {
        Intent intent = new Intent(MainActivity.this, Results.class);
        startActivity(intent);
    }

    public void goToStretch(View v) {
        Intent intent = new Intent(MainActivity.this, Stretch.class);
        startActivity(intent);
    }

    public void goToEmail(View v) {
        Intent intent = new Intent(MainActivity.this, Email.class);
        startActivity(intent);
    }

    public void connectBluetooth(View v) {
        Intent intent = new Intent(MainActivity.this, BluetoothConnect.class);
        startActivity(intent);
    }

    public void goToRESTTest (View v) {
        Intent intent = new Intent(MainActivity.this, RESTTest.class);
        startActivity(intent);
    }

    public void goToVoiceControl (View v) {
        Intent intent = new Intent(MainActivity.this, VoiceControl.class);
        startActivity(intent);
    }

    public void goToVoiceNWControl(View v){
        Intent intent = new Intent(MainActivity.this, VoiceControlNoWords.class);
        startActivity(intent);
    }
}








