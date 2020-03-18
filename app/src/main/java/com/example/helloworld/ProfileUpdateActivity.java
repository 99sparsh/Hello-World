package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class ProfileUpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        Toast.makeText(this, "Create Profile", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        Toast.makeText(this, "Resume Profile", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_profile_update);
    }
}
