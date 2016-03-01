package com.example.cs160_ej.lordofrepresentatives;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MobileToWearService extends Service
{
    protected GoogleApiClient apiClient;

    public MobileToWearService()
    {
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
                {
                    @Override
                    public void onConnected(Bundle bundle)
                    {

                    }

                    @Override
                    public void onConnectionSuspended(int i)
                    {

                    }
                })
                .build();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        apiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Bundle extras = intent.getExtras();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

            }
        })
        .start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    protected void sendMessage(final String path, final String text)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                NodeApi.GetConnectedNodesResult nodesResult
                        = Wearable.NodeApi.getConnectedNodes(apiClient).await();
                for (Node node : nodesResult.getNodes())
                {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(apiClient,
                            node.getId(), path, text.getBytes()).await();
                }
            }
        })
        .start();
    }
}
