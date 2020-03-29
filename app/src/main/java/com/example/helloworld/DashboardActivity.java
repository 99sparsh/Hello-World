package com.example.helloworld;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class DashboardActivity extends AppCompatActivity {
    private TextView user;
    private String username;
    private FirebaseUser fUser;
    private FirebaseFirestore db;
    private LocationRequest mLocationRequest;
    ImageView img;
    private long UPDATE_INTERVAL = 600 * 1000;  /* 10 minutes */
    private static double latitude;
    private static double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

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

        username = fUser.getDisplayName();
        user = findViewById(R.id.user_name);
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
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        loc.put("latitude",location.getLatitude());
        loc.put("longitude",location.getLongitude());
        loc.put("UpdatedAt",(new Timestamp(date.getTime())).toString());
        db.collection("users") //update Firestore
                .document(fUser.getUid())
                .update("location",loc);
        Toast.makeText(this, "Updated location", Toast.LENGTH_SHORT).show(); //remove later
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


    public void navigate_to_posts(View view) {
        startActivity(new Intent(DashboardActivity.this,PostsActivity.class));
    }

}
