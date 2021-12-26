package com.example.musicure;

import static com.example.musicure.NotificationReceiver.ACTION_NEXT;
import static com.example.musicure.NotificationReceiver.ACTION_PLAY;
import static com.example.musicure.NotificationReceiver.ACTION_PREV;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MusicService extends Service
{
    private IBinder mBinder=new MyBinder();
    public static final String ACTION_NEXT="NEXT";
    public static final String ACTION_PREV="PREV";
    public static final String ACTION_PLAY="PLAY";
    ActionPlaying actionPlaying;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public class MyBinder extends Binder{
        MusicService getService()
        {
            return MusicService.this;
        }


    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        String actionName=intent.getStringExtra("myActionName");
        if(actionName!=null)
        {
            switch (actionName) {

                case ACTION_PLAY:
                    if(actionPlaying!=null)
                    {
                        actionPlaying.playclicked();
                    }
                    else{
                        Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case ACTION_NEXT:
                    if(actionPlaying!=null)
                    {
                        actionPlaying.nextClicked();
                    }

                    break;

                case ACTION_PREV:
                    if(actionPlaying!=null)
                    {
                        actionPlaying.prevClicked();
                    }
                    break;

            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
        }

        return START_STICKY;
    }

}
