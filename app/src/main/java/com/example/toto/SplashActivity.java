package com.example.toto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.toto.queue.channelRcv.QueueService;
import com.example.toto.queue.channelTransmission.ChannelSingleton;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SplashActivity extends Activity {
    //Use splash screen to perform checks on device, like if the device is connected to
    //the network or the user as already logged in
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //At the moment we aren't performing any checks just sleeping
        new Thread() {
            @Override
            public void run() {
                try {
                    super.run();

                    //initializing queue
                    try {
                        ChannelSingleton.getInstance();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                    sleep(3500);  //Delay of 3.5 seconds

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "There were some issues loading the application, please try again later",
                            Toast.LENGTH_SHORT).show();
                } finally {
                    Intent intent = new Intent(getApplicationContext(),SignInSignUp.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();
    }
}
