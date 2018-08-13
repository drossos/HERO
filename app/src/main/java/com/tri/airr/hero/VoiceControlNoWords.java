package com.tri.airr.hero;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.service.voice.AlwaysOnHotwordDetector;
import android.service.voice.VoiceInteractionService;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Calendar;

public class VoiceControlNoWords extends VoiceControl {

    private boolean wordToggle = true;

    private TextView currActionNW;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_voice_control_no_words, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currActionNW = (TextView) getView().findViewById(R.id.current_action_nw);
    }

    @Override
    protected String checkForCommand(String result){
        String str = "";
        if (wordToggle)
            str =  "open";
        else
            str =  "close";
        wordToggle = !wordToggle;
        return str;
    }

    @Override
    public void onEndOfSpeech() {
       super.onEndOfSpeech();
        ArrayList<String> tempArr = new ArrayList<String>();
        tempArr.add("");
       Bundle temp = new Bundle();
       temp.putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, tempArr);
       checkForVoiceMatch(temp);
    }

    @Override
    protected Thread createUIThread(){
        Thread updateUI = new Thread() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //changing color
                        currActionNW.setTextColor(Color.BLACK);
                        Log.i(voiceTag, text + "ing");
                        if (text.equals("open")) {
                            currActionNW.setText("OPENING");
                            currActionNW.setBackgroundColor(getResources().getColor(R.color.openHand));
                            updateMotor.start();
                            numGrasps++;
                        } else if (text.equals("close")){
                            currActionNW.setText("CLOSING");
                            currActionNW.setBackgroundColor(getResources().getColor(R.color.closeHand));
                            updateMotor.start();
                        }else {
                            currActionNW.setText("RELAXING");
                            currActionNW.setBackgroundColor(getResources().getColor(R.color.material_grey_100));
                            updateMotor.start();
                        }
                        //spR.startListening(intent);
                    }
                });
            }
        };

        return updateUI;
    }



}
