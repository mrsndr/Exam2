package com.example.exam2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static androidx.dynamicanimation.animation.SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;
import static androidx.dynamicanimation.animation.SpringForce.STIFFNESS_LOW;

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

    private SpringAnimation thingSpring;
    DisplayMetrics displayMetrics = new DisplayMetrics();

    private int chopNum = 0; // Number of total trees that have been chopped since last reset

    private int treeOneChops = 4;   // How many chops to cut down this tree
    private int treeTwoChops = 3;
    private int treeThreeChops = 5;
    private ImageView treeOne, treeTwo, treeThree;
    private ImageView Axe;
    Animation oneFallOver, twoFallOver, threeFallOver, axeFadeOut;

    private int treeTracker1, treeTracker2, treeTracker3 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chop_trees);

        // set up the activitiy here!
        treeOne = findViewById(R.id.imageViewTreeOne);
        treeTwo = findViewById(R.id.imageViewTreeTwo);
        treeThree = findViewById(R.id.imageViewTreeThree);
        Axe = findViewById(R.id.Axe);

        oneFallOver = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.treefall);
        twoFallOver = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.treefall);
        threeFallOver = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.treefall_left);
        axeFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);

        oneFallOver.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                //treeOne.setImageResource(getResources(R.drawable.));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        twoFallOver.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                treeTwo.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        threeFallOver.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                treeThree.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        axeFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                Axe.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


    }

    public void treeOneClicked (View view) {

        switch (treeOneChops) {
            case 1:
                // fall down
                chopSound();
                treeFallSound();
                treeOne.startAnimation(oneFallOver);
                treeHasFallen();
                treeOneChops --;
                break;
            case 0:
                // Do nothing, it has not been reset yet
                break;
            default:
                // Chopping animation and sound
                chopSound();
                getHighBounceX(Axe, -10000F, 1F, DAMPING_RATIO_MEDIUM_BOUNCY, STIFFNESS_LOW).start();
                treeOneChops --;
        }

    }


    public void treeTwoClicked (View view) {

        switch (treeTwoChops) {
            case 1:
                // fall down
                chopSound();
                treeFallSound();
                treeTwo.startAnimation(twoFallOver);
                treeHasFallen();
                treeTwoChops --;
                break;
            case 0:
                // Do nothing, it has not been reset
                break;
            default:
                // Chopping animation and sound
                chopSound();
                getHighBounceX(Axe, -10000F, 1F, DAMPING_RATIO_MEDIUM_BOUNCY, STIFFNESS_LOW).start();
                treeTwoChops --;
        }

    }

    public void treeThreeClicked (View view) {

        switch (treeThreeChops) {
            case 1:
                // fall down
                chopSound();
                treeFallSound();
                treeThree.startAnimation(threeFallOver);
                treeHasFallen();
                treeThreeChops --;
                break;
            case 0:
                // Do nothing, it has not been reset
                break;
            default:
                // Chopping animation and sound
                chopSound();
                getHighBounceX(Axe, -10000F, -6F, DAMPING_RATIO_MEDIUM_BOUNCY, STIFFNESS_LOW).start();
                treeThreeChops --;
        }

    }


    // Tree fell
    public void treeHasFallen () {
        // Add to the counter

        chopNum = readIntFromInternal(getApplicationContext());
        //myCounter.setText(Integer.toString(chopNum));
        writeToInternal(getApplicationContext(),chopNum + 1);

        //Toast.makeText(getApplicationContext(), "The counter was reset!" ,Toast.LENGTH_SHORT).show();

        if (treeTracker1 + treeTracker2 + treeTracker3 == 0) {
            // start timer to close app

            // Take the axe away, they clearly should not be allowed to have it! Three trees in a row! Who do they think they are?
            Axe.startAnimation(axeFadeOut);
        }

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






    private SpringForce getSpringForce(float dampingRotation, float stiffness, float finalPosition) {
        SpringForce force = new SpringForce();
        force.setDampingRatio(dampingRotation).setStiffness(stiffness);
        force.setFinalPosition(finalPosition);
        return force;
    }

    private float getVelocity(float velocityDp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, velocityDp, getResources().getDisplayMetrics());
    }

    private SpringAnimation getHighBounceX(View view, float velocityDP, float FinalPosition, float DAMPING, float STIFFNESS) {
        final SpringAnimation anim = new SpringAnimation(view, DynamicAnimation.TRANSLATION_X);
        anim.setStartVelocity(velocityDP);
        anim.animateToFinalPosition(FinalPosition);
        anim.setSpring(getSpringForce(DAMPING, STIFFNESS, FinalPosition));
        return anim;
    }

    private SpringAnimation getHighBounceY(View view, float velocityDP, float FinalPosition, float DAMPING, float STIFFNESS) {
        final SpringAnimation anim = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y);
        anim.setStartVelocity(velocityDP);
        anim.animateToFinalPosition(FinalPosition);
        anim.setSpring(getSpringForce(DAMPING, STIFFNESS, FinalPosition));
        return anim;
    }

}