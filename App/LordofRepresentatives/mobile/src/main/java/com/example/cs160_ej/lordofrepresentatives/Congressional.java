package com.example.cs160_ej.lordofrepresentatives;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Congressional extends AppCompatActivity
{
    protected String toAppend;
    protected int receivedZipCode;
    protected TextView congressionalHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);

        receivedZipCode = -1;

        Bundle extras = getIntent().getExtras();
        toAppend = " " + extras.getString("to append", "Unknown Location");
        if (toAppend.equals(" ZIP code"))
        {
            receivedZipCode = extras.getInt("zip");
            toAppend += " " + Integer.toString(receivedZipCode);
        }

        congressionalHeader = (TextView) findViewById(R.id.congressionalHeader);
        congressionalHeader.setText(congressionalHeader.getText() + toAppend);
    }

    @Override
    public void onBackPressed()
    {
        Intent toMain = new Intent(this, MainActivity.class);
        if (receivedZipCode != -1)
        {
            toMain.putExtra("received zip", (CharSequence) Integer.toString(receivedZipCode));
        }
        startActivity(toMain);
        finish();
    }
}
