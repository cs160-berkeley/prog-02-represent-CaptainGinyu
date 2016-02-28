package com.example.cs160_ej.lordofrepresentatives;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Congressional extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
