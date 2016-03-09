package com.example.cs160_ej.lordofrepresentatives;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class RepFragment extends Fragment
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

    protected ImageView repImage;
    protected ImageView partyImage;
    protected FrameLayout root;

    private boolean goingToWebsite;
    private boolean pressedViewMoreInfoButton;

    protected HashMap<String, Drawable> partiesToLogos;
    protected HashMap<String, Integer> partiesToColors;

    private static final int SHORT_TOAST_DURATION = 2000;

    protected String website;

    protected String index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rep, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Context context = getContext();
        partiesToLogos = new HashMap<String, Drawable>();
        partiesToLogos.put("Republican", ContextCompat.getDrawable(context, R.drawable.republican));
        partiesToLogos.put("Democrat", ContextCompat.getDrawable(context, R.drawable.democrat));
        partiesToLogos.put("Unknown Party", ContextCompat.getDrawable(context, R.drawable.unknown));
        partiesToLogos.put("Independent", ContextCompat.getDrawable(context, R.drawable.unknown));

        partiesToColors = new HashMap<String, Integer>();
        partiesToColors.put("Republican", Color.rgb(255, 125, 125));
        partiesToColors.put("Democrat", Color.rgb(125, 150, 255));
        partiesToColors.put("Unknown Party", Color.rgb(255, 245, 118));
        partiesToColors.put("Independent", Color.rgb(255, 245, 118));


        nameText = (TextView) view.findViewById(R.id.repName);
        partyText = (TextView) view.findViewById(R.id.partyName);
        emailText = (TextView) view.findViewById(R.id.repEmail);
        tweetText = (TextView) view.findViewById(R.id.tweet);

        root = (FrameLayout) view.findViewById(R.id.root);

        visitWebsiteButton = (Button) view.findViewById(R.id.visitWebsiteButton);
        viewLastTweetButton = (Button) view.findViewById(R.id.viewLastTweetButton);
        viewMoreInfoButton = (Button) view.findViewById(R.id.viewMoreInfoButton);
        visitWebsiteButtonColor = ((ColorDrawable) visitWebsiteButton.getBackground()).getColor();
        viewLastTweetButtonColor = ((ColorDrawable) viewLastTweetButton.getBackground()).getColor();
        viewMoreInfoButtonColor = ((ColorDrawable) viewMoreInfoButton.getBackground()).getColor();

        goingToWebsite = false;
        pressedViewMoreInfoButton = false;

        repImage = (ImageView) view.findViewById(R.id.repImage);
        partyImage = (ImageView) view.findViewById(R.id.partyImage);

        String receivedNameString = nameText.getText().toString();
        String receivedPartyString = partyText.getText().toString();

        Bundle args = getArguments();
        if (args != null)
        {
            receivedNameString = args.getString("name", nameText.getText().toString());
            nameText.setText(receivedNameString);

            receivedPartyString = args.getString("party", partyText.getText().toString());
            partyText.setText(receivedPartyString);
            partyImage.setImageDrawable(partiesToLogos.get(receivedPartyString));
            root.setBackgroundColor(partiesToColors.get(receivedPartyString));

            emailText.setText(args.getString("email", emailText.getText().toString()));
            tweetText.setText(args.getString("lastTweet", tweetText.getText().toString()));
            repImage.setImageResource(args.getInt("repImageReference", R.drawable.unknown));

            website = args.getString("website", "404 error: Website not found");

            index = args.getString("index", "-1");
        }

        visitWebsiteButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, visitWebsiteButtonColor);

                if (!goingToWebsite)
                {
                    Toast.makeText(getActivity(), website, Toast.LENGTH_SHORT).show();
                    goingToWebsite = true;
                    Thread thread = new Thread()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(SHORT_TOAST_DURATION);
                                goingToWebsite = false;
                            }
                            catch (Exception e)
                            {

                            }
                        }
                    };
                    thread.start();
                }

                return true;
            }
        });
        viewLastTweetButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, viewLastTweetButtonColor);
                tweetText.setVisibility(View.VISIBLE);
                return true;
            }
        });
        viewMoreInfoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, viewMoreInfoButtonColor);
                if (!pressedViewMoreInfoButton)
                {
                    pressedViewMoreInfoButton = true;
                    Intent goToDetailed = new Intent(getActivity(), DetailedInfoActivity.class);
                    goToDetailed.putExtra("index", index);
                    startActivity(goToDetailed);
                }
                return true;
            }
        });

        Intent wearIntent = new Intent(getContext(), MobileToWearService.class);
        wearIntent.putExtra("name", receivedNameString);
        wearIntent.putExtra("party", receivedPartyString);
        wearIntent.putExtra("index", index);
        Log.i("index", "index at rep fragment is " + index);
        if (index != null)
        {
            getActivity().startService(wearIntent);
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
