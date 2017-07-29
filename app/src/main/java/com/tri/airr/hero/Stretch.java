package com.tri.airr.hero;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by drossos on 7/26/2017.
 */
public class Stretch extends AppCompatActivity {
    boolean on = false;
    private final int HOLD = 1;
    private final int EXTEN = 2;
    private final int SPD = 3;
    private Button onOff;
    private Button hold;
    private Button extension;
    private Button speed;
    private TextView descript;
    private Button up;
    private Button down;
    //Counter that decides and shows level
    int holdLev = 0;
    int extenLev = 0;
    int spdLev = 0;
    int curr = 0;
    //TODO make sure to make the up and down buttons work
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //Var that shows which is current option selected
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stretch_section);
        //initalizing all buttons and interactive elements
        onOff = (Button) findViewById(R.id.onOff);
        hold = (Button) findViewById(R.id.hold_time);
        extension = (Button) findViewById(R.id.extension);
        speed = (Button) findViewById(R.id.speed);
        descript = (TextView) findViewById(R.id.description);
        up = (Button) findViewById(R.id.up);
        down = (Button) findViewById(R.id.down);


        onOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curr = HOLD;
                hold.setBackgroundColor(Color.GRAY);
                extension.setBackgroundColor(Color.parseColor("#add8e6"));
                speed.setBackgroundColor(Color.parseColor("#add8e6"));
                updateText(curr);
            }

        });
        extension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curr = EXTEN;
                hold.setBackgroundColor(Color.parseColor("#add8e6"));
                extension.setBackgroundColor(Color.GRAY);
                speed.setBackgroundColor(Color.parseColor("#add8e6"));
                updateText(curr);

            }

        });

        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curr = SPD;
                speed.setBackgroundColor(Color.GRAY);
                extension.setBackgroundColor(Color.parseColor("#add8e6"));
                hold.setBackgroundColor(Color.parseColor("#add8e6"));
                updateText(curr);
            }

        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curr == HOLD)
                    holdLev++;
                else if (curr == EXTEN)
                    extenLev++;
                else if (curr == SPD) {
                    spdLev++;
                } else {
                }
                updateText(curr);

            }
        });
        down.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                if (curr == HOLD && holdLev !=0)
                    holdLev--;
                else if (curr == EXTEN && extenLev !=0)
                    extenLev--;
                else if (curr == SPD && spdLev !=0) {
                    spdLev--;
                } else {
                }
                updateText(curr);
            }
        });
    }

    public void updateText(int curr) {
        descript = (TextView) findViewById(R.id.description);
        if (curr == HOLD)
            descript.setText("Hold Lev: " + holdLev);
        if (curr == EXTEN)
            descript.setText("Challenge Level: " + extenLev);
        if (curr == SPD)
            descript.setText("Speed: " + spdLev);
    }
}