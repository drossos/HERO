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

import java.util.UUID;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

}








