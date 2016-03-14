package com.example.cs160_ej.lordofrepresentatives;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class RepFragment extends Fragment
{
    private static final String TWITTER_KEY = "8CI1AiQ5zD8JyGFioPnmTptqy";
    private static final String TWITTER_SECRET = "P5BLEQQz6krrSQXaLtdzhLHoJMhs5t25fUd3ML45hbyJ4f7Geo";

    protected TextView nameText;
    protected TextView partyText;
    protected TextView emailText;

    protected Button visitWebsiteButton;
    protected Button viewMoreInfoButton;
    protected int visitWebsiteButtonColor;
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
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        final Context context = getContext();
        partiesToLogos = new HashMap<String, Drawable>();
        partiesToLogos.put("Republican", ContextCompat.getDrawable(context, R.drawable.republican));
        partiesToLogos.put("Democrat", ContextCompat.getDrawable(context, R.drawable.democrat));
        partiesToLogos.put("Unknown Party", ContextCompat.getDrawable(context, R.drawable.unknown));
        partiesToLogos.put("Independent", ContextCompat.getDrawable(context, R.drawable.unknown));

        partiesToColors = new HashMap<String, Integer>();
        partiesToColors.put("Republican", Color.rgb(237, 165, 164));
        partiesToColors.put("Democrat", Color.rgb(164, 204, 237));
        partiesToColors.put("Unknown Party", Color.rgb(255, 245, 118));
        partiesToColors.put("Independent", Color.rgb(255, 245, 118));


        nameText = (TextView) view.findViewById(R.id.repName);
        partyText = (TextView) view.findViewById(R.id.partyName);
        emailText = (TextView) view.findViewById(R.id.repEmail);

        root = (FrameLayout) view.findViewById(R.id.root);

        visitWebsiteButton = (Button) view.findViewById(R.id.visitWebsiteButton);
        viewMoreInfoButton = (Button) view.findViewById(R.id.viewMoreInfoButton);
        visitWebsiteButtonColor = ((ColorDrawable) visitWebsiteButton.getBackground()).getColor();
        viewMoreInfoButtonColor = ((ColorDrawable) viewMoreInfoButton.getBackground()).getColor();

        goingToWebsite = false;
        pressedViewMoreInfoButton = false;

        repImage = (ImageView) view.findViewById(R.id.repImage);
        partyImage = (ImageView) view.findViewById(R.id.partyImage);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(context, new Twitter(authConfig));

        Bundle args = getArguments();
        if (args != null)
        {
            index = args.getString("index", "-1");
            if (Integer.parseInt(index) >= 0)
            {
                final WebRepresentativeInfo currRep = Congressional.repInfo.get(Integer.parseInt(index));
                nameText.setText(currRep.name);

                String receivedPartyString = currRep.party;
                partyText.setText(receivedPartyString);
                if (partiesToLogos.get(receivedPartyString) == null)
                {
                    partyImage.setImageDrawable(partiesToLogos.get("Independent"));
                }
                else
                {
                    partyImage.setImageDrawable(partiesToLogos.get(receivedPartyString));
                }
                if (partiesToColors.get(receivedPartyString) == null)
                {
                    root.setBackgroundColor(partiesToColors.get("Independent"));
                }
                else
                {
                    root.setBackgroundColor(partiesToColors.get(receivedPartyString));
                }

                emailText.setText(currRep.email);

                TwitterCore.getInstance().logInGuest(new Callback<AppSession>()
                {
                    final View _view = view;

                    @Override
                    public void success(Result<AppSession> result)
                    {
                        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                        StatusesService statusesService = twitterApiClient.getStatusesService();
                        statusesService.userTimeline(null, currRep.twitter_id, 1, null, null, false,
                                false, false, true, new Callback<List<Tweet>>()
                                {
                                    @Override
                                    public void success(Result<List<Tweet>> result)
                                    {
                                        Log.i("tweet", result.data.get(0).text);
                                        Log.i("tweet id", Long.toString(result.data.get(0).id));

                                        final ViewGroup parentView
                                                = (FrameLayout) _view.findViewById(R.id.tweetContainer);

                                        long tweetId = result.data.get(0).id;
                                        TweetUtils.loadTweet(tweetId, new Callback<Tweet>()
                                        {
                                            @Override
                                            public void success(Result<Tweet> result)
                                            {
                                                TweetView tweetView = new TweetView(context, result.data);
                                                parentView.addView(tweetView);
                                            }

                                            @Override
                                            public void failure(TwitterException exception)
                                            {
                                                Log.d("TwitterKit", "Load Tweet failure", exception);
                                            }
                                        });

                                    }

                                    @Override
                                    public void failure(TwitterException e)
                                    {

                                    }
                                });
                    }

                    @Override
                    public void failure(TwitterException e)
                    {

                    }
                });

                Picasso.with(context).load(currRep.repImgUrl).into(repImage);

                website = currRep.website;
            }




        }

        visitWebsiteButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, visitWebsiteButtonColor);

                if (!goingToWebsite)
                {
                    //Toast.makeText(getActivity(), website, Toast.LENGTH_SHORT).show();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    startActivity(browserIntent);

                    goingToWebsite = true;
                    /*Thread thread = new Thread()
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
                    thread.start();*/
                }

                return true;
            }
        });
        viewMoreInfoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, viewMoreInfoButtonColor);
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    pressedViewMoreInfoButton = true;
                    if (Congressional.repInfo.size() > 0)
                    {
                        Intent goToDetailed = new Intent(getActivity(), DetailedInfoActivity.class);
                        goToDetailed.putExtra("index", index);
                        startActivity(goToDetailed);
                    }
                }

                return true;
            }
        });

        Intent wearIntent = new Intent(getContext(), MobileToWearService.class);
        if (Congressional.repInfo != null && Congressional.repInfo.size() > 0)
        {
            try
            {
                wearIntent.putExtra("name", Congressional.repInfo.get(Integer.parseInt(index)).name);
                wearIntent.putExtra("party", Congressional.repInfo.get(Integer.parseInt(index)).party);
                wearIntent.putExtra("index", index);
            }
            catch (Exception e)
            {

            }
        }
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
