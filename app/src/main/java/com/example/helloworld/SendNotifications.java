package com.example.helloworld;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import utils.Coordinates;

public class SendNotifications extends AsyncTask<ArrayList, Void, Void> {
    private final String TAG = "SendNotifications";
    private FirebaseFirestore db;
    @Override
    protected Void doInBackground(ArrayList... arrayLists) {
        ArrayList postInterests = arrayLists[0];
        final String Name = arrayLists[1].get(0).toString();
        final String ownToken = arrayLists[1].get(1).toString();
        final double ownLat = Double.parseDouble(arrayLists[1].get(2).toString());
        final double ownLon = Double.parseDouble(arrayLists[1].get(3).toString());
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereArrayContainsAny("interests",postInterests)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> FCMtokens = new ArrayList<String>();
                        if (task.isSuccessful()) {
                            Coordinates ob = new Coordinates();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> details = (Map<String, Object>) document.getData();
                                if(details.get("location")==null)
                                    continue;
                                Map<String,Double> location = (Map<String, Double>) details.get("location");
                                if(location.get("longitude")==null || location.get("latitude")==null)
                                    continue;

                                if (!ob.checkRange(ownLat, ownLon,  location.get("latitude"), location.get("longitude")))
                                    continue;
                                if (details.get("FCM_Token") == null)
                                    continue;
                                String token = document.getData().get("FCM_Token").toString();
                                if (!TextUtils.isEmpty(token))
                                    if (token.compareTo(ownToken) != 0)
                                        FCMtokens.add(token);
                            }
                                HashMap<String,Object> notif =  new HashMap<String,Object>();
                                notif.put("tokens",FCMtokens);
                                notif.put("subtext","New post!");
                                notif.put("title","New activity nearby!");
                                String msg = Name.substring(0,Name.indexOf(' ')) + " is up to something you are interested in!";
                                notif.put("body",msg);
                                Date date = new Date();
                                notif.put("timestamp",(new Timestamp(date.getTime())).toString());
                                db.collection("notifications")
                                        .document("current")
                                        .update(notif);
                        }else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return null;
    }
}