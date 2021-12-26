package com.example.musicure;

import static com.example.musicure.ApplicationClass.ACTION_NEXT;
import static com.example.musicure.ApplicationClass.ACTION_PLAY;
import static com.example.musicure.ApplicationClass.ACTION_PREV;
import static com.example.musicure.ApplicationClass.CHANNEL_ID_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaMetadata;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.Token;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class Music_player extends AppCompatActivity implements ActionPlaying{

    Button btnPlay,btnNext,btnPrev,btnForward,btnRewind;
    TextView txtSongName,txtSongStart,txtSongEnd;
    SeekBar seekMusicBar;
    FirebaseFirestore fStore;
    Thread updateseekbar;
    int err=0;

 //   BarVisualizer barVisualizer;

    ImageView imageView;

    String category,link,artist,Song;
    public static final String EXTRA_NAME="song_name";
    public static String songName=null;
    private MediaPlayer mediaPlayer;
    MediaSessionCompat mediaSession;
    MediaSessionCompat mediaSession1;
    Random r = new Random();
    int position = new Random().nextInt((8 - 1) + 1) + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        getWindow().setStatusBarColor(ContextCompat.getColor(Music_player.this,R.color.red));
        getWindow().setNavigationBarColor(ContextCompat.getColor(Music_player.this,R.color.darkred));

        Intent intent = getIntent();
        category = (intent.getStringExtra("raga"));

        ImageButton back=findViewById(R.id.BACK);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnPlay = findViewById(R.id.btnPlay);
        btnForward = findViewById(R.id.btnForward);
        btnRewind = findViewById(R.id.btnRewind);

        mediaSession=new MediaSessionCompat(this,"PlayerAudio");
        mediaSession1=new MediaSessionCompat(this,"tag");


        txtSongName = findViewById(R.id.txtSong);
        txtSongStart = findViewById(R.id.txtSongStart);
        txtSongEnd = findViewById(R.id.txtSongEnd);

        seekMusicBar = findViewById(R.id.seekBar);
        seekMusicBar.setMax(100);
        mediaPlayer=new MediaPlayer();
        // barVisualizer=findViewById(R.id.wave);

        imageView = findViewById(R.id.imgView);
        ImageView temp=findViewById(R.id.temp);
        txtSongName.setText("Connecting...");


     //   MediaMetadata mediaMetadata = new MediaMetadata.Builder().putLong(MediaMetadata.METADATA_KEY_DURATION, -1L).build();



        //    Glide.with(this).load("http://goo.gl/gEgYUd").into(imageView);

         //   Glide.with(this).load("https://raw.githubusercontent.com/Shubhu2603/musicure/main/abhisheki.jpg").into(imageView);





        //Toast.makeText(getApplicationContext(), category, Toast.LENGTH_SHORT).show();
        fStore = FirebaseFirestore.getInstance();


            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(category).child(String.valueOf(position));
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String u;
                    songName = snapshot.child("Song").getValue().toString();
                    artist = snapshot.child("Artist").getValue().toString();
                    link = snapshot.child("Link").getValue().toString();
                    u=snapshot.child("Image").getValue().toString();

                  //  Picasso.get().load(u).placeholder(R.drawable.music_note).into(imageView);
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try  {
                                try {
                                    URL url = new URL(u);
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoInput(true);
                                    connection.connect();
                                    InputStream input = connection.getInputStream();
                                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                    showNotification(myBitmap,R.drawable.playmediayellowhd,songName,artist);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();
                    Glide.with(getApplicationContext()).load(u).apply(RequestOptions.circleCropTransform()).into(imageView);
                    Glide.with(getApplicationContext()).load(u).into(temp);


                    txtSongName.setSelected(true);
                    txtSongName.setText(songName + " : " + artist);


                    btnPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            Drawable dr = ((ImageView) temp).getDrawable();
                            Bitmap bm =  ((BitmapDrawable)dr.getCurrent()).getBitmap();

                            if (mediaPlayer.isPlaying()) {
                         //       handler.removeCallbacks(updater);
                                mediaPlayer.pause();
                                btnPlay.setBackgroundResource(R.drawable.playmediayellowhd);
                                showNotification(bm,R.drawable.playmediayellowhd,songName,artist);


                            } else {
                                mediaPlayer.start();
                                btnPlay.setBackgroundResource(R.drawable.pausemediayellowhd);
                                showNotification(bm,R.drawable.pausemediayellowhd,songName,artist);

                                updateseekbar=new Thread()
                                {
                                    @Override
                                    public void run() {
                                        int totalduration=mediaPlayer.getDuration();
                                        int currentPosition=0;

                                        while(currentPosition<totalduration)
                                        {
                                            try {
                                                sleep(500);
                                                currentPosition=mediaPlayer.getCurrentPosition();
                                                seekMusicBar.setProgress(currentPosition);
                                            }
                                            catch (InterruptedException | IllegalStateException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                };
                                seekMusicBar.setMax(mediaPlayer.getDuration());
                                updateseekbar.start();
                        //        seekMusicBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                        //        seekMusicBar.getThumb().setColorFilter(getResources().getColor(R.color.white),PorterDuff.Mode.MULTIPLY);

                                seekMusicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                        mediaPlayer.seekTo(seekBar.getProgress());

                                    }
                                });


                                final Handler handler=new Handler();
                                final int delay=1000;

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        String current_time=millisecondstotimer(mediaPlayer.getCurrentPosition());
                                        txtSongStart.setText(current_time);
                                        handler.postDelayed(this,delay);
                                    }
                                },delay);

                                TranslateAnimation moveAnim=new TranslateAnimation(-25,25,-25,25);
                                moveAnim.setInterpolator(new AccelerateInterpolator());
                                moveAnim.setDuration(600);
                                moveAnim.setFillEnabled(true);
                                moveAnim.setFillAfter(true);
                                moveAnim.setRepeatMode(Animation.REVERSE);
                                moveAnim.setRepeatCount(1);
                                imageView.startAnimation(moveAnim);
                            }
                        }
                    });

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.pause();
                            btnPlay.setBackgroundResource(R.drawable.playmediayellowhd);
                            btnNext.performClick();
                        }
                    });

                    prepareMediaPlayer(link);

                    btnNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            btnNext.setEnabled(false);
                            btnPrev.setEnabled(false);
                            txtSongName.setText("Connecting...");
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer=new MediaPlayer();
                            position+=1;
                            if(position==9)
                            {
                                position=1;
                            }
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(category).child(String.valueOf(position));
                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    String r;
                                    songName = snapshot.child("Song").getValue().toString();
                                    artist = snapshot.child("Artist").getValue().toString();
                                    link = snapshot.child("Link").getValue().toString();
                                    r=snapshot.child("Image").getValue().toString();


                                    Thread thread = new Thread(new Runnable() {

                                        @Override
                                        public void run() {
                                            try  {
                                                try {
                                                    URL url = new URL(r);
                                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                                    connection.setDoInput(true);
                                                    connection.connect();
                                                    InputStream input = connection.getInputStream();
                                                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                                    showNotification(myBitmap,R.drawable.playmediayellowhd,songName,artist);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    thread.start();





                                    // imageView.setImageResource(R.drawable.music_note);
                                    //Picasso.get().load(r).placeholder(R.drawable.music_note).into(imageView);
                                 //   Drawable dr = ((ImageView) imageView).getDrawable();
                                  //  Bitmap bm =  ((BitmapDrawable)dr.getCurrent()).getBitmap();

                                    Glide.with(getApplicationContext()).load(r).apply(RequestOptions.circleCropTransform()).into(imageView);
                                    Glide.with(getApplicationContext()).load(r).into(temp);


                                    txtSongName.setSelected(true);
                                    txtSongName.setText(songName + " : " + artist);
                                    btnPlay.setBackgroundResource(R.drawable.playmediayellowhd);


                                    prepareMediaPlayer(link);
                                    btnNext.setEnabled(true);
                                    btnPrev.setEnabled(true);






                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            mediaPlayer.pause();
                                            btnPlay.setBackgroundResource(R.drawable.playmediayellowhd);
                                            btnNext.performClick();
                                        }
                                    });





                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        }


                    });

                    btnPrev.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            btnPlay.setEnabled(false);
                            btnNext.setEnabled(false);
                            btnPrev.setEnabled(false);
                            txtSongName.setText("Connecting...");
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer=new MediaPlayer();
                            position-=1;
                            if(position<=0)
                            {
                                position=8;
                            }
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(category).child(String.valueOf(position));
                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {



                                    String l;
                                    songName = snapshot.child("Song").getValue().toString();
                                    artist = snapshot.child("Artist").getValue().toString();
                                    link = snapshot.child("Link").getValue().toString();
                                    l=snapshot.child("Image").getValue().toString();




                                    Thread thread = new Thread(new Runnable() {

                                        @Override
                                        public void run() {
                                            try  {
                                                try {
                                                    URL url = new URL(l);
                                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                                    connection.setDoInput(true);
                                                    connection.connect();
                                                    InputStream input = connection.getInputStream();
                                                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                                    showNotification(myBitmap,R.drawable.playmediayellowhd,songName,artist);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    thread.start();

                                    Glide.with(getApplicationContext()).load(l).apply(RequestOptions.circleCropTransform()).into(imageView);
                                    Glide.with(getApplicationContext()).load(l).into(temp);
                                    //Picasso.get().load(l).placeholder(R.drawable.music_note).into(imageView);



                                    txtSongName.setSelected(true);
                                    txtSongName.setText(songName + " : " + artist);




                                    prepareMediaPlayer(link);
                                    btnPlay.setEnabled(true);
                                    btnNext.setEnabled(true);
                                    btnPrev.setEnabled(true);

                                    btnPlay.setBackgroundResource(R.drawable.playmediayellowhd);

                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            mediaPlayer.pause();
                                            btnPlay.setBackgroundResource(R.drawable.playmediayellowhd);
                                            btnNext.performClick();
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });

                    btnForward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mediaPlayer.isPlaying())
                            {
                                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                            }
                        }
                    });

                    btnRewind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mediaPlayer.isPlaying())
                            {
                                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayer.stop();
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    startActivity(new Intent(getApplicationContext(),AboutPage.class));
                }
            });




    }

    private void prepareMediaPlayer(String L){
        try{
            mediaPlayer.setDataSource(L);
            mediaPlayer.prepare();
            txtSongEnd.setText(millisecondstotimer(mediaPlayer.getDuration()));
        }
        catch (Exception exception)
        {
            Toast.makeText(this,"Slow down!", Toast.LENGTH_SHORT).show();
            err++;
            if(err%5==0)
            {
                Toast.makeText(getApplicationContext(), "You are connected to the inernet and downloading takes time !", Toast.LENGTH_LONG).show();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=new MediaPlayer();
            prepareMediaPlayer(L);
        }
    }



    private String millisecondstotimer(long milliseconds){
        String timerString="";
        String secondsString;

        int hours=(int)(milliseconds/(1000*60*60));
        int minutes=(int)(milliseconds%(1000*60*60))/(1000*60);
        int seconds=(int)((milliseconds%(1000*60*60))%(1000*60)/1000);

        if(hours>0){
            timerString=hours+":";

        }
        if(seconds<10){
            secondsString="0"+seconds;
        }else{
            secondsString=""+seconds;
        }

        timerString=timerString+minutes+":"+secondsString;
        return timerString;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed()
    {

        mediaPlayer.stop();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        startActivity(new Intent(this,AboutPage.class));

    }

    
    public void showNotification(Bitmap b,int playpausebtn,String name,String singer)
    {
       // Intent intent=new Intent(this,Music_player.class);
       // PendingIntent contentIntent=PendingIntent.getActivity(this,0,intent,0);
        Intent prevIntent=new Intent(this,NotificationReceiver.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent=PendingIntent.getBroadcast(this,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent=new Intent(this,NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent=PendingIntent.getBroadcast(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent=new Intent(this,NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent=PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

     //   MediaSessionCompat mediaSession2 = new MediaSessionCompat(this,"tag");

     //   mediaSession.setMetadata(MediaMetadataCompat.fromMediaMetadata(mediaMetadata));



        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID_2)
                .setSmallIcon(R.drawable.meditationhd)
                .setLargeIcon(b)
                .setColor(getResources().getColor(R.color.red))
                .setColorized(true)
                .setContentTitle(name)
                .setContentText(singer)
                .setSubText("Beta version")
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession((mediaSession.getSessionToken())))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);

    }

    @Override
    public void nextClicked() {
        btnPlay.performClick();
    }

    @Override
    public void prevClicked() {

        btnPrev.performClick();
    }

    @Override
    public void playclicked() {
        btnNext.performClick();

    }
}