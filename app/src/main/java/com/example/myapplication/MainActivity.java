package com.example.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private TextView statusTextView;
    private MediaPlayer stableSound;
    private MediaPlayer movingSound;

    private boolean isStable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        Button resetButton = findViewById(R.id.resetButton);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        stableSound = MediaPlayer.create(this, R.raw.pepe); // Asegúrate de tener el sonido en /res/raw
        movingSound = MediaPlayer.create(this, R.raw.mario);

        resetButton.setOnClickListener(v -> resetDetection());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float z = event.values[2];
            if (Math.abs(z) < 0.5) {
                setStatus("Estable");
            } else {
                setStatus("En Movimiento");
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Lógica para detección de movimiento
            if (Math.abs(event.values[0]) > 0.5 || Math.abs(event.values[1]) > 0.5 || Math.abs(event.values[2]) > 0.5) {
                setStatus("En Movimiento");
            }
        }
    }

    private void setStatus(String status) {
        if (!status.equals(statusTextView.getText().toString().replace("Estado: ", ""))) {
            statusTextView.setText("Estado: " + status);
            if (status.equals("Estable")) {
                stableSound.start();
            } else {
                movingSound.start();
            }
        }
    }

    private void resetDetection() {
        statusTextView.setText("Estado: Estable");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        if (stableSound != null) {
            stableSound.release();
            stableSound = null;
        }
        if (movingSound != null) {
            movingSound.release();
            movingSound = null;
        }
        super.onDestroy();
    }

}
