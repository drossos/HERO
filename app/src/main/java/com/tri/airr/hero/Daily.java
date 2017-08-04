package com.tri.airr.hero;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ThreadFactory;
import java.util.stream.*;



//TODO CHANGE IT SO METHODS ARE CALLED INSTEAD OF ONCLICK LISTENERS THAT WAY CAN KEEP THE WHOLE CODE IN A LISTENING AND RECEVING LOOP
/**
 * Created by drossos on 7/26/2017.
 */
public class Daily extends AppCompatActivity {
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
    byte buffer[];
    //Counter that decides and shows level
    int flexLev = 0;
    int extenLev = 0;
    int spdLev = 0;
    int curr = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //Var that shows which is current option selected
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_section);
        //initalizing all buttons and interactive elements
        onOff = (Button) findViewById(R.id.onOff);
        flexion = (Button) findViewById(R.id.flexion);
        extension = (Button) findViewById(R.id.extension);
        speed = (Button) findViewById(R.id.speed);
        descript = (TextView) findViewById(R.id.description);
        up = (Button) findViewById(R.id.up);
        down = (Button) findViewById(R.id.down);

        //on and off button
        //TODO have to add update status so when button is turned off motor stops
        thread();
    }
        public void onOffSelect(View v) {
                if (on) {
                    on = false;
                    onOff.setBackgroundColor(Color.RED);
                    onOff.setText("OFF");
                } else {
                    on = true;
                    onOff.setBackgroundColor(Color.GREEN);
                    onOff.setText("ON");
                }
            }


        public void flexSelect (View c){

                curr = FLEX;
                flexion.setBackgroundColor(Color.GRAY);
                extension.setBackgroundColor(Color.parseColor("#add8e6"));
                speed.setBackgroundColor(Color.parseColor("#add8e6"));
                updateText(curr);
            }

        public void extenSelect(View v) {
                curr = EXTEN;
                flexion.setBackgroundColor(Color.parseColor("#add8e6"));
                extension.setBackgroundColor(Color.GRAY);
                speed.setBackgroundColor(Color.parseColor("#add8e6"));
                updateText(curr);

            }



       public void speedSelect(View v){
                curr = SPD;
                speed.setBackgroundColor(Color.GRAY);
                extension.setBackgroundColor(Color.parseColor("#add8e6"));
                flexion.setBackgroundColor(Color.parseColor("#add8e6"));
                updateText(curr);
            }


        //Controls the different aspcets of the motor with the up and down arrows
        //TODO find a way witht he arduino code the make this work after that should be easy
        public void upSelect(View v){
                if (curr == FLEX) {
                    flexLev++;
                    //THIS IS JUST SAMPLE CODE TO SAY THIS IS HOW THE APP WOULD WORK
                } else if (curr == EXTEN)
                    extenLev++;
                else if (curr == SPD) {
                    spdLev++;
                } else {
                }
                updateText(curr);

            }

       public void downSelect(View v){
                if (curr == FLEX && flexLev != 0)
                    flexLev--;
                else if (curr == EXTEN && extenLev != 0)
                    extenLev--;
                else if (curr == SPD && spdLev != 0) {
                    spdLev--;
                } else {
                }
                updateText(curr);
            }




    public void updateText(int curr) {
        descript = (TextView) findViewById(R.id.description);
        if (curr == FLEX)
            descript.setText("Flexion: " + flexLev);
        if (curr == EXTEN)
            descript.setText("Extension: " + extenLev);
        if (curr == SPD)
            descript.setText("Speed:" + spdLev);
    }

    void thread(){
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



}


