package com.tri.airr.hero;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    ListView lv;
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
            bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
            }
            else if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 0);
                Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
            }
                if (bluetoothAdapter.isEnabled()){
                    AlertDialog.Builder pairedDevs = new AlertDialog.Builder(
                            context);
                   list();

                    pairedDevs.setTitle("test");
                }


        }
    public void list(){
        pairedDevices = bluetoothAdapter.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
    }

    }

