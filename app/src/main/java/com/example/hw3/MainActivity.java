package com.example.hw3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor mSensor;
    private Animation ballAnimation;
    private float lastX, lastY, lastZ;
    private int shake = 0;
    private ImageView ball;
    private TextView Tv;
    private String[] array;
    public static final int FADE_DURATION = 1500;
    public static final int START_OFFSET = 1000;
    public static final int THRESHOLD = 240;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tv = findViewById(R.id.msgTv);
        ball = findViewById(R.id.ball);
        array = getResources().getStringArray(R.array.answers);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
                // Niepowodzenie
        }
        Tv.setVisibility(View.INVISIBLE);
        ballAnimation = AnimationUtils.loadAnimation(this, R.anim.animation);
        ball.setImageResource(R.drawable.hw3ball_front);

    }



    @Override
    protected void onResume() {
        super.onResume();
        if (mSensor != null)
            sensorManager.registerListener(this, mSensor, 100000);

        //msgTv.setVisibility(View.INVISIBLE);
        //ball.setImageResource(R.drawable.hw3ball_front);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mSensor != null)
            sensorManager.unregisterListener(this, mSensor);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int a=handleAccelerationSensor(event.values[0], event.values[1], event.values[2]);
            if (a!=0) {
                showAnswer(array, a-1);
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public int handleAccelerationSensor(float x, float y, float z) {
        double force = 0d;

        force += Math.pow((x - lastX) / SensorManager.GRAVITY_EARTH, 2.0);
        force += Math.pow((y - lastY) / SensorManager.GRAVITY_EARTH, 2.0);
        force += Math.pow((z - lastZ) / SensorManager.GRAVITY_EARTH, 2.0);

        force = Math.sqrt(force);

        lastX = x;
        lastY = y;
        lastZ = z;

        if (force > ((float) THRESHOLD / 150.0f)) {

            ball.setImageResource(R.drawable.hw3ball_empty);
            ball.startAnimation(ballAnimation);
            lastX = 0;
            lastY = 0;
            lastZ = 0;
            shake = (int) (abs(x + y + z) % 20)+1;
            return shake;

        }
        return 0;
    }

    private void showAnswer(String[] array, int shake1) {

        Tv.setText(array[shake1]);
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setStartOffset(START_OFFSET);
        Tv.setVisibility(View.VISIBLE);
        animation.setDuration(FADE_DURATION);
        Tv.startAnimation(animation);
    }
}
