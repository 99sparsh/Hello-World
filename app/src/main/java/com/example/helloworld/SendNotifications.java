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

import java.util.ArrayList;
import java.util.HashMap;

public class SendNotifications extends AsyncTask<ArrayList, Void, Void> {
    private final String TAG = "SendNotifications";
    private FirebaseFirestore db;
    @Override
    protected Void doInBackground(ArrayList... arrayLists) {
        ArrayList postInterests = arrayLists[0];
        final String Name = arrayLists[1].get(0).toString();
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereArrayContainsAny("interests",postInterests)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> FCMtokens = new ArrayList<String>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(document.getData().get("FCM_Token")==null)
                                    continue;
                                String token = document.getData().get("FCM_Token").toString();
                                if(!TextUtils.isEmpty(token))
                                    FCMtokens.add(token);
                                HashMap<String,Object> notif =  new HashMap<String,Object>();
                                notif.put("tokens",FCMtokens);
                                notif.put("subtext","New post!");
                                notif.put("title","New activity nearby!");
                                String msg = Name.substring(0,Name.indexOf(' ')) + " is up to something you are interested in";
                                notif.put("body",msg);
                                db.collection("notifications")
                                        .document("current")
                                        .update(notif);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return null;
    }

}