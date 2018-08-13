package com.tri.airr.hero;

//TODO NOT IDEAL TO HAVE ALL BLUETOOTH METHODS WITHIN A ACTIVITY CLASS
import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.tri.airr.hero.RESTTest.BASE_URL;


/**
 * Created by Daniel Rossos on 7/31/2017.
 */

//TODO GOING ACROSS ACTIVITIES MESSES UP RECONNECTING TO BLUETOOTH SERVICES

public class BluetoothConnect extends AppCompatActivity {
    private int mConnectionState = STATE_DISCONNECTED;
    public static BluetoothManager btManager;
    public static BluetoothAdapter btAdapter;
    public static BluetoothLeScanner btScanner;
    Button startScanningButton;
    Button stopScanningButton;
    static final String TAG = "test";
    TextView peripheralTextView, testText, batteryDisplay;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public static BluetoothDevice hero;
    public static BluetoothGatt heroGatt;
    public static int battery;
    //TODO FIND BETTER WAY THAT ALLOWS FOR NON HARDCODED ADRESS
    public final String HERO_MAC = "84:68:3E:06:CF:36"; //"E3:8E:E4:56:FF:4F";
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public static int extendMotor = 50; //50 = fully squeeze bottle; 80 = partially squeeze bottle
    public static int retractMotor = 140; //150 = fully extend fingers; 120 = partially extend fingers

    //NAME TO SEARCH FOR
    public static final String HERO_NAME = "LegoHERO";
    //hand states
    public static final int MANUAL_EXTEND = 0;
    public static final int MANUAL_CONTRACT = 1;
    public static final int MANUAL_RELAX = -1;
    //UUID here are for the different services/characterisits
    public static final UUID RBL_SERVICE_UUID = UUID.fromString("713D0000-503e-4C75-BA94-3148F18D941E");
    public static final UUID RBL_CHAR_TX_UUID = UUID.fromString("713d0002-503e-4c75-ba94-3148f18d941e");
    public static final UUID RBL_CHAR_RX_UUID = UUID.fromString("713d0003-503e-4c75-ba94-3148f18d941e");

    public static final UUID RBL_TX_UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID TINY_TILE_BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID TINY_TILE_BATTERY_LEVEL_CHAR = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");

    public static final UUID TINY_TILE_MOTOR_SERVICE = UUID.fromString("ebbb19a6-c943-44f9-aee0-e180300007f0");
    public static final UUID TINY_TILE_MOTOR_LEVEL_CHAR = UUID.fromString("00211321-dc03-4f55-82b6-14a630bd8e2d");
    public static final UUID TINY_TILE_MOTOR_EXTEND_CHAR = UUID.fromString("809ba7a9-13ad-4446-a005-bdc12ca93c76");
    public static final UUID TINY_TILE_MOTOR_CONTRACT_CHAR = UUID.fromString("2fad8a3f-e1a1-47cd-982b-24e13fbe9342");

