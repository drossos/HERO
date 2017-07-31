package com.tri.airr.hero;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void goToDaily (View v){
        Intent intent = new Intent(MainActivity.this, Daily.class);
        startActivity(intent);

    }

    public void goToExercise (View v){
        Intent intent = new Intent(MainActivity.this, Exercise.class);
        startActivity(intent);
    }
    public void goToResults (View v){
        Intent intent = new Intent(MainActivity.this, Results.class);
        startActivity(intent);
    }
    public void goToStretch (View v){
        Intent intent = new Intent(MainActivity.this, Stretch.class);
        startActivity(intent);
    }
    public void goToEmail (View v){
        Intent intent = new Intent(MainActivity.this, Email.class);
        startActivity(intent);
    }
    public void connectBluetooth (View v){
       BluetoothConnect();
    }

    public void BluetoothConnect (){
            BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
            }
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 0);
            }

        }
    }

