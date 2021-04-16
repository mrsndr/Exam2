package com.example.exam2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;

// The activity_splash_screen.xml layout with a complete, pleasing layout [3 morks]
// Retrieve and display the number of trees chopped down from prior launches [5 marks]
// Add an action button to the splash.xml layout that resets the count to 0 [3 marks]
//      when the reset is done show a Toast message to indicate [2 marks]

// don't forget to set the icon to be the axe! [2 marks]

public class SplashScreen extends AppCompatActivity {
    private int splashTime = 5000;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private int chopNum = 0;
    private final static String privateDirectory = "private";
    private final static String fileName = "chopnum.txt";

    private TextView myCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // do all the things for setting up this screen:
        //if (CheckPermissions()) {
        //} else {
        //    RequestPermissions();
        //}

        myCounter = findViewById(R.id.splashScreenCounterText);

        chopNum = readIntFromInternal(getApplicationContext());
        myCounter.setText(Integer.toString(chopNum));

            // trigger the next activity after 5 seconds
        new Handler(Looper.getMainLooper()).postDelayed(
                () -> {
                    Intent myIntent = new Intent(this, ChopTrees.class);
                    startActivity(myIntent);
                }, splashTime);



    }


    // Reset button pressed
    public void resetCounterButton_Pressed (View view) {
        // Set the counter back to zero
        writeToInternal(getApplicationContext(),0);
        myCounter.setText("0");
        // Toast after reset
        Toast.makeText(getApplicationContext(), "The counter was reset!" ,Toast.LENGTH_SHORT).show();
    }


    // need any helper functions? put them here

    public void RequestPermissions() {
        ActivityCompat.requestPermissions(SplashScreen.this,
                new String[]{WRITE_EXTERNAL_STORAGE},
                REQUEST_AUDIO_PERMISSION_CODE);
    }

    public boolean CheckPermissions() {
        int resultExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int resultRecordAudio = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return ( resultExternalStorage == PackageManager.PERMISSION_GRANTED &&
                resultRecordAudio == PackageManager.PERMISSION_GRANTED );
    }


    public void writeToInternal(Context myContext, int value) {
        File dir = new File(myContext.getFilesDir(),privateDirectory);
        if(!dir.exists()) {
            dir.mkdir();
        }

        try {
            File outputFile = new File(dir, fileName);
            FileWriter writer = new FileWriter(outputFile);

            writer.write(Integer.toString(value));
            writer.flush();
            writer.close();

        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    public int readIntFromInternal (Context myContext) {
        File dir = new File(myContext.getFilesDir(),privateDirectory);
        int retval = 0;
        if(!dir.exists()) {
            retval = 0;
        } else {
            try {
                File inputFile = new File(dir,fileName);
                FileInputStream fis = new FileInputStream(inputFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);

                retval = Integer.parseInt(bufferedReader.readLine());

                bufferedReader.close();
                isr.close();
                fis.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retval;
    }

}