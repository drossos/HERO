package com.tri.airr.hero;


import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;


/**
 * Created by Daniel Rossos on 7/31/2017.
 */

public class BluetoothConnect extends AppCompatActivity {
    private int mConnectionState = STATE_DISCONNECTED;
    public static BluetoothManager btManager;
    public static BluetoothAdapter btAdapter;
    public static BluetoothLeScanner btScanner;
    Button startScanningButton;
    Button stopScanningButton;
    static final String TAG = "test";
    TextView peripheralTextView, testText;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public static BluetoothDevice hero;
    public static BluetoothGatt heroGatt;
    //TODO FIND BETTER WAY THAT ALLOWS FOR NON HARDCODED ADRESS
    public final String HERO_MAC = "D1:85:3B:0F:02:AF";//"E3:8E:E4:56:FF:4F";
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    //UUID here are for the different services/characterisits
    public static final UUID RBL_SERVICE_UUID = UUID.fromString("713D0000-503e-4C75-BA94-3148F18D941E");
    public static final UUID RBL_CHAR_TX_UUID = UUID.fromString("713d0002-503e-4c75-ba94-3148f18d941e");
    public static final UUID RBL_CHAR_RX_UUID = UUID.fromString("713d0003-503e-4c75-ba94-3148f18d941e");
    public static final UUID RBL_TX_UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static BluetoothGattCharacteristic motorControl;
    public static boolean connected;
    int test = 1;
    private static FileWriter fw;
    private final String DIRECTORY_PATH = Environment.getExternalStorageDirectory().toString();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_connect);

        File root = new File(DIRECTORY_PATH);
        File gpxfile = new File(root, "samples.txt");
        try {
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("First string is here to be written.");
            writer.flush();
            writer.close();
            Log.i(TAG, "test works");
        } catch (IOException e){
            Log.e(TAG, "didnt work");
        }

        peripheralTextView = (TextView) findViewById(R.id.PeripheralTextView);
        peripheralTextView.setMovementMethod(new ScrollingMovementMethod());
        testText = (TextView) findViewById(R.id.test_area);
        startScanningButton = (Button) findViewById(R.id.StartScanButton);
        startScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*
                Currently taken out GPS requierment, if later in development this changes add this code to conditional check of enabled services
                getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                 */
                if (!btAdapter.isEnabled()){
                    peripheralTextView.setText("Make sure that Bluetooth services are enabeld");
                } else {
                    startScanning();
                }
            }
        });

        stopScanningButton = (Button) findViewById(R.id.StopScanButton);
        stopScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               stopScanning();
            }
        });
        stopScanningButton.setVisibility(View.INVISIBLE);
        Button testConnect = (Button) findViewById(R.id.test_connect);
        testConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                testConnect();
            }
        });
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        //check if bt enabled and then request to enable
        btCheck();

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        //TODO Figure out this gps request better, pretty sure first one does not work , IF REQUIRED LATER IN DEVELOPMENT

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }


        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }






    }


    private void btCheck() {
        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!btAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);

        }

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private final BluetoothGattCallback heroGattCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        heroGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        // New services discovered
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("BluetoothLeService", "onServicesDiscovered()");
            if (status == BluetoothGatt.GATT_SUCCESS) {


                List<BluetoothGattService> gattServices = heroGatt.getServices();
                Log.e("onServicesDiscovered", "Services count: " + gattServices.size());

                for (BluetoothGattService gattService : gattServices) {
                    String serviceUUID = gattService.getUuid().toString();
                    Log.e("onServicesDiscovered", "Service uuid " + serviceUUID);
                }
                motorControl = heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_TX_UUID);
            } else {
                Log.e("BluetoothLeService", "No BluetoothLe discovered");
            }
        }

        //Callback for when character write is "requested"
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            Log.i(TAG, "Attempting to execute write ");
            if(status != BluetoothGatt.GATT_SUCCESS){
                Log.i("onCharacteristicWrite", "Failed write, retrying");
                gatt.writeCharacteristic(characteristic);
            }
            Log.i("onCharacteristicWrite","Write Success");
            super.onCharacteristicWrite(gatt, characteristic, status);


        }

    };

    // Device scan callback, searches available devices and then displays and connects to HERO
    //TODO conisder making a different way to display devices so just not list of them
    //TODO currently goes based off of MAC adress which changes with each different device, for more practical use make it connect via name or some other way
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            peripheralTextView.append("Device Name: " + result.getDevice().getName() + " rssi: " + result.getRssi() + " MAC: " + result.getDevice().getAddress() +
                    "\n");

            // auto scroll for text view
            final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
            // if there is no need to scroll, scrollAmount will be <=0
            if (scrollAmount > 0)
                peripheralTextView.scrollTo(0, scrollAmount);
            if(result.getDevice().getAddress().equals(HERO_MAC)) {
                hero = result.getDevice();
                Toast.makeText(getApplicationContext(), "Found HERO", Toast.LENGTH_SHORT).show();
                heroGatt = result.getDevice().connectGatt(getApplicationContext(), false, heroGattCallBack);
                connectGatt();
                stopScanning();
            }
        }


    };
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void startScanning() {
        System.out.println("start scanning");
        peripheralTextView.setText("");
        startScanningButton.setVisibility(View.INVISIBLE);
        stopScanningButton.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        System.out.println("stopping scanning");
        peripheralTextView.append("Stopped Scanning");
        startScanningButton.setVisibility(View.VISIBLE);
        stopScanningButton.setVisibility(View.INVISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }
    public void connectGatt(){
        heroGatt.connect();
        connected = true;
    }
    //checks to see if it is possible to send data|-used only for testing
    //TODO get rid of the test connect once advance development to across views control
    public void testConnect(){
        //TODO make the bellow connect work with the static varriable
        //test data to make fist
        byte [] dat = {(byte)0x03,
                (byte)0x03, (byte)0x02};
        byte [] close = {(byte)0x03,
                (byte)0x03, (byte)0x01};
        byte[] turnOn = {0x01, 0x00, 0x01};
        heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID).setValue(turnOn);
        pause();
        heroGatt.writeCharacteristic(heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID));
       // heroGatt.readCharacteristic(heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_TX_UUID));
        pause();
        //Characteristic is re written here on the app side
        if (test == 1) {
            Log.i(TAG, heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID).setValue(dat) + "");
            test = 0;
        } else {
            Log.i(TAG, heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID).setValue(close) + "");
            test = 1;
        }

        pause();
        //request is made for the rewritten characteristic from app to be pushed to the robot
        //callback than handles that request
        Log.i(TAG,heroGatt.writeCharacteristic(heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID)) + " Attempt at writing Characteristic" );
    }

    public static void pause(){
        try{
            Thread.sleep(10);
        }catch(InterruptedException e){
            System.out.println("got interrupted!");
        }
    }

    //For across view data write call method
    //TODO test to see if static method works from other views
    public void writeToHero(byte[] dat){
        pause();
        //Characteristic is re written here on the app side

        Log.i(TAG, heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID).setValue(dat) + "");
        pause();
        //request is made for the rewritten characteristic from app to be pushed to the robot
        //callback then handles that request
        Log.i(TAG,heroGatt.writeCharacteristic(heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID)) + " Attempt at writing Characteristic" );
        logBytesSent(dat);
    }

    private static void logBytesSent(byte[] dat){

        try{
            fw = new FileWriter(new File("BytesSent.txt"));
        } catch (IOException e){
            Log.e(TAG, ".txt not found ");
            e.printStackTrace();
        }

        for (int i  = 0; i < dat.length;i++){
            try{
                fw.write((byte)dat[i] + "/n");
            } catch (IOException e){
                Log.e(TAG, ".unable to write bytes");
                e.printStackTrace();
            }
        }

        try{
            fw.write("/n");
        } catch (IOException e){
            Log.e(TAG, ".unable to write bytes");
            e.printStackTrace();
        }
    }




    /* Test code for check connection
     pause();
        //Characteristic is re written here on the app side
        if (test == 1) {
            Log.i(TAG, heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID).setValue(dat) + "");
            test = 0;
        } else {
            Log.i(TAG, heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID).setValue(close) + "");
            test = 1;
        }

        pause();
        //request is made for the rewritten characteristic from app to be pushed to the robot
        //callback than handles that request
        Log.i(TAG,heroGatt.writeCharacteristic(heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID)) + " Attempt at writing Characteristic" );
     */


}

