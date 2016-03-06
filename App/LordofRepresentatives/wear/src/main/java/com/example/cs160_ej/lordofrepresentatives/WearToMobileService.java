package com.example.cs160_ej.lordofrepresentatives;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class WearToMobileService extends Service implements GoogleApiClient.ConnectionCallbacks
{

    private GoogleApiClient mWatchApiClient;
    private List<Node> nodes = new ArrayList<>();

    @Override
    public void onCreate()
    {
        super.onCreate();
        mWatchApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        mWatchApiClient.connect();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mWatchApiClient.disconnect();
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Wearable.NodeApi.getConnectedNodes(mWatchApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>()
                {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult)
                    {

                        nodes = getConnectedNodesResult.getNodes();
                    }
                });
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId)
    {
        if (intent != null)
        {
            final Service _this = this;

            Bundle extras = intent.getExtras();

            if (extras != null)
            {
                final String index = extras.getString("index", "-1");
                Log.i("index", index);

                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        mWatchApiClient.connect();
                        sendMessage("DetailedInfoActivity", index);
                        try
                        {
                            Thread.sleep(500);
                        }
                        catch (Exception e)
                        {

                        }
                        _this.stopSelf();
                    }
                })
                .start();
            }
        }

        return START_STICKY;
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    private void sendMessage(final String path, final String text)
    {
        for (Node node : nodes)
        {
            Wearable.MessageApi.sendMessage(
                    mWatchApiClient, node.getId(), path, text.getBytes());
        }
    }

}
