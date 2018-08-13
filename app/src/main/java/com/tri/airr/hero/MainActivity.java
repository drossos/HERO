package com.tri.airr.hero;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import at.grabner.circleprogress.CircleProgressView;

import static com.tri.airr.hero.RESTTest.BASE_URL;

/**
 * Created by drossos on 7/26/2017.
 */


public class MainActivity extends Fragment {

    private RESTMethods rm = new RESTMethods();
    public static RequestQueue request;
    public static String entryName;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static FirebaseAuth mAuth;
    public String GA_TAG = "Google Analytics";
    //TODO remove just for development to test this one apps capability for data analysis
    public static final String dbEntry = "numtest";
    public static Database db;
    public CircleProgressView graspsProgress;
    public TextView graspsTitle;
    public Button goToVoiceControl;
    public Button goToVoiceControlNW;
    long grasps;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceStates){
        super.onActivityCreated(savedInstanceStates);

       // graspsProgress.setValueAnimated();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        goToVoiceControl = (Button) getView().findViewById(R.id.voice_control_goto);
        goToVoiceControlNW = (Button) getView().findViewById(R.id.voice_nw_control_goto);

        goToVoiceControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToVoiceControl();
            }
        });

        goToVoiceControlNW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToVoiceNWControl();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //check for login
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //FirebaseUser currentUser =null;
        //String str =  currentUser.getEmail();
        updateUI(currentUser);

       updateProgress();
    }

    private void updateProgress() {
        BluetoothConnect.pause(1000);
        grasps=Database.numGrasps;
        graspsProgress = (CircleProgressView) getView().findViewById(R.id.grasps_progress);

        graspsTitle = (TextView) getView().findViewById(R.id.grasp_title);
        graspsProgress.setValue(grasps);
        graspsProgress.setMaxValueAllowed(grasps);
        graspsProgress.setMinValueAllowed(grasps);

        graspsTitle.setText("Daily Grasps: "+grasps);
    }

    //check to see if user signed in
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null){
            Intent intent = new Intent(getContext(), Authentication.class);
            startActivity(intent);
        } else {
            Authentication.ENTRY_NAME = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("username", "def");
        }
        db = new Database();

    }

    public void goTo(Context base, Class dest){
        Intent intent = new Intent(base, dest);
        startActivity(intent);
    }

    public void goToDaily(View v) {
        Intent intent = new Intent(getContext(), Daily.class);
        startActivity(intent);

    }

    public void goToExercise(View v) {
        Intent intent = new Intent(getContext(), Exercise.class);
        startActivity(intent);
    }

    public void goToResults(View v) {
        Intent intent = new Intent(getContext(), Results.class);
        startActivity(intent);
    }

    public void goToStretch(View v) {
        Intent intent = new Intent(getContext(), Stretch.class);
        startActivity(intent);
    }

    public void goToEmail(View v) {
        Intent intent = new Intent(getContext(), Email.class);
        startActivity(intent);
    }

    public void connectBluetooth(View v) {
        Intent intent = new Intent(getContext(), BluetoothConnect.class);
        startActivity(intent);
    }

    public void goToRESTTest (View v) {
        Intent intent = new Intent(getContext(), RESTTest.class);
        startActivity(intent);
    }

    public void goToVoiceControl () {
        // Begin the transaction
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.content, new VoiceControl());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }

    public void goToVoiceNWControl(){
        // Begin the transaction
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.content, new VoiceControlNoWords());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }


}








