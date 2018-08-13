package com.tri.airr.hero;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tri.airr.hero.Authentication;

import java.util.HashMap;

public class Database {
    public static FirebaseDatabase database;
    public static DatabaseReference myRef;
    public static long numGrasps;
    String firebaseTag = "Firebase";
    public static HashMap< String, Object > currPatient;
    public String baseRoot = "/patients";

    public Database () {
        // Write a message to the database
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("/patients/");
        DatabaseReference myRef2 = database.getReference("/patients");
        Query query = myRef2.orderByChild("name").equalTo(Authentication.ENTRY_NAME);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String ext = ((HashMap<String, Object>) dataSnapshot.getValue()).keySet().iterator().next();
                    baseRoot = baseRoot + "/" + ext;
                    myRef = database.getReference(baseRoot);

                    currPatient = ((HashMap<String, Object>) dataSnapshot.child(ext).getValue());
                    numGrasps = (long) currPatient.get("metric1");
                } else {
                    currPatient = initDefMap();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private HashMap < String, Object > initDefMap() {
        HashMap < String, Object > hm = new HashMap < String, Object > ();
        hm.put("id", "defValue");
        hm.put("metric1", 0);
        hm.put("metric2", 0);
        hm.put("metric3", 0);
        hm.put("name", "defName");

        return hm;
    }

}
