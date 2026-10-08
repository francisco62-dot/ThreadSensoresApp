package com.example.threadsensoresapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button btnCargar;
    private ImageView mImageView;
    private TextView tvSensor;

    private SensorManager sensorManager;
    private Sensor rotationSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Inicializar vistas UI
        btnCargar = findViewById(R.id.btnCargar);
        mImageView = findViewById(R.id.mImageView);
        tvSensor = findViewById(R.id.tvSensor);

        // 2. Configurar evento click del botón para iniciar el Thread en segundo plano
        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ejecutarThreadDescarga();
            }
        });

        // 3. Inicializar el SensorManager y obtener el sensor de rotación (o acelerómetro de respaldo)
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (rotationSensor == null) {
                rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }
    }

    /**
     * Simula la descarga de una imagen mediante un Thread en segundo plano.
     */
    private void ejecutarThreadDescarga() {
        btnCargar.setEnabled(false);
        Toast.makeText(this, "Iniciando descarga en segundo plano...", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Simula un proceso pesado / descarga de 2 segundos
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Actualiza la interfaz gráfica en el Hilo Principal (UI Thread)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImageResource(R.drawable.ic_launcher_foreground);
                        btnCargar.setEnabled(true);
                        Toast.makeText(MainActivity.this, "¡Imagen descargada con éxito!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el Listener para escuchar cambios del sensor cuando la app esté activa
        if (sensorManager != null && rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Anular el registro del Listener para ahorrar batería cuando la app se pause
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR || event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String datosFormatted = String.format(Locale.getDefault(),
                    "Datos del Sensor:\nX: %.2f | Y: %.2f | Z: %.2f", x, y, z);

            tvSensor.setText(datosFormatted);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario implementar para esta actividad
    }
}
