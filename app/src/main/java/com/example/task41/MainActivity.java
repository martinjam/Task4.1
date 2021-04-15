package com.example.task41;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Boolean resume = false;
    TextView previousTextView;
    SharedPreferences sharedPreferences;
    String workoutType;
    String WORKOUT_NAME = "WORKOUT_NAME";
    String duration;
    String DURATION = "DURATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("com.example.task41", MODE_PRIVATE);

        previousTextView = findViewById(R.id.previousTextView);
        workoutType = "";
        duration = "";
        if (savedInstanceState != null) {
            workoutType = savedInstanceState.getString(WORKOUT_NAME);
            duration = savedInstanceState.getString(DURATION);

            previousTextView.setText("You spent " + duration + " on " + workoutType + " last time.");
        } else {
            checkSharedPreferences();
        }

        //Chronometer format implementation sourced from https://stackoverflow.com/questions/4152569/how-to-change-format-of-chronometer
        Chronometer timerChronometer = findViewById(R.id.timerChronometer);
        timerChronometer.setOnChronometerTickListener(cArg -> {
            long time = SystemClock.elapsedRealtime() - cArg.getBase();
            int h = (int) (time / 3600000);
            int m = (int) (time - h * 3600000) / 60000;
            int s = (int) (time - h * 3600000 - m * 60000) / 1000;
            String hh = h < 10 ? "0" + h : h + "";
            String mm = m < 10 ? "0" + m : m + "";
            String ss = s < 10 ? "0" + s : s + "";
            cArg.setText(hh + ":" + mm + ":" + ss);
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(WORKOUT_NAME, workoutType);
        outState.putString(DURATION, duration);
        super.onSaveInstanceState(outState);
    }


    public void timerClick(View view) {
        ImageButton playButton = findViewById(R.id.playButton);
        ImageButton pauseButton = findViewById(R.id.pauseButton);
        TextView previousTextView = findViewById(R.id.previousTextView);
        EditText typeEditText = findViewById(R.id.typeEditText);

        Chronometer timerChronometer = findViewById(R.id.timerChronometer);

        switch (view.getId()) {
            case R.id.playButton:
                playButton.setClickable(false);
                pauseButton.setClickable(true);

                if (!resume) {
                    timerChronometer.setBase(SystemClock.elapsedRealtime());
                }
                timerChronometer.start();
                break;

            case R.id.pauseButton:
                playButton.setClickable(true);
                pauseButton.setClickable(false);
                timerChronometer.stop();
                resume = true;
                break;

            case R.id.recordButton:
                timerChronometer.stop();
                resume = false;
                duration = "" + timerChronometer.getText().toString();
                workoutType = "" + typeEditText.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(DURATION, duration);
                editor.putString(WORKOUT_NAME, workoutType);
                editor.apply();

                previousTextView.setText("You spent " + duration + " on " + workoutType + " last time.");
                timerChronometer.setText("00:00:00");
                pauseButton.setClickable(false);
                playButton.setClickable(true);
                break;
        }
    }

    public void checkSharedPreferences() {
        duration = sharedPreferences.getString(DURATION, "");
        workoutType = sharedPreferences.getString(WORKOUT_NAME, "");
        previousTextView.setText("You spent " + duration + " on " + workoutType + " last time.");
    }
}