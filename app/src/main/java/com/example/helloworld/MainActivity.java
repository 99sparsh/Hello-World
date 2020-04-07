package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import helpers.MyEditTextDatePicker;
import helpers.Post;


public class MainActivity extends AppCompatActivity {

    private EditText nameet;
    private EditText emailet;
    private EditText passet;
    private EditText pass2et;
    private EditText dobet;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private AlertDialog alertDialog;
    public void pickdate(View view) {
        new MyEditTextDatePicker(this, R.id.editText5);
    }
    public void okClicked(View view){
        alertDialog.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void locationPermission(){
        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        if(permissionAccessFineLocationApproved){
            boolean backgroundLocationPermissionApproved =
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;
            if(backgroundLocationPermissionApproved){
                Log.e("LOC","APPROVED");
            }
            else{
                //Request for permission
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION},1);
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    }, 1);

        }

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.permission_dialog, viewGroup, false);
            builder.setView(dialogView);
            alertDialog = builder.create();
            alertDialog.show();
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        user = auth.getCurrentUser();
        if(user!=null)
            navigateToDashboard();
    }
    public void navigateToDashboard(){
        startActivity(new Intent(this,DashboardActivity.class));
    }

    //com.google.firebase.auth.FirebaseAuthWeakPasswordException:
    // The given password is invalid. [ Password should be at least 6 characters ]

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameet = findViewById(R.id.editText);
        emailet = findViewById(R.id.editText2);
        passet = findViewById(R.id.editText3);
        pass2et = findViewById(R.id.editText4);
        dobet = findViewById(R.id.editText5);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        locationPermission();
    }

    public void SignUp(View view){
        final String name = nameet.getText().toString()+" ";
        final String email = emailet.getText().toString();
        final String pass = passet.getText().toString();
        final String pass2 = pass2et.getText().toString();
        final String dob = dobet.getText().toString();
        if(TextUtils.isEmpty(name))
            Toast.makeText(this,"Please enter name", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(email))
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(pass))
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(pass2))
            Toast.makeText(this,"Please confirm password", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(dob))
            Toast.makeText(this,"Please enter birthday", Toast.LENGTH_SHORT).show();
        else if(pass.compareTo(pass2)!=0)
            Toast.makeText(this,"Passwords do not match", Toast.LENGTH_SHORT).show();
        else{
            auth.createUserWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                    Toast.makeText(getApplicationContext(),"This Email is already registered",Toast.LENGTH_SHORT).show();
                                else {
                                    Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                                    Log.e("error", task.getException().toString());
                                }
                            }
                            else {
                                String uid = auth.getCurrentUser().getUid();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                auth.getCurrentUser().updateProfile(profileUpdates);
                                Map<String,Object>user = new HashMap<String,Object>();
                                user.put("name",name);
                                user.put("email",email);
                                user.put("dob",dob);
                                user.put("interests", Arrays.asList("coffee","movie")); //default interests
                                db.collection("users").document(uid)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void aVoid) {
                                                  Log.d("User write", "DocumentSnapshot successfully written!");
                                              }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("user write", "Error writing document", e);
                                            }
                                        });
                                Toast.makeText(getApplicationContext(), "User successfully registered!", Toast.LENGTH_SHORT).show();
                                navigateToDashboard();
                            }
                        }
                    });
        }

    }
    public void SignIn(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
