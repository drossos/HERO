package com.tri.airr.hero;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.service.voice.AlwaysOnHotwordDetector;
import android.service.voice.VoiceInteractionService;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;

import static com.tri.airr.hero.BluetoothConnect.connected;

public class VoiceControl extends AppCompatActivity implements RecognitionListener{

    Button voiceListen;
    private static final int SPEECH_REQUEST_CODE = 100;
    private static final int REQUEST_AUDIO =1;
    private int speechRequestCode;
    private Intent intent;
    private  AudioManager audioManager;
    private TextView currAction;
    private BluetoothConnect blc;
    SpeechRecognizer spR;
    VoiceInteractionService voiceService;
    AlwaysOnHotwordDetector hotwordDetector;
    AlwaysOnHotwordDetector.Callback callback;

    String voiceTag = "Hotword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_control);

        currAction = (TextView) findViewById(R.id.current_action);
        blc = new BluetoothConnect();
        if (this.checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs acess to mic");
            builder.setMessage("Please grant audio record access so this app");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO);
                }
            });
            builder.show();
        }

        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);


        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        //Used to modify the number of similar sounds words
       //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
       // startActivityForResult(intent, SPEECH_REQUEST_CODE);

        spR = SpeechRecognizer.createSpeechRecognizer(this);
        spR.setRecognitionListener(this);
        spR.startListening(intent);

    }

    public void voiceListen(View v) {


            // Create an intent that can start the Speech Recognizer activity
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            // Start the activity, the intent will be populated with the speech text
            startActivityForResult(intent, speechRequestCode);

    }

    public void voiceListen() {
        // Create an intent that can start the Speech Recognizer activity
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, speechRequestCode);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Log.i(BluetoothConnect.TAG, spokenText);

            if (connected && (spokenText.equals("open") || spokenText.equals("close"))){
            blc.handToggle();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
        voiceListen();
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(voiceTag, "ready for speech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(voiceTag, "beginning of speech");
    }

    @Override
    public void onRmsChanged(float v) {
        //Log.i(voiceTag, "rms has changed!!!");
    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {
        Log.i(voiceTag, "error");
        spR.startListening(intent);
    }

    @Override
    public void onResults(Bundle bundle) {
        Log.i(voiceTag, "onResults");
        ArrayList<String> matches = bundle
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches) {
            text = checkForCommand(result);
            if (text != "")
                break;
        }

        if (connected && !text.equals("")){
            BluetoothConnect blc = new BluetoothConnect();
            blc.handToggle(text);
            //changing color
            currAction.setTextColor(Color.BLACK);
            Log.i(voiceTag, text+"ing");
            if(text.equals("open")){
                currAction.setText("OPENING");
                currAction.setBackgroundColor(getResources().getColor(R.color.openHand));
            } else {
                currAction.setText("CLOSING");
                currAction.setBackgroundColor(getResources().getColor(R.color.closeHand));
            }
        }
        spR.startListening(intent);
    }

    private String checkForCommand(String result) {
        String[] words = result.split(" ");
        for (int i =0; i < words.length; i++){
            if (words[i].equals("open") || words[i].equals("close"))
                return words[i];
        }
        return "";
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

    }
}


