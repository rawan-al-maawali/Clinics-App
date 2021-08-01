package com.example.favoritelist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class first_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
    }

    public void goToAboutUs(View view) {
        Intent intent = new Intent(this, AboutUs.class);
        startActivity(intent);
    }

    public void goToList(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}