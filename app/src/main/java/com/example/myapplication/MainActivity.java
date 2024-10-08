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

    private String currentStatus = "Estable"; // Estado inicial

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        Button resetButton = findViewById(R.id.resetButton);

        // Inicializar el SensorManager y los sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Inicializar los sonidos
        stableSound = MediaPlayer.create(this, R.raw.mario); // Cambia "mario" por el nombre del archivo correcto
        movingSound = MediaPlayer.create(this, R.raw.timbre); // Cambia "despacito" por el nombre del archivo correcto

        resetButton.setOnClickListener(v -> resetDetection());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float z = event.values[2];  // Componente Z del acelerómetro
            float threshold = 8.0f;      // Umbral para considerar que está plano

            boolean isStable = Math.abs(z) >= threshold;

            // Lógica para detección de movimiento
            boolean isMoving = false;
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                isMoving = Math.abs(event.values[0]) > 0.5 || Math.abs(event.values[1]) > 0.5 || Math.abs(event.values[2]) > 0.5;
            }

            // Determinar el estado
            if (isStable && !isMoving) {
                setStatus("Estable");
            } else {
                setStatus("En Movimiento");
            }
        }
    }

    private void setStatus(String status) {
        if (!status.equals(currentStatus)) {
            currentStatus = status;
            statusTextView.setText("Estado: " + status);

            if (status.equals("Estable")) {
              movingSound.stop();
              stableSound.start();
            }else{
                movingSound.start();
            }
        }
    }

    private void resetDetection() {
        setStatus("Estable");
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
