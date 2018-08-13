package com.tri.airr.hero;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.stream.*;
import static com.tri.airr.hero.BluetoothConnect.RBL_CHAR_TX_UUID;
import static com.tri.airr.hero.BluetoothConnect.RBL_SERVICE_UUID;
import static com.tri.airr.hero.BluetoothConnect.TAG;
import static com.tri.airr.hero.BluetoothConnect.connected;
import static com.tri.airr.hero.BluetoothConnect.hero;
import static com.tri.airr.hero.BluetoothConnect.heroGatt;
import static com.tri.airr.hero.BluetoothConnect.motorControl;



/**
 * Created by drossos on 7/26/2017.
 */

public class Daily extends Fragment {
    BluetoothConnect bleMethods = new BluetoothConnect();
    CommandBytes dataPresets = new CommandBytes();
    boolean on = false;
    private final int FLEX = 1;
    private final int EXTEN = 2;
    private final int SPD = 3;
    private Button onOff;
    private Button flexion;
    private Button extension;
    private Button speed;
    private TextView descript;
    private Button up;
    private boolean stopThread;
    private Button down;
    private final String EXTENSION_TITLE = "extenTitle";
    byte buffer[];
    private SharedPreferences prefs;
    private boolean optionSelected;
    //Counter that decides and shows level
    static int flexLev = 0;
    static int extenLev = 0;
    static int spdLev = 0;
    int curr = 0;
    byte [] commandDat =dataPresets.autoThreshhold;
    private StorageReference mStorageRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.daily_section, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //initalize storage for data
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        //initalizing all buttons and interactive elements
        onOff = (Button) getView().findViewById(R.id.onOff);
        flexion = (Button) getView().findViewById(R.id.flexion);
        extension = (Button) getView().findViewById(R.id.extension);
        speed = (Button) getView().findViewById(R.id.speed);
        descript = (TextView) getView().findViewById(R.id.description);
        up = (Button) getView().findViewById(R.id.up);
        down = (Button) getView().findViewById(R.id.down);

        //initalize data with value
        extenLev = prefs.getInt(EXTENSION_TITLE , 0);

        if (connected){
           /* bleMethods.handToggle();
            bleMethods.handToggle();*/
        }

        onOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOffSelect();
            }
        });

        flexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flexSelect();
            }
        });

        extension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extenSelect();
            }
        });

        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speedSelect();
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upSelect();
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downSelect();
            }
        });

    }
    //on and off button
    //TODO bug where if motor is off then select arrow the motor will not turn back on
        public void onOffSelect() {
            if (on) {
                on = false;
                onOff.setBackgroundColor(Color.RED);
                onOff.setText("OFF");
                if (connected) {
                    bleMethods.readFromHero();
                    bleMethods.writeToHero(dataPresets.turnOff);
                    //bleMethods.writeToHero(dataPresets.auto);
                }

            } else {
                on = true;
                onOff.setBackgroundColor(Color.GREEN);
                onOff.setText("ON");
                if (connected) {
                    //bleMethods.handToggle();
                    bleMethods.writeToHero(dataPresets.turnOn);
                    bleMethods.writeToHero(dataPresets.auto);
                }
            }
        }


        public void flexSelect (){
                optionSelected = true;
                curr = FLEX;
                flexion.setBackgroundColor(Color.GRAY);
                extension.setBackgroundColor(Color.parseColor("#add8e6"));
                speed.setBackgroundColor(Color.parseColor("#add8e6"));
                updateStatus(curr);
            }

        public void extenSelect() {
                optionSelected = true;
                curr = EXTEN;
                flexion.setBackgroundColor(Color.parseColor("#add8e6"));
                extension.setBackgroundColor(Color.GRAY);
                speed.setBackgroundColor(Color.parseColor("#add8e6"));
                updateStatus(curr);

            }



       public void speedSelect(){
                optionSelected = true;
                curr = SPD;
                speed.setBackgroundColor(Color.GRAY);
                extension.setBackgroundColor(Color.parseColor("#add8e6"));
                flexion.setBackgroundColor(Color.parseColor("#add8e6"));
                updateStatus(curr);
            }


        //Controls the different aspcets of the motor with the up and down arrows
        public void upSelect(){
                if (curr == FLEX) {
                    if (connected) {
                        bleMethods.readFromHero();
                        bleMethods.changeContract(-5);
                    }
                    flexLev++;
                    commandDat[2] = (byte)(commandDat[2] + 15);
                    if (connected) {
                        bleMethods.writeToHero(commandDat);

                    }
                } else if (curr == EXTEN) {
                    extenLev++;
                    commandDat[1] = (byte)(commandDat[1] + 1);
                    if(connected) {
                        bleMethods.writeToHero(commandDat);
                        bleMethods.changeExtend(5);
                    }
                }
                else if (curr == SPD) {
                    spdLev++;
                } else {
                }
                updateStatus(curr);

            }

       public void downSelect(){
                if (curr == FLEX && flexLev != 0) {
                    flexLev--;
                    commandDat[2] = (byte)(commandDat[2]-15);
                    if (connected) {
                        bleMethods.writeToHero(commandDat);
                        bleMethods.changeContract(5);
                    }
                }
                else if (curr == EXTEN && extenLev != 0) {
                    extenLev--;
                    commandDat[1] = (byte)(commandDat[1] - 1);
                    if (connected) {
                        bleMethods.writeToHero(commandDat);
                        bleMethods.changeExtend(-5);
                    }
                }
                else if (curr == SPD && spdLev != 0) {
                    spdLev--;
                } else {
                }
                updateStatus(curr);
            }




    public void updateStatus(int curr) {
        descript = (TextView) getView().findViewById(R.id.description);
        if (curr == FLEX) {
            descript.setText("Flexion: " + flexLev);
        }
        if (curr == EXTEN) {
            descript.setText("Extension: " + extenLev);
        }
        if (curr == SPD)
            descript.setText("Speed:" + spdLev);
        if (connected && optionSelected)
            updateMotor();
    }
    //TODO make it so updates data with correct values || make it only call static methods from within BluetoothConnect
    private void updateMotor(){

    }

    //TODO Potentialy take out listening thread
   /* void thread(){
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {


            }
        });

        thread.start();
    }
*/

    public String[] byteToString (byte[] dat){
        String[] B = new String[dat.length];
        for (int i = 0; i < dat.length; i++)
        {
            B[i] = (byte)dat[i]+"";
        }
        return B;
    }

    @Override
    public void onDestroy(){
        prefs.edit().putInt(EXTENSION_TITLE, extenLev).commit();
        super.onDestroy();
    }

   @Override
    public void onPause(){
       prefs.edit().putInt(EXTENSION_TITLE, extenLev).commit();
        super.onPause();
    }

    //TODO MAKE THIS WORK FOR THE NEW FRAGMENT VERSION
   /* @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            prefs.edit().putInt(EXTENSION_TITLE, extenLev).commit();
        }
    }*/
}


