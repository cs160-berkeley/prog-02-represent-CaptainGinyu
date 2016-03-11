package com.example.cs160_ej.lordofrepresentatives;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Congressional extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener
{
    protected String toAppend;
    protected int receivedZipCode;
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

    public static ArrayList<RepresentativeInfo> dummyRepInfo;

    private String initialHeader;

    static
    {
        setUpDummyRepInfo();
    }

    public static void setUpDummyRepInfo()
    {
        dummyRepInfo = new ArrayList<RepresentativeInfo>();
        RepresentativeInfo rep1 = new RepresentativeInfo(
                "Ian McDiarmid",
                R.drawable.palpatine,
                "Republican",
                "willBeEmperor@hotmail.com",
                "emperorpalpatine.com",
                "Power!!! Unlimited... POWER!!!",
                new DetailedInfo("May 4, 2066", "The Empire"));
        rep1.detailedInfo.billsAndDates.put("2005", "Order 66");
        rep1.detailedInfo.billsAndDates.put("2006", "Death Star Law");
        rep1.detailedInfo.billsAndDates.put("2010", "Anti-Jedi Law");
        RepresentativeInfo rep2 = new RepresentativeInfo(
                "Eric Paulos",
                R.drawable.paulos,
                "Democrat",
                "paulos@paulos.gov",
                "yourdesignisbad.com",
                "I bet that your app is full of bad design.",
                new DetailedInfo("December 31, 2345", "HCI Committee"));
        rep2.detailedInfo.billsAndDates.put("2004", "Anti-Bad-Design Bill");
        rep2.detailedInfo.billsAndDates.put("2005", "Anti-Bad-Design Bill Beta");
        rep2.detailedInfo.billsAndDates.put("2017", "The Good Design Initiative");
        RepresentativeInfo rep3 = new RepresentativeInfo(
                "Donald Duck",
                R.drawable.donald_duck,
                "Independent",
                "makedisneygreatagain@makedisneygreatagain.com",
                "makedisneygreatagain.com",
                "Quack",
                new DetailedInfo("January 22, 3000", "The Grand Duck Legion"));
        rep3.detailedInfo.billsAndDates.put("2052", "Anti-Daffy-Duck Bill");
        rep3.detailedInfo.billsAndDates.put("2055", "Ultimate Quack Bill");
        dummyRepInfo.add(rep1);
        dummyRepInfo.add(rep2);
        dummyRepInfo.add(rep3);
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient()
    {
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
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected()))
        {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        nextButton = (Button) findViewById(R.id.next);
        prevButton = (Button) findViewById(R.id.previous);
        nextButtonColor = ((ColorDrawable) nextButton.getBackground()).getColor();
        prevButtonColor = ((ColorDrawable) prevButton.getBackground()).getColor();

        receivedZipCode = -1;

        setUpDummyRepInfo();
        currRepIndex = 0;

        Intent receivedIntent = getIntent();
        if (receivedIntent != null)
        {
            Bundle extras = receivedIntent.getExtras();
            toAppend = "\n" + extras.getString("to append", "?????");
            if (toAppend.equals("\nZIP code"))
            {
                receivedZipCode = extras.getInt("zip");
                toAppend += " " + Integer.toString(receivedZipCode);
            }
            else if (extras.getBoolean("doing gps", false))
            {
                buildGoogleApiClient();
            }

            toAppend += ":";
        }

        updateRepInfo();

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
                    currRepIndex = (currRepIndex + 1) % dummyRepInfo.size();
                    updateRepInfo();
                }

                return true;
            }
        });
        prevButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeButtonColor(v, event, prevButtonColor);

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    currRepIndex--;
                    if (currRepIndex < 0) {
                        currRepIndex = dummyRepInfo.size() - 1;
                    }
                    updateRepInfo();
                }

                return true;
            }
        });
    }

    protected void updateRepInfo()
    {
        RepFragment currRepFragment = new RepFragment();
        Bundle argsForFragment = new Bundle();
        RepresentativeInfo representativeInfo = dummyRepInfo.get(currRepIndex);

        argsForFragment.putString("name", representativeInfo.name);
        argsForFragment.putInt("repImageReference", representativeInfo.repImageReference);
        argsForFragment.putString("party", representativeInfo.party);
        argsForFragment.putString("email", representativeInfo.email);
        argsForFragment.putString("website", representativeInfo.website);
        argsForFragment.putString("lastTweet", representativeInfo.lastTweet);
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
        if (receivedZipCode != -1)
        {
            toMain.putExtra("received zip", (CharSequence) Integer.toString(receivedZipCode));
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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

            String postalCode;
            if (addresses != null)
            {
                postalCode = addresses.get(0).getPostalCode();
                congressionalHeader.setText(initialHeader + "\n" + postalCode + ":");
            }
            else
            {
                Toast.makeText(this, "Could not detect location", Toast.LENGTH_LONG).show();
            }


        } else
        {
            Toast.makeText(this, "Could not detect location", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("Congressional", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause)
    {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("Congressional", "Connection suspended");
        mGoogleApiClient.connect();
    }
}
