package com.example.cs160_ej.lordofrepresentatives;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class DetailedInfoActivity extends AppCompatActivity
{
    protected TextView nameText;
    protected TextView partyText;
    protected TextView emailText;
    protected TextView committeeText;
    protected TextView billsText;
    protected TextView endOfTermText;

    protected ImageView repImage;
    protected ImageView partyImage;

    protected HashMap<String, Drawable> partiesToLogos;
    protected HashMap<String, Integer> partiesToColors;

    protected ProgressBar progressBar;

    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_info);

        Context context = getBaseContext();
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

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar = (ProgressBar) findViewById(R.id.progressBarDetailed);

        nameText = (TextView) findViewById(R.id.repName);
        partyText = (TextView) findViewById(R.id.partyName);
        repImage = (ImageView) findViewById(R.id.repImage);
        partyImage = (ImageView) findViewById(R.id.partyImage);
        endOfTermText = (TextView) findViewById(R.id.endOfTerm);
        emailText = (TextView) findViewById(R.id.repEmail);
        committeeText = (TextView) findViewById(R.id.committee);
        billsText = (TextView) findViewById(R.id.billsList);

        Intent receivedIntent = getIntent();

        if (receivedIntent != null)
        {
            Bundle extras = receivedIntent.getExtras();

            if (extras != null)
            {
                index = Integer.parseInt(extras.getString("index"));

                if (index > -1)
                {
                    WebRepresentativeInfo currRep = Congressional.repInfo.get(index);
                    //DetailedInfo repDetailedInfo = currRep.detailedInfo;
                    nameText.setText(currRep.name);
                    partyText.setText(currRep.party);
                    Picasso.with(context).load(currRep.repImgUrl).into(repImage);
                    partyImage.setImageDrawable(partiesToLogos.get(currRep.party));
                    endOfTermText.setText("End of Term: " + currRep.endOfTerm);
                    emailText.setText(currRep.email);

                    Log.i("curr bio id", currRep.bio_id);
                    new HandleApiStuff(context, "committee").execute(currRep.bio_id);
                    new HandleApiStuff(context, "bills").execute(currRep.bio_id);


                    //committeeText.setText("Committee(s): " + repDetailedInfo.committeeName);

                    /*for (String key : repDetailedInfo.billsAndDates.keySet())
                    {
                        billsTextContent += key + ": " + repDetailedInfo.billsAndDates.get(key) + "\n";
                    }
                    billsText.setText(billsTextContent);*/
                }
            }
        }
    }

    private class HandleApiStuff extends AsyncTask<String, Void, String>
    {
        Context context;
        boolean cannotConnect = false;
        String type;

        public HandleApiStuff(Context context, String type)
        {
            this.context = context;
            this.type = type;
        }

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... bio_id)
        {
            if (type.equals("committee"))
            {
                try
                {
                    URL url = new URL("http://congress.api.sunlightfoundation.com/committees?member_ids="
                            + Congressional.repInfo.get(index).bio_id
                            + "&apikey=9432749fb814425c909f15ac87ff6495");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try
                    {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null)
                        {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        return stringBuilder.toString();
                    }
                    finally
                    {
                        urlConnection.disconnect();
                    }
                }
                catch (Exception e)
                {
                    Log.e("ERROR", e.getMessage(), e);
                    cannotConnect = true;
                    return null;
                }
            }
            else
            {
                try
                {
                    URL url = new URL("http://congress.api.sunlightfoundation.com/bills?sponsor_id="
                            + Congressional.repInfo.get(index).bio_id
                            + "&apikey=9432749fb814425c909f15ac87ff6495");

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try
                    {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null)
                        {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        return stringBuilder.toString();
                    }
                    finally
                    {
                        urlConnection.disconnect();
                    }
                }
                catch (Exception e)
                {
                    Log.e("ERROR", e.getMessage(), e);
                    cannotConnect = true;
                    return null;
                }

            }
        }


        protected void getDetailedInfo(String response)
        {
            if(response == null)
            {
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (type.equals("committee"))
            {
                Log.i("COMMITTEE RESPONSE", response);
                try
                {
                    JSONObject repsJson = new JSONObject(response);
                    int count = repsJson.getInt("count");
                    if (count < 1)
                    {
                        return;
                    }

                    String committees = "";

                    JSONArray results = repsJson.getJSONArray("results");

                    for (int i = 0; i < count; i++)
                    {
                        JSONObject curr = (JSONObject) (results.get(i));
                        String currName = curr.getString("name");
                        committees += "\n" + " • "  + currName + "\n";
                        Log.i("committee", currName);
                    }

                    if (committees.equals(""))
                    {
                        committees = "None";
                    }

                    committeeText.setText(committees);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.i("BILLS RESPONSE", response);
                try
                {
                    JSONObject repsJson = new JSONObject(response);

                    String bills = "";

                    JSONArray results = repsJson.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++)
                    {
                        JSONObject curr = (JSONObject) (results.get(i));
                        String currName = curr.getString("short_title");
                        if (currName.equals("null"))
                        {
                            currName = curr.getString("official_title");
                        }
                        bills += "<br>" + " • " + curr.getString("introduced_on") + ": <i>" + currName + "</i><br>";
                        Log.i("curr name", currName);
                    }

                    if (bills.equals(""))
                    {
                        bills = "None";
                    }

                    billsText.setText(Html.fromHtml(bills));

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }

        protected void onPostExecute(String response)
        {
            if (cannotConnect)
            {
                Toast.makeText(context, "Cannot connect to Congress Database", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            getDetailedInfo(response);

            progressBar.setVisibility(View.GONE);
        }
    }
}
