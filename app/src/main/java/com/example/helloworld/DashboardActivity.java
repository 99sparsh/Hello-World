package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class DashboardActivity extends AppCompatActivity {
    private TextView user;
    private String username;
    private FirebaseUser fUser;
    private FirebaseFirestore db;
    private LocationRequest mLocationRequest;
    ImageView img;
    private long UPDATE_INTERVAL = 600 * 1000;  /* 10 minutes */

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = FirebaseFirestore.getInstance();
        startLocationUpdates();
    }

    @Override
    public void onResume(){
        super.onResume();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser==null)
            navigate_to_login();
        Intent i=getIntent();
        username = i.getStringExtra("username");
        user = findViewById(R.id.user_name);

        DocumentReference doc = db.collection("users").document(fUser.getUid());
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        username = document.getString("name");
                    }
                    else{
                        Log.d("fetchName","No data");
                    }
                }
                else{
                    Log.d("fetchname","get failed with", task.getException());
                }
            }
        });
        user.setText(username);
        img=(ImageView)findViewById(R.id.user_photo) ;
        if(fUser!=null)
        {
            if(fUser.getPhotoUrl()!=null)
            {
                Glide.with(this).load(fUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(img);
            }
        }
    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);


        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }
    public void onLocationChanged(Location location) {
        //Location has now been determined
        Map<String,Object> loc = new HashMap<String,Object>();
        Date date = new Date();
        loc.put("latitude",location.getLatitude());
        loc.put("longitude",location.getLongitude());
        loc.put("UpdatedAt",(new Timestamp(date.getTime())).toString());
        db.collection("users") //update Firestore
                .document(fUser.getUid())
                .update("location",loc);
        Toast.makeText(this, "Updated location", Toast.LENGTH_SHORT).show(); //remove later
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DASHBOARD_ACTIVITY", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }




    public void navigate_to_login() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
    }

    public void profile(View view){
        Intent i = new Intent(this,ProfileUpdateActivity.class);
        //i.putExtra("fUser",""+fUser);
        startActivity(i);
    }

}
