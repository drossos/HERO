package com.tri.airr.hero;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.*;

/**
 * Created by Daniel on 7/28/2017.
 */

public class Email extends AppCompatActivity {

    private EditText email,subject,message,error;
     Button send;
     Intent intent;
     Pattern pattern;
     Matcher matcher;
     final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_screen);

        pattern = Pattern.compile(EMAIL_REGEX);
        email = (EditText) findViewById(R.id.email);
        email = (EditText) findViewById(R.id.email);
        subject = (EditText) findViewById(R.id.subject);
        message = (EditText) findViewById(R.id.message);
        error = (EditText) findViewById((R.id.errorMessage));
        send = (Button) findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = false;
                String reciptient = email.getText().toString();
                String subj = subject.getText().toString();
                String messg = message.getText().toString();


                    valid = validCheck (reciptient);
                    if (valid) {
                        intent = new Intent(intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{reciptient});
                        intent.putExtra(Intent.EXTRA_SUBJECT, subj);
                        intent.putExtra(Intent.EXTRA_TEXT, messg);
                        error.setText("");
                        intent.setType("message/rfc822");
                        startActivity(Intent.createChooser(intent, "Select Email App"));
                    }
                    else
                        error.setText("Invalid Email");


            }
        });
    }   //TODO consider getting better regex and also adding more paramaters for subj and message

    public boolean validCheck (String recipient){
        matcher = pattern.matcher(recipient);
       return matcher.matches();
    }


}
