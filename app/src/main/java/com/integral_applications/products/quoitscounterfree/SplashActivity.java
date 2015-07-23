package com.integral_applications.products.quoitscounterfree;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Resources res = getResources();

        ImageView image = (ImageView)findViewById(R.id.splashImage);

        image.setRotationY(90);
        image.setVisibility(View.VISIBLE);

        ObjectAnimator coin = ObjectAnimator.ofFloat(image, "rotationY", 90f, 0f);
        coin.setStartDelay(res.getInteger(R.integer.splash_animation_delay));
        coin.setDuration(res.getInteger(R.integer.splash_animation_duration));
        coin.setInterpolator(new AccelerateDecelerateInterpolator());
        coin.start();

        // image.startAnimation(r);

        Thread splashDelay = new Thread (){
            public void run(){
                try {
                    sleep(res.getInteger(R.integer.splash_duration_seconds) * 1000);

                    Intent mainActivity = new Intent("android.intent.action.COUNTER");
                    startActivity(mainActivity);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        splashDelay.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }*/
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
