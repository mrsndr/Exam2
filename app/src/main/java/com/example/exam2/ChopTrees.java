package com.example.exam2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

// The ChopTrees activity should have the following...
// When the tree is clicked:
//   play audio [5 marks]
//   rotate the image 90-degrees (make it look like it is falling!) [5 marks]
//           remember... trees don't spin aroun the middle when chopped
// increment and save "chopped trees" counter [5 marks]
// shortly after the tree has fallen and the sound finishes, (timer or trigger) [5 marks]
//      clear the tree,
//      set up a new tree.

public class ChopTrees extends AppCompatActivity {
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    MediaPlayer chopPlayer = new MediaPlayer();
    MediaPlayer fallPlayer = new MediaPlayer();

    private final static String privateDirectory = "private";
    private final static String fileName = "chopnum.txt";

    private int chopNum = 0; // Number of total trees that have been chopped since last reset

    private int treeOneChops = 3;   // How many chops to cut down this tree
    private ImageView treeOne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chop_trees);

        // set up the activitiy here!
        treeOne = findViewById(R.id.imageViewTreeOne);

    }

    public void treeOneClicked (View view) {


        switch (treeOneChops) {
            case 1:
                // fall down
                chopSound();
                treeFallSound();
                treeHasFallen();
                treeOneChops --;
                break;
            case 0:
                // Do nothing, it has not been reset yet
                break;
            default:
                // Chopping animation and sound
                chopSound();

                treeOneChops --;

        }



    }





    // Tree fell
    public void treeHasFallen () {
        // Add to the counter

        chopNum = readIntFromInternal(getApplicationContext());
        //myCounter.setText(Integer.toString(chopNum));
        writeToInternal(getApplicationContext(),chopNum + 1);

        //Toast.makeText(getApplicationContext(), "The counter was reset!" ,Toast.LENGTH_SHORT).show();
    }

    public void chopSound() {
        //for preloaded sounds
        chopPlayer = MediaPlayer.create(this, R.raw.can_to_table_2);
        chopPlayer.setLooping(false);
        chopPlayer.seekTo(0);
        chopPlayer.setVolume(0.5f, 0.5f);

        chopPlayer.start();
    }

    public void treeFallSound() {
        //for preloaded sounds
        fallPlayer = MediaPlayer.create(this, R.raw.treefalling);
        fallPlayer.setLooping(false);
        fallPlayer.seekTo(0);
        fallPlayer.setVolume(0.5f, 0.5f);

        fallPlayer.start();

        fallPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mPlayer) {
                mPlayer.release();
                // Reset the tree

            }
        });

    }


    // need any helper functions? put them here


    public void RequestPermissions() {
        ActivityCompat.requestPermissions(ChopTrees.this,
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