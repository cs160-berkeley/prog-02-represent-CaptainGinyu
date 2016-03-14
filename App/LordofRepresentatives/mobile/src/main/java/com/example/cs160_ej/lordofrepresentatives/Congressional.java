package com.example.cs160_ej.lordofrepresentatives;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Congressional extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener
{
    protected String toAppend;
    protected String receivedZipCode;
    protected TextView congressionalHeader;

    protected Button nextButton;
    protected Button prevButton;
    protected int nextButtonColor;
    protected int prevButtonColor;

    protected int currRepIndex;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    protected double latitude;
    protected double longitude;

    protected Geocoder geocoder;
    protected List<Address> addresses;

    public static ArrayList<WebRepresentativeInfo> repInfo;

    private String initialHeader;

    protected ProgressBar progressBar;

    protected JSONObject repsJson;

    protected String zipCode;

    protected ScrollView scrollView;
    protected RelativeLayout extraFloating;

    private boolean doingGps = false;
    protected String countyName;

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient()
    {
        doingGps = true;
        progressBar.setVisibility(View.VISIBLE);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mGoogleApiClient != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            mGoogleApiClient.connect();
        }
        Log.i("started", "started");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected()))
        {
            mGoogleApiClient.disconnect();
        }
        finish();
        Log.i("stopped", "stopped");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i("resumed", "resumed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i("intent", Boolean.toString(getIntent() == null));
        setContentView(R.layout.activity_congressional);
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reps Viewer");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        extraFloating = (RelativeLayout) findViewById(R.id.extraFloating);
        scrollView.setVisibility(View.GONE);
        extraFloating.setVisibility(View.GONE);


        repInfo = new ArrayList<WebRepresentativeInfo>();

        nextButton = (Button) findViewById(R.id.next);
        prevButton = (Button) findViewById(R.id.previous);
        nextButtonColor = ((ColorDrawable) nextButton.getBackground()).getColor();
        prevButtonColor = ((ColorDrawable) prevButton.getBackground()).getColor();

        receivedZipCode = "-1";
        currRepIndex = 0;

        Intent receivedIntent = getIntent();
        if (receivedIntent != null)
        {
            Bundle extras = receivedIntent.getExtras();
            toAppend = "\n" + extras.getString("to append", "?????");
            if (toAppend.equals("\nZIP code"))
            {
                receivedZipCode = extras.getString("zip");
                zipCode = receivedZipCode;
                toAppend += " " + zipCode;
                new HandleApiStuff(getBaseContext()).execute("http://congress.api.sunlightfoundation.com/legislators/locate?zip="
                        + zipCode
                        + "&apikey=9432749fb814425c909f15ac87ff6495");
            }
            else if (extras.getBoolean("doing gps", false))
            {
                buildGoogleApiClient();

            }

            toAppend += ":";
        }

        congressionalHeader = (TextView) findViewById(R.id.congressionalHeader);
        initialHeader = congressionalHeader.getText().toString();
        congressionalHeader.setText(initialHeader + toAppend);

        nextButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, nextButtonColor);

                if (event.getAction() == MotionEvent.ACTION_UP)
                {

                    if (repInfo.size() > 0)
                    {
                        currRepIndex = (currRepIndex + 1) % repInfo.size();
                        updateRepInfo();
                    }
                }

                return true;
            }
        });
        prevButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeButtonColor(v, event, prevButtonColor);

                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (repInfo.size() > 0)
                    {
                        currRepIndex--;
                        if (currRepIndex < 0)
                        {
                            currRepIndex = repInfo.size() - 1;
                        }
                        updateRepInfo();
                    }
                }

                return true;
            }
        });
    }



    protected void updateRepInfo()
    {
        RepFragment currRepFragment = new RepFragment();
        Bundle argsForFragment = new Bundle();
        WebRepresentativeInfo webRepresentativeInfo = repInfo.get(currRepIndex);

        argsForFragment.putString("name", webRepresentativeInfo.name);
        argsForFragment.putString("party", webRepresentativeInfo.party);
        argsForFragment.putString("email", webRepresentativeInfo.email);
        argsForFragment.putString("website", webRepresentativeInfo.website);
        argsForFragment.putString("lastTweet", webRepresentativeInfo.lastTweet);
        argsForFragment.putString("index", Integer.toString(currRepIndex));

        currRepFragment.setArguments(argsForFragment);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.repInfoDisplay, currRepFragment);

        transaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        Intent toMain = new Intent(this, MainActivity.class);
        if (!receivedZipCode.equals("-1"))
        {
            toMain.putExtra("received zip", (CharSequence) receivedZipCode);
        }
        startActivity(toMain);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean changeButtonColor(View view, MotionEvent motionEvent, int origColor)
    {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            ((Button) view).setBackgroundColor(Color.GREEN);
        } else
        {
            ((Button) view).setBackgroundColor(origColor);
        }

        return true;
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint)
    {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null)
        {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            Log.i("congressional", "latitude is " + Double.toString(latitude));
            Log.i("congressional", "longitude is " + Double.toString(longitude));



            geocoder = new Geocoder(this, Locale.getDefault());
            try
            {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Log.i("congressional", Integer.toString(addresses.size()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (addresses != null)
            {
                if (!doingGps)
                {
                    zipCode = addresses.get(0).getPostalCode();
                    congressionalHeader.setText(initialHeader + "\nZIP code " + zipCode + ":");
                    new HandleApiStuff(getBaseContext()).execute("http://congress.api.sunlightfoundation.com/legislators/locate?zip="
                            + zipCode
                            + "&apikey=9432749fb814425c909f15ac87ff6495");
                }
                else
                {
                    new CountyGetter(getBaseContext()).execute();
                    new HandleApiStuff(getBaseContext()).execute("http://congress.api.sunlightfoundation.com/legislators/locate?" +
                            "latitude=" + latitude + "&longitude=" + longitude
                            + "&apikey=9432749fb814425c909f15ac87ff6495");
                }
            }
            else
            {
                Toast.makeText(this, "Could not get location", Toast.LENGTH_LONG).show();
                TextView noneText = (TextView) findViewById(R.id.noneText);
                noneText.setText("Could not get location!");
                progressBar.setVisibility(View.GONE);
            }


        }
        else
        {
            Toast.makeText(this, "Could not get location", Toast.LENGTH_LONG).show();
            TextView noneText = (TextView) findViewById(R.id.noneText);
            noneText.setText("Could not get location!");
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("Congressional", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause)
    {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("Congressional", "Connection suspended");
        mGoogleApiClient.connect();
    }

    private class CountyGetter extends AsyncTask<Void, Void, String>
    {

        boolean cannotGetCounty;
        Context context;

        public CountyGetter(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
            cannotGetCounty = false;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
                URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?&latlng=" +
                        latitude + "," + longitude);
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
            catch(Exception e)
            {
                Log.e("ERROR", e.getMessage(), e);
                cannotGetCounty = true;
                return null;
            }
        }

        protected void onPostExecute(String response)
        {
            if (cannotGetCounty || response == null)
            {
                Toast.makeText(context, "Could not get location", Toast.LENGTH_LONG).show();
                TextView noneText = (TextView) findViewById(R.id.noneText);
                noneText.setText("Could not get location!");
                progressBar.setVisibility(View.GONE);
            }
            Log.i("GPS RESPONSE", response);

            try
            {
                JSONObject location = new JSONObject(response);
                JSONArray results = location.getJSONArray("results");

                for (int i = 0; i < results.length(); i++)
                {
                    JSONObject curr = (JSONObject) results.get(i);
                    Log.i("curr", curr.toString());
                    JSONArray addressComponents = (JSONArray) curr.get("address_components");
                    JSONObject firstResult = (JSONObject) ((JSONArray) addressComponents).get(0);
                    JSONArray typesArray = firstResult.getJSONArray("types");
                    for (int j = 0; j < typesArray.length(); j++)
                    {

                        Log.i("blah", typesArray.get(j).toString());
                        Log.i("blah2", Boolean.toString(typesArray.get(j).toString().equals("administrative_area_level_2")));
                        if (typesArray.get(j).toString().equals("administrative_area_level_2"))
                        {
                            Log.i("here", "here");

                            Log.i("address components", addressComponents.toString());
                            countyName = firstResult.getString("long_name");
                            Log.i("county name", countyName);
                            congressionalHeader.setText(initialHeader + "\n" + countyName + ":");
                            return;
                        }
                    }
                }
            }
            catch(JSONException e)
            {
                Log.i("Exception!", "Exception!");
            }
        }
    }

    private class HandleApiStuff extends AsyncTask<String, Void, String>
    {
        Context context;
        boolean cannotConnect = false;
        int repsCount;

        public HandleApiStuff(Context context)
        {
            this.context = context;
            repsCount = 0;
        }

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls)
        {
            try
            {
                URL url = new URL(urls[0]);
                Log.i("received url", urls[0]);
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
            catch(Exception e)
            {
                Log.e("ERROR", e.getMessage(), e);
                cannotConnect = true;
                return null;
            }
        }


        protected void getMainInfo(String response)
        {
            if(response == null)
            {
                Toast.makeText(context, "No reps found!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                TextView noneText = (TextView) findViewById(R.id.noneText);
                noneText.setText("Nothing found at " + zipCode);
                return;
            }

            Log.i("INFO", response);
            try
            {
                repsJson = new JSONObject(response);
                repsCount = repsJson.getInt("count");
                if (repsCount < 1)
                {
                    Toast.makeText(context, "No reps found!", Toast.LENGTH_SHORT).show();
                    TextView noneText = (TextView) findViewById(R.id.noneText);
                    noneText.setText("Nothing found at " + zipCode);
                    return;
                }

                String name;
                String party;
                String email;
                String website;
                String lastTweet;

                JSONArray results = repsJson.getJSONArray("results");

                for (int i = 0; i < repsCount; i++)
                {
                    JSONObject currRep = (JSONObject) (results.get(i));
                    name = currRep.getString("first_name");
                    try
                    {
                        String middleName = currRep.getString("middle_name");

                        if (!middleName.equals("null"))
                        {
                            name += " " + middleName;
                        }
                    }
                    catch (JSONException e)
                    {

                    }

                    name += " " + currRep.getString("last_name");

                    String id = currRep.getString("bioguide_id");
                    String imgUrl = "https://theunitedstates.io/images/congress/225x275/" + id + ".jpg";
                    party = currRep.getString("party");
                    Log.i("PARTY!!!", party);
                    if (party.equals("R"))
                    {
                        party = "Republican";
                    }
                    else if (party.equals("D"))
                    {
                        party = "Democrat";
                    }
                    else if (party.equals("I"))
                    {
                        party = "Independent";
                    }
                    email = currRep.getString("oc_email");
                    website = currRep.getString("website");
                    String twitterId = currRep.getString("twitter_id");
                    lastTweet = "";
                    String endOfTerm = currRep.getString("term_end");

                    WebRepresentativeInfo currRepInfo = new WebRepresentativeInfo(name,
                            imgUrl, party, email, website, lastTweet, endOfTerm, id, twitterId);
                    repInfo.add(currRepInfo);
                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        protected void onPostExecute(String response)
        {
            if (cannotConnect)
            {
                Toast.makeText(context, "Cannot connect to Congress Database", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                TextView noneText = (TextView) findViewById(R.id.noneText);
                scrollView.setVisibility(View.VISIBLE);
                
                noneText.setText("Cannot connect to Congress Database");
                return;
            }

            getMainInfo(response);
            if (repsCount > 0)
            {
                updateRepInfo();
                scrollView.setVisibility(View.VISIBLE);
                extraFloating.setVisibility(View.VISIBLE);
            }

            progressBar.setVisibility(View.GONE);
        }
    }
}

