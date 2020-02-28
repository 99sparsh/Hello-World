package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

import helpers.MyEditTextDatePicker;


public class MainActivity extends AppCompatActivity {

    private EditText nameet;
    private EditText emailet;
    private EditText passet;
    private EditText pass2et;
    private EditText dobet;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public void pickdate(View view) {
        new MyEditTextDatePicker(this, R.id.editText5, R.style.DatePickerTheme);
    }


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
    }

    public void SignUp(View view){
        final String name = nameet.getText().toString();
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
                                Map<String,Object>user = new HashMap<String,Object>();
                                user.put("name",name);
                                user.put("email",email);
                                user.put("dob",dob);
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
                            }
                        }
                    });
        }

    }
}
