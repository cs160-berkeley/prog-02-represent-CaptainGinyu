package com.example.cs160_ej.lordofrepresentatives;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class MobileListener extends WearableListenerService
{

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        String path = messageEvent.getPath();

        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if (path.equals("DetailedInfoActivity"))
        {
            Intent intent = new Intent(this, DetailedInfoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String dataReceived = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            intent.putExtra("index", dataReceived);
            startActivity(intent);
        }
        else
        {
            super.onMessageReceived(messageEvent);
        }

    }
}