    public static BluetoothGattCharacteristic motorControl;
    public static boolean connected;
    int test = 1;
    private int bytesChange = 1;
    private static boolean toggle = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Widget_Holo);
        setContentView(R.layout.bt_connect);

  /*myRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
      currPatient = (HashMap<String, Object> )dataSnapshot.getValue();
      numGrasps = (long)currPatient.get("metric1");
      Log.i(firebaseTag, dataSnapshot.getValue().toString());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
  });*/

        peripheralTextView = (TextView) findViewById(R.id.PeripheralTextView);
        batteryDisplay = (TextView) findViewById(R.id.battery_display);
        peripheralTextView.setMovementMethod(new ScrollingMovementMethod());
        testText = (TextView) findViewById(R.id.test_area);
        startScanningButton = (Button) findViewById(R.id.StartScanButton);
        startScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
    /*
    Currently taken out GPS requierment, if later in development this changes add this code to conditional check of enabled services
    getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
     */
                if (heroGatt != null) {
                    heroGatt.close();
                    heroGatt.disconnect();
                    //heroGatt.close();
                }
                if (!btAdapter.isEnabled()) {
                    peripheralTextView.setText("Make sure that Bluetooth and GPS services are enabeld");
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
        testConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testConnect();
            }
        });

        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
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
              requestPermissions(new String[] {
                      Manifest.permission.ACCESS_COARSE_LOCATION
              }, PERMISSION_REQUEST_COARSE_LOCATION);
          }
      });
      builder.show();
  }


        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

  /*if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      buildAlertMessageNoGps();
  }*/
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

    //to allow for scanner to only be established on response
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        btScanner = btAdapter.getBluetoothLeScanner();
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

                //auto send back to main screen on connect
                Intent intent = new Intent(BluetoothConnect.this, Home.class);
                startActivity(intent);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Disconnected from HERO, scan to connect again", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        // New services discovered
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("BluetoothLeService", "onServicesDiscovered()");
            if (status == BluetoothGatt.GATT_SUCCESS) {


                List < BluetoothGattService > gattServices = heroGatt.getServices();
                Log.e("onServicesDiscovered", "Services count: " + gattServices.size());

                for (BluetoothGattService gattService: gattServices) {
                    String serviceUUID = gattService.getUuid().toString();
                    Log.e("onServicesDiscovered", "Service uuid " + serviceUUID);
                }
                //motorControl = heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_TX_UUID);
            } else {
                Log.e("BluetoothLeService", "No BluetoothLe discovered");
            }
        }

        //Callback for when character write is "requested"
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            Log.i(TAG, "Attempting to execute write ");
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.i("onCharacteristicWrite", "Failed write, retrying");
                gatt.writeCharacteristic(characteristic);
            }
            Log.i("onCharacteristicWrite", "Write Success");
            super.onCharacteristicWrite(gatt, characteristic, status);


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            Log.i(TAG, "in da callback");
            int characValue = heroGatt.getService(TINY_TILE_BATTERY_SERVICE).getCharacteristic(TINY_TILE_BATTERY_LEVEL_CHAR).getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);

            //        for (byte i : charValue)
            Log.i(TAG, "" + ("HERO Battery Level: %" + characValue));
            battery = characValue;

            /*This all to allow for a active update on HERO battery*/
            //get descriptor and allow for notification
            UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            heroGatt.writeDescriptor(descriptor);

            //subscribe for notifications from the characteristic
            heroGatt.setCharacteristicNotification(heroGatt.getService(TINY_TILE_BATTERY_SERVICE).getCharacteristic(TINY_TILE_BATTERY_LEVEL_CHAR), true);

            //todo change back to true, but right now unable to properaly handle copnstant updates to the ui
            //heroGatt.setCharacteristicNotification(heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_LEVEL_CHAR), false);
            //            byte[] charValue = characteristic.getValue();
            //
            //            for (byte i : charValue)
            //                Log.i(TAG,i + "");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

   /*//todo make so not case sensitive
   //will read if button is changed on the robot and will update ui
   if (characteristic.getUuid().equals(TINY_TILE_MOTOR_LEVEL_CHAR)){
       int characValue = heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_LEVEL_CHAR).getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
       if (characValue == MANUAL_CONTRACT)
           VoiceControl.text = "close";
       if (characValue == MANUAL_EXTEND)
           VoiceControl.text = "open";

       VoiceControl.updateUI.start();
       try {
           VoiceControl.updateUI.join();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }

   } else {*/

            int characValue = heroGatt.getService(TINY_TILE_BATTERY_SERVICE).getCharacteristic(TINY_TILE_BATTERY_LEVEL_CHAR).getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            //Log.i(TAG,"InOnChange"+ ("HERO Battery Level: %"+characValue));
            battery = characValue;


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    batteryDisplay.setText("HERO battery:" + battery);
                }
            });
        }


        //}
    };


    // Device scan callback, searches available devices and then displays and connects to HERO
    //TODO conisder making a different way to display devices so just not list of them
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
            if (result.getDevice().getName() != null && result.getDevice().getName().equals(HERO_NAME)) {
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
            case PERMISSION_REQUEST_COARSE_LOCATION:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {}

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
        btScanner = btAdapter.getBluetoothLeScanner();
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
    public void connectGatt() {
        //heroGatt.connect();
        connected = true;
        boolean temp = heroGatt.requestMtu(1000);
        heroGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
    }
    //checks to see if it is possible to send data|-used only for testing
    //TODO get rid of the test connect once advance development to across views control
    public void testConnect() {
        if (connected) {
            //Log.i(TAG,heroGatt.writeCharacteristic(heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID)) + " Attempt at writing Characteristic" );
            Log.i(TAG, "Attempt to read BLEchar" + heroGatt.readCharacteristic(heroGatt.getService(TINY_TILE_BATTERY_SERVICE).getCharacteristic(TINY_TILE_BATTERY_LEVEL_CHAR)));
            pause(100);

            handToggle();
        }
    }

    public void handToggle() {
        Log.i(TAG, "Attempt to write to HERO");

        if (toggle)
            heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_LEVEL_CHAR).setValue(MANUAL_EXTEND, BluetoothGattCharacteristic.FORMAT_SINT32, 0);
        else
            heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_LEVEL_CHAR).setValue(MANUAL_CONTRACT, BluetoothGattCharacteristic.FORMAT_SINT32, 0);

        toggle = !toggle;

        //pause(1000);
        //boolean temp = heroGatt.writeCharacteristic(heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_LEVEL_CHAR));
        //batteryDisplay.setText("HERO battery:"+battery);
        //pause(1000);
    }

    //TODO change so any string can be passed not just the two commands
    public void handToggle(String command) {
        Log.i(TAG, "Attempt to write to HERO");

        if (command.equals("open")) {
            heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_LEVEL_CHAR).setValue(MANUAL_EXTEND, BluetoothGattCharacteristic.FORMAT_SINT32, 0);
            MainActivity.db.currPatient.put("metric1", ++MainActivity.db.numGrasps);
            MainActivity.db.myRef.setValue(MainActivity.db.currPatient);
        } else if (command.equals("close"))
            heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_LEVEL_CHAR).setValue(MANUAL_CONTRACT, BluetoothGattCharacteristic.FORMAT_SINT32, 0);
        else
            heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_LEVEL_CHAR).setValue(MANUAL_RELAX, BluetoothGattCharacteristic.FORMAT_SINT32, 0);

        toggle = !toggle;

        //pause(1000);
        boolean temp = heroGatt.writeCharacteristic(heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_LEVEL_CHAR));
        //batteryDisplay.setText("HERO battery:"+battery);
        // pause(1000);
    }


    public static void pause() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.out.println("got interrupted!");
        }
    }

    public static void pause(int dur) {
        try {
            Thread.sleep(dur);
        } catch (InterruptedException e) {
            System.out.println("got interrupted!");
        }
    }

    //For across view data write call method
    //TODO test to see if static method works from other views
    public void writeToHero(byte[] dat) {
  /* pause();
   //Characteristic is re written here on the app side

   Log.i(TAG, heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID).setValue(dat) + "");
   pause();
   //request is made for the rewritten characteristic from app to be pushed to the robot
   //callback then handles that request
   Log.i(TAG,heroGatt.writeCharacteristic(heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID)) + " Attempt at writing Characteristic" );
   logBytesSent(dat);*/
    }

    public void readFromHero() {
  /* pause();
   //Characteristic is re written here on the app side
   //request is made for the rewritten characteristic from app to be pushed to the robot
   //callback then handles that request
   Log.i(TAG,heroGatt.readCharacteristic(heroGatt.getService(RBL_SERVICE_UUID).getCharacteristic(RBL_CHAR_RX_UUID)) + " Attempt at writing Characteristic" );
   pause();
   Log.i(TAG, "Attempting to execute read ");*/

    }

    private void logBytesSent(byte[] dat) {
        Daily methods = new Daily();
        bytesChange++;
    }

    public void changeExtend(int n) {
        extendMotor += n;
        heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_EXTEND_CHAR).setValue(extendMotor, BluetoothGattCharacteristic.FORMAT_SINT32, 0);
        heroGatt.writeCharacteristic(heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_EXTEND_CHAR));
        Log.i(TAG, "Change extension value");
    }

    public void changeContract(int n) {
        retractMotor += n;
        heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_CONTRACT_CHAR).setValue(retractMotor, BluetoothGattCharacteristic.FORMAT_SINT32, 0);
        heroGatt.writeCharacteristic(heroGatt.getService(TINY_TILE_MOTOR_SERVICE).getCharacteristic(TINY_TILE_MOTOR_CONTRACT_CHAR));
        Log.i(TAG, "Change contract value");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (heroGatt != null) {
            heroGatt.close();
            heroGatt.disconnect();
            //heroGatt.close();
        }
    }


}