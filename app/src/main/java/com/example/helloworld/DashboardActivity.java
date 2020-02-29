package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    private TextView user;
    private String username;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        user = findViewById(R.id.user_name);

    }
    @Override
    public void onResume(){
        super.onResume();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser==null)
            navigate_to_login();
        Intent i=getIntent();
        username = i.getStringExtra("username");
        user.setText(username);
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
}
