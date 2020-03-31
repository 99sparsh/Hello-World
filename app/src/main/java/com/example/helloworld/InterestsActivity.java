package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseUser fUser;
    Chip[] chip=new Chip[20];
    Chip single_chip;
    ChipGroup chipGroup;
    List<String> interests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        load_interests(true);
        //display_chips();
        //cant call here sync problems
    }
    private void setter(List<String> interests)
    {
        this.interests=interests;
        Log.d("setter", "" + interests);
    }
    public void load_interests(final boolean should_display) {
        DocumentReference doc = db.collection("users").document(fUser.getUid());
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                            List<String> interests = (List<String>) document.get("interests");
                            setter(interests);
                            Log.d("output", "" + interests);
                        if (should_display)
                            display_chips();
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
        //display_chips();
    }
    public void display_chips()
    {
        chipGroup= findViewById(R.id.chip_group);
        for(int i=0;i<20;i++)
        //initializing chips because cant initialize inside onComplete for some reason
        //max 20 interests allowed
        {
            chip[i]=new Chip(this);
        }
        for (int i = 0; i < interests.size();i++) {
            chip[i].setCloseIconVisible(true);
            chip[i].setText(interests.get(i));
            //chip[i].setBackgroundColor(Color.parseColor("#03A9F4"));
            chipGroup.addView(chip[i]);

            final int finalI = i;
            chip[i].setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroup.removeView(chip[finalI]);
                    remove_interest((String) chip[finalI].getText());
                }
            });
        }
    }
    public void display_single_chip(String interest)
    {
        single_chip=new Chip(this);
        single_chip.setText(interest);
        single_chip.setCloseIconVisible(true);
        chipGroup.addView(single_chip);
        single_chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(single_chip);
                remove_interest((String) single_chip.getText());
            }
        });
    }
    public void navigate_to_dashboard(View view){
        Intent i = new Intent(this,DashboardActivity.class);
        startActivity(i);
    }
    public String findRoot(String input)
    {
        return input;
    }
    public void add_interest(View view) {
        load_interests(false);
        EditText editText= findViewById(R.id.editText9);
        String interest=findRoot(editText.getText().toString());
        interests.add(interest);
        db.collection("users") //update Firestore
                .document(fUser.getUid())
                .update("interests",interests);
        display_single_chip(interest);
    }
    public void remove_interest(String interest){
        interests.remove(interest);
        db.collection("users") //update Firestore
                .document(fUser.getUid())
                .update("interests",interests);
    }
}



