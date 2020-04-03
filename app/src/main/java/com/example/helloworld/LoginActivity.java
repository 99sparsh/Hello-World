package com.example.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {
    private EditText emailet;
    private EditText passet;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private final String TAG = "LoginActivity";

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailet = findViewById(R.id.editText6);
        passet = findViewById(R.id.editText7);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
    @Override
    public void onResume(){
        super.onResume();
        user = auth.getCurrentUser();
        if(user!=null)
            getFCMTokenAndRedirect();
    }
    public void login(View view){
        final String email = emailet.getText().toString();
        final String pass = passet.getText().toString();
        if(TextUtils.isEmpty(email))
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(pass))
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
        else {
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                getFCMTokenAndRedirect();
                                Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                    Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                else {
                                    Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        }
                    });
        }
    }
    public void navigate_to_signup(View view){
        Intent I = new Intent(this, MainActivity.class);
        startActivity(I);
    }
    public void getFCMTokenAndRedirect(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.w(TAG,task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        user = auth.getCurrentUser();
                        db.collection("users")
                                .document(user.getUid())
                                .update("FCM_Token",token);
                    }
                });
        startActivity(new Intent(this,DashboardActivity.class));
    }
}
