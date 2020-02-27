package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import helpers.MyEditTextDatePicker;


public class MainActivity extends AppCompatActivity {


    public void pickdate(View view) {
        new MyEditTextDatePicker(this, R.id.editText5, R.style.DatePickerTheme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
