package com.example.cs160_ej.lordofrepresentatives;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends Activity implements SensorEventListener
{
    private float x = Float.NaN;
    private float y = Float.NaN;
    private float z = Float.NaN;

    private final int MIN_SHAKE_SPEED = 30;

    private SensorManager manager;
    private Sensor accel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener()
        {
            @Override
            public void onLayoutInflated(WatchViewStub stub)
            {

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
