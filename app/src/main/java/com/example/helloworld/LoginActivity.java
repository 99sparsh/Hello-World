package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText emailet;
    private EditText passet;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    String username;
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
            updateUI(user);
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

                                Log.d("LOGIN", "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                updateUI(user);
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
    public void updateUI(FirebaseUser user){

        String uid = user.getUid();
        final String email=user.getEmail();
        DocumentReference doc = db.collection("users").document(uid);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        username = document.getString("name");
                        navigate_to_dashboard(username,email);
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

    }
    public void navigate_to_signup(View view){
        Intent I = new Intent(this, MainActivity.class);
        startActivity(I);
    }
    public void navigate_to_dashboard(String username, String email){
        Intent I = new Intent(this,DashboardActivity.class);
        I.putExtra("username",""+username);
        I.putExtra("email",email);
        startActivity(I);
    }
}
