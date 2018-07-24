package com.tri.airr.hero;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.UUID;

import static com.tri.airr.hero.BluetoothConnect.TINY_TILE_MOTOR_CONTRACT_CHAR;
import static com.tri.airr.hero.BluetoothConnect.TINY_TILE_MOTOR_EXTEND_CHAR;
import static com.tri.airr.hero.BluetoothConnect.TINY_TILE_MOTOR_SERVICE;
import static com.tri.airr.hero.RESTTest.BASE_URL;

/**
 * Created by drossos on 7/26/2017.
 */


public class MainActivity extends AppCompatActivity {

    private RESTMethods rm = new RESTMethods();
    public static RequestQueue request;

    //TODO remove just for development to test this one apps capability for data analysis
    public static final String dbEntry = "HERO_Test";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Widget_Holo);
        setContentView(R.layout.activity_main);

        request = Volley.newRequestQueue(this);
        rm.JSONArrayRequest(request, BASE_URL, null, Request.Method.GET);

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
}








