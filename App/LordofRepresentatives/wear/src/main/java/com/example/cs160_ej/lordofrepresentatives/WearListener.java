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
            /*int commaIndex = dataReceived.indexOf(',');
            String name = dataReceived.substring(0, commaIndex);
            int pipeIndex = dataReceived.indexOf('|');
            String party = dataReceived.substring(commaIndex + 1, pipeIndex);
            int starIndex = dataReceived.indexOf('*');
            String index = dataReceived.substring(pipeIndex + 1, starIndex);
            String total = dataReceived.substring(starIndex + 1, dataReceived.length());

            Log.i("wear listener", "message received");

            intent.putExtra("name", name);
            intent.putExtra("party", party);
            intent.putExtra("index", index);
            intent.putExtra("total", total);*/


            String currName;
            String currParty;

            Log.i("received data", dataReceived);

            int percentIndex = dataReceived.indexOf('%');
            String total = dataReceived.substring(0, percentIndex);
            dataReceived = dataReceived.substring(percentIndex + 1, dataReceived.length());

            int poundIndex = dataReceived.indexOf('#');
            String index = dataReceived.substring(0, poundIndex);
            dataReceived = dataReceived.substring(poundIndex + 1, dataReceived.length());
            int numIndex = Integer.parseInt(index);

            for (int i = 0; i <= numIndex; i++)
            {
                int semColIndex = dataReceived.indexOf(';');
                dataReceived = dataReceived.substring(semColIndex + 1, dataReceived.length());
            }
            int semColIndex = dataReceived.indexOf(';');
            currName = dataReceived.substring(0, semColIndex);
            dataReceived = dataReceived.substring(dataReceived.indexOf('|') + 1, dataReceived.length());

            for (int i = 0; i <= numIndex; i++)
            {
                int starIndex = dataReceived.indexOf('*');
                dataReceived = dataReceived.substring(starIndex + 1, dataReceived.length());
            }
            int starIndex = dataReceived.indexOf('*');
            currParty = dataReceived.substring(0, starIndex);

            intent.putExtra("name", currName);
            intent.putExtra("party", currParty);
            intent.putExtra("index", index);
            intent.putExtra("total", total);
            startActivity(intent);
        }
        else
        {
            super.onMessageReceived(messageEvent);
        }
    }
}
