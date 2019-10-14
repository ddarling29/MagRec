package edu.uark.magrec;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mMagnetometer;
    private TextView xView;
    private TextView yView;
    private TextView zView;
    private Button recordButton;
    private PrintWriter writer;
    private boolean isRecording;
    private String absolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        xView = findViewById(R.id.x_value_text_view);
        yView = findViewById(R.id.y_value_text_view);
        zView = findViewById(R.id.z_value_tect_view);
        recordButton = findViewById(R.id.record_button);
        isRecording = false;
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        if (isRecording && writer != null) {
            //Timestamp currTime = new Timestamp(System.currentTimeMillis());
            writer.println(String.format("%d,%.2f,%.2f,%.2f", System.currentTimeMillis(), x, y, z));
        }

        xView.setText(String.format("%.2f", x));
        yView.setText(String.format("%.2f", y));
        zView.setText(String.format("%.2f", z));
    }

    public void recordClick(View currView) {
        isRecording = !isRecording;

        if (!isRecording) {
            if (writer != null) {
                writer.close();
                writer = null;
                Toast.makeText(this, "Recording saved to: " + absolutePath, Toast.LENGTH_LONG).show();
            }
        } else {
            File path = getExternalFilesDir(null);
            Date currTime = Calendar.getInstance().getTime();
            File file = new File(path, currTime.toString() + ".txt");
            absolutePath = file.getAbsolutePath();

            FileOutputStream fileOutStream;

            try {
                fileOutStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to record.", Toast.LENGTH_SHORT).show();
                return;
            }

            writer = new PrintWriter(fileOutStream);
            Toast.makeText(this, "Started recording.", Toast.LENGTH_SHORT).show();
        }
    }
}
