package com.tri.airr.hero;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Home extends AppCompatActivity {
    public static BottomNavigationView bot_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        goTo(new MainActivity());
        bot_nav = (BottomNavigationView) findViewById(R.id.bot_nav);

        bot_nav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.nav_daily:
                                goTo(new Daily());
                                break;

                            case R.id.nav_stretch:
                                goTo(new Exercise());
                                break;

                            case R.id.nav_results:
                                goTo(new Results());
                                break;

                            case R.id.nav_main:
                                goTo(new MainActivity());
                                break;

                        }
                        return true;
                    }
                }
        );
    }

    public void goTo(Object dest){
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.content, (Fragment) dest);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }

    public void connectBluetooth(View v) {
        Intent intent = new Intent(Home.this, BluetoothConnect.class);
        startActivity(intent);
    }

    public void goToVoiceControl (View v) {
        Intent intent = new Intent(Home.this, VoiceControl.class);
        startActivity(intent);
    }

    public void goToVoiceNWControl(View v){
        Intent intent = new Intent(Home.this, VoiceControlNoWords.class);
        startActivity(intent);
    }

    public void goToEmail(View v) {
        Intent intent = new Intent(Home.this, Email.class);
        startActivity(intent);
    }
}
