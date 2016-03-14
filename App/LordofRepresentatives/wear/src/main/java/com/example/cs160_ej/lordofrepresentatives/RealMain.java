package com.example.cs160_ej.lordofrepresentatives;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

public class RealMain extends Activity implements SensorEventListener
{
    protected TextView nameText;
    protected TextView partyText;
    protected Button detailsButton;
    protected RelativeLayout root;

    protected HashMap<String, Integer> partiesToColors;

    protected String index;
    protected String name;
    protected String party;
    protected String total;

    private float y1, y2;
    private final int MIN_SWIPE_DIST = 50;

    private SensorManager manager;
    private Sensor accel;
    private float x = Float.NaN;
    private float y = Float.NaN;
    private float z = Float.NaN;

    private final int MIN_SHAKE_SPEED = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

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
            Log.i("real main intent", "received intent");
            Bundle extras = receivedIntent.getExtras();

            name = extras.getString("name", nameText.getText().toString());
            nameText.setText(name);
            party = extras.getString("party", partyText.getText().toString());
            partyText.setText(party);
            if (partiesToColors.get(party) == null)
            {
                root.setBackgroundColor(partiesToColors.get("Independent"));
            }
            else
            {
                root.setBackgroundColor(partiesToColors.get(party));
            }
            index = extras.getString("index", "-1");
            total = extras.getString("total");
            Log.i("real main intent", name);
            Log.i("real main intent", party);
        }

        detailsButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    Intent intent = new Intent(getBaseContext(), WearToMobileService.class);
                    intent.putExtra("button", true);
                    intent.putExtra("index", index);
                    Log.i("index", "index at real main is " + index);
                    if (index != null)
                    {
                        startService(intent);
                    }
                }

                return true;
            }
        });

        root.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN)
                {
                    y1 = event.getY();
                }
                else if (action == MotionEvent.ACTION_UP)
                {
                    y2 = event.getY();
                    float swipeDist = Math.abs(y2 - y1);
                    if (swipeDist > MIN_SWIPE_DIST)
                    {
                        if (y1 < y2)
                        {
                            Log.i("before index", index);
                            //swipe from top to bottom
                            int numberIndex = (Integer.parseInt(index) - 1) % Integer.parseInt(total);
                            if (numberIndex < 0)
                            {
                                numberIndex = Integer.parseInt(total) - 1;
                            }
                            index = Integer.toString(numberIndex);
                            Log.i("new index", index);
                        }
                        else
                        {
                            //swipe from bottom to top
                            Log.i("before index", index);
                            index = Integer.toString((Integer.parseInt(index) + 1) % Integer.parseInt(total));
                            Log.i("new index", index);
                        }

                        Intent intent = new Intent(RealMain.this, RealMain.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("index", index);

                        Intent intentSwipe = new Intent(getBaseContext(), WearToMobileService.class);
                        intentSwipe.putExtra("button", false);
                        intentSwipe.putExtra("index swipe", index);
                        Log.i("index", "index at real main is " + index);
                        if (index != null)
                        {
                            startService(intentSwipe);
                        }

                        startActivity(intent);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {

            float currX = event.values[0];
            float currY = event.values[1];
            float currZ = event.values[2];

            if (!((Float.isNaN(x)) || (Float.isNaN(y)) || (Float.isNaN(z))))
            {
                if ((Math.abs(currX - x) >= MIN_SHAKE_SPEED)
                        || (Math.abs(currY - y) >= MIN_SHAKE_SPEED)
                        || (Math.abs(currZ - z) >= MIN_SHAKE_SPEED))
                {
                    Intent intent = new Intent(getBaseContext(), VoteViewActivity.class);
                    if (!VoteViewActivity.started)
                    {
                        startActivity(intent);
                    }
                    else
                    {
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        VoteViewActivity.randVote();
                    }
                }
            }

            x = currX;
            y = currY;
            z = currZ;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
