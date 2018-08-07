package com.tri.airr.hero;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import static com.tri.airr.hero.MainActivity.mAuth;

public class Authentication extends Activity {

    private EditText emailReg;
    private EditText passwordReg;
    private EditText username;
    private String TAG = "Firebase Info";
    public static String ENTRY_NAME;
    public static SharedPreferences prefs;
    public static String userPref = "username";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        emailReg = (EditText) findViewById(R.id.email_reg);
        passwordReg = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void createUser(View v) {
        ENTRY_NAME = username.getText().toString();
        String email = emailReg.getText().toString();
        String password = passwordReg.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();
                            prefs.edit().putString(userPref,ENTRY_NAME).commit();
                            returnToMain();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Authentication.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    public void loginUser(View v){
        ENTRY_NAME = username.getText().toString();
        String email = emailReg.getText().toString();
        String password = passwordReg.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            prefs.edit().putString(userPref,ENTRY_NAME).commit();
                            returnToMain();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Authentication.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }


    private void returnToMain() {
        Intent intent = new Intent(Authentication.this, MainActivity.class);
        startActivity(intent);
    }

}
