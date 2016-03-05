package com.example.cs160_ej.lordofrepresentatives;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

public class RealMain extends Activity
{
    protected TextView nameText;
    protected TextView partyText;
    protected Button detailsButton;
    protected int detailsButtonColor;
    protected RelativeLayout root;

    protected HashMap<String, Integer> partiesToColors;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);

        partiesToColors = new HashMap<String, Integer>();
        partiesToColors.put("Republican", Color.rgb(255, 125, 125));
        partiesToColors.put("Democrat", Color.rgb(125, 150, 255));
        partiesToColors.put("Unknown Party", Color.rgb(255, 245, 118));
        partiesToColors.put("Independent", Color.rgb(255, 245, 118));

        nameText = (TextView) findViewById(R.id.name);
        partyText = (TextView) findViewById(R.id.party);
        detailsButton = (Button) findViewById(R.id.toDetailedViewButton);
        root = (RelativeLayout) findViewById(R.id.root);

        Intent receivedIntent = getIntent();

        if (receivedIntent != null)
        {
            Bundle extras = receivedIntent.getExtras();

            nameText.setText(extras.getString("name", nameText.getText().toString()));
            String receivedPartyString = extras.getString("party", partyText.getText().toString());
            partyText.setText(receivedPartyString);
            root.setBackgroundColor(partiesToColors.get(receivedPartyString));
        }

        detailsButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                /*if (!leavingActivity)
                {
                    leavingActivity = true;
                    Intent intent = new Intent(getBaseContext(), WearToMobileService.class);

                    startService(intent);
                }*/
                Log.i("HEY", "pressed");
                Intent intent = new Intent(getBaseContext(), WearToMobileService.class);
                startService(intent);

                return true;
            }
        });
    }
}
