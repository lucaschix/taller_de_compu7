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
        movingSound = MediaPlayer.create(this, R.raw.despacio); // Cambia "despacito" por el nombre del archivo correcto

        resetButton.setOnClickListener(v -> resetDetection());
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float z = event.values[2];  // Componente Z del acelerómetro
            float threshold = 8.0f;      // Umbral para considerar que está plano (ajustable)

            // Verificar si el dispositivo está en una superficie plana
            if (Math.abs(z) >= threshold) {
                setStatus("Estable");      // Dispositivo en posición plana
            } else {
                setStatus("En Movimiento"); // Dispositivo no está plano o en movimiento
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Lógica para detección de movimiento
            if (Math.abs(event.values[0]) > 0.5 || Math.abs(event.values[1]) > 0.5 || Math.abs(event.values[2]) > 0.5) {
                setStatus("En Movimiento");
            }
        }
    }

    private void setStatus(String status) {
        // Comprobar si el estado ha cambiado
        if (!status.equals(statusTextView.getText().toString().replace("Estado: ", ""))) {
            statusTextView.setText("Estado: " + status);

            // Reproducir el sonido correspondiente basado en el estado
            if (status.equals("Estable")) {
                if (movingSound.isPlaying()) {
                    movingSound.stop();  // Detener sonido de "En Movimiento" si está sonando
                }
                if (stableSound != null && !stableSound.isPlaying()) {
                    stableSound.start();  // Sonido para "Estable"
                }
            } else if (status.equals("En Movimiento")) {
                if (stableSound.isPlaying()) {
                    stableSound.stop();  // Detener sonido de "Estable" si está sonando
                }
                if (movingSound != null && !movingSound.isPlaying()) {
                    movingSound.start();   // Sonido para "En Movimiento"
                }
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
