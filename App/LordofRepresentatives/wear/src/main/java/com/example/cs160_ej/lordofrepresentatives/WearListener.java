package com.example.cs160_ej.lordofrepresentatives;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class WearListener extends WearableListenerService
{
    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        String path = messageEvent.getPath();

        if (path.equals("RealMain"))
        {
            Intent intent = new Intent(this, RealMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            String dataReceived = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            int commaIndex = dataReceived.indexOf(',');
            String name = dataReceived.substring(0, commaIndex);
            int pipeIndex = dataReceived.indexOf('|');
            String party = dataReceived.substring(commaIndex + 1, pipeIndex);
            String index = dataReceived.substring(pipeIndex + 1, dataReceived.length());

            intent.putExtra("name", name);
            intent.putExtra("party", party);
            intent.putExtra("index", index);

            startActivity(intent);
        }
        else
        {
            super.onMessageReceived(messageEvent);
        }
    }
}
