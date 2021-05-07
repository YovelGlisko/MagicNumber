package com.example.magicnumber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    // here I set up some objects I need for the application. I make my ProgressBar so it can be accessed throughout. I make a string tag to use on my logs. I make an AtomicBoolean to stop my threads
    // I make my TextView and I make a Random to get random numbers.
    private ProgressBar progressCircle;
    private final String tag = "Assignment 6 Yovel Glisko";
    private static volatile AtomicBoolean runBoolean = new AtomicBoolean(false);
    private TextView textBox;
    private Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // in my onCreate after the basics I make my two threads using my background Runnable and name them first and second to keep track
        Thread tOne = new Thread(background, "first");
        tOne.start();
        Thread tTwo = new Thread(background, "second");
        tTwo.start();
        // I set up my text box and my ProgressBar, making the latter visible
        textBox = (TextView)findViewById(R.id.textView);
        progressCircle = (ProgressBar) findViewById(R.id.circle);
        progressCircle.setVisibility(View.VISIBLE);
    }
    // here I use a handler to have the main thread take messages from the background ones
    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            // I make the number a string to make the next line a bit cleaner and then I use an if to see if the number is a magic number
            String number = String.valueOf((int)msg.obj);
            if((int)msg.obj%7==0 || ((int)msg.obj%4==0 && number.endsWith("2"))){
                // here I set my AtomicBoolean to true so that my threads will stop and make my ProgressBar invisible to indicate to the user it is done before setting the textBox to the magic number
                runBoolean.set(true);
                progressCircle.setVisibility(View.INVISIBLE);
                textBox.setText(String.valueOf((int)msg.obj));

            }
        }
    };
    // here is my runnable that I use to have the threads go through their processes
    Runnable background = new Runnable(){
        public void run(){
                try {
                    // I make an int i that goes up by one every loop and check that runBoolean is false before getting a random 4 digit number using my Random
                    int i = 0;
                    while(!runBoolean.get()) {
                        i++;
                        int random = rand.nextInt(9000) + 1000;
                        // I then use the log to log the random number and the thread associated before making a message and sending it to the handler
                        Log.v(tag, String.valueOf(random) + " " + Thread.currentThread().getName());
                        Message message = handler.obtainMessage(i, random);
                        handler.sendMessage(message);
                        // I have my sleep at the end of this loop otherwise the timings get messed up and the threads do not stop in time
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    // here I simply catch an interruption to end the loop if needed
                    Thread.currentThread().interrupt();
                    runBoolean.set(true);
                    e.printStackTrace();
                }
            }
    };
}

