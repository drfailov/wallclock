package com.fsoft.wallclock;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dr. Failov on 28.08.2017.
 */

public class MainActivity extends Activity {
    private Handler handler = null;
    private TextView dateView = null;
    private TextView timeView = null;
    private TextView secondsView = null;
    private TextView dayView = null;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat secFormat = new SimpleDateFormat(":ss");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private boolean running = false;
    private boolean apiTooLowForImmersive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT){

            apiTooLowForImmersive = true;

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.main);
        dateView = (TextView)findViewById(R.id.label_date);
        dayView = (TextView)findViewById(R.id.label_day);
        timeView = (TextView)findViewById(R.id.label_time);
        secondsView = (TextView)findViewById(R.id.label_seconds);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !apiTooLowForImmersive ) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );}
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running){
                    try {
                        Thread.sleep(500);
                        tick();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    private void tick() throws Exception{
        if(!running)
            return;
        Date date = new Date();
        String time = timeFormat.format(date);
        String sec = secFormat.format(date);
        String day = dayFormat.format(date).toUpperCase();
        String dat = dateFormat.format(date);
        update(time, sec, day, dat);
    }

    private void update(final String time, final String sec, final String day, final String date){
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (timeView != null && time != null)
                        timeView.setText(time);
                    if (secondsView != null && sec != null)
                        secondsView.setText(sec);
                    if (dayView != null && day != null)
                        dayView.setText(day);
                    if (dateView != null && date != null)
                        dateView.setText(date);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

}
