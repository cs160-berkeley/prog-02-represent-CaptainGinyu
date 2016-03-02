package com.example.cs160_ej.lordofrepresentatives;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RepFragmentActivity extends AppCompatActivity
{
    protected TextView nameText;
    protected TextView partyText;
    protected TextView emailText;
    protected TextView tweetText;

    protected Button visitWebsiteButton;
    protected Button viewLastTweetButton;
    protected Button viewMoreInfoButton;
    protected int visitWebsiteButtonColor;
    protected int viewLastTweetButtonColor;
    protected int viewMoreInfoButtonColor;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rep);
        Log.i("WHEEEEEEEEEEE", "WHOOOOOOOOOOOOOOOO");
        nameText = (TextView) findViewById(R.id.repName);
        partyText = (TextView) findViewById(R.id.partyName);
        emailText = (TextView) findViewById(R.id.repEmail);
        tweetText = (TextView) findViewById(R.id.tweet);

        visitWebsiteButton = (Button) findViewById(R.id.visitWebsiteButton);
        viewLastTweetButton = (Button) findViewById(R.id.viewLastTweetButton);
        viewMoreInfoButton = (Button) findViewById(R.id.viewMoreInfoButton);
        visitWebsiteButtonColor = ((ColorDrawable) visitWebsiteButton.getBackground()).getColor();
        viewLastTweetButtonColor = ((ColorDrawable) viewLastTweetButton.getBackground()).getColor();
        viewMoreInfoButtonColor = ((ColorDrawable) viewMoreInfoButton.getBackground()).getColor();

        visitWebsiteButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, visitWebsiteButtonColor);
                return true;
            }
        });
        viewLastTweetButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, viewLastTweetButtonColor);
                return true;
            }
        });
        viewMoreInfoButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, viewMoreInfoButtonColor);
                return true;
            }
        });


        Intent receivedIntent = getIntent();
        if (receivedIntent != null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras != null)
            {
                nameText.setText(extras.getString("name", nameText.getText().toString()));
                partyText.setText(extras.getString("party", partyText.getText().toString()));
                emailText.setText(extras.getString("email", emailText.getText().toString()));
                tweetText.setText(extras.getString("lastTweet", tweetText.getText().toString()));
            }
        }

    }

    public boolean changeButtonColor(View view, MotionEvent motionEvent, int origColor)
    {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            ((Button) view).setBackgroundColor(Color.GREEN);
        }
        else
        {
            ((Button) view).setBackgroundColor(origColor);
        }

        return true;
    }
}
