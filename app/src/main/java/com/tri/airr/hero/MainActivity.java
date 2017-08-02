package com.tri.airr.hero;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    public static BluetoothAdapter bluetoothAdapter;
    public static Set<BluetoothDevice> pairedDevices;
    ListView lv;
    AlertDialog.Builder pairedDevs;
    public static BluetoothDevice device;
    public static BluetoothSocket socket;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static OutputStream outputStream;
    public static InputStream inputStream;
    public static boolean connected;
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

    public void BluetoothConnect () {
        connected = false;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //checks if device has B.T capabilties
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        //checks and then asks you to turn on B.T
        else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }
        //if B.T is already enabled it checks to see if HERO device is paired
        if (bluetoothAdapter.isEnabled()) {
            if (!list()) {
                Toast.makeText(getApplicationContext(), "HERO is not a paired device. Please pair HERO first", Toast.LENGTH_LONG).show();
            } else {
                //TODO TRY AND FIGURE THIS CODE OUT BETTER TO MAKE IT WORK LATER
                connected = true;
                try {
                    socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
                    socket.connect();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    connected = false;
                }
                if (connected) {
                    try {
                        outputStream = socket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        inputStream = socket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connected)
                    Toast.makeText(getApplicationContext(), "Connected to HERO", Toast.LENGTH_LONG).show();
            }


        }
    }
        //TODO MAKE IT ABLE TO CONNECT TO THE ARDUINO also clean up look of code
    public boolean list() {
        pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();

        } else  for (BluetoothDevice iterator : pairedDevices) {

            if(iterator.getAddress().equals("sa")){ //Replace with iterator.getName() if comparing Device names.

                device=iterator; //device is an object of type BluetoothDevice

                return true;



            }
        }
        return false;
    }



    }

