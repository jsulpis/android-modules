package com.example.sensors_overview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the list of all the sensors of the device
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // Print some information about each sensor
        StringBuilder sb = new StringBuilder();
        for (Sensor sensor : deviceSensors) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                sb.append("Type: ").append(sensor.getStringType()).append("<br>");
            }
            sb.append("Name: <strong>").append(sensor.getName()).append("</strong><br>");
            sb.append("Vendor: ").append(sensor.getVendor()).append("<br>");
            sb.append("Power: ").append(sensor.getPower()).append("mA<br>");
            sb.append("<br>");
        }

        // Check if a sensor is available on the device
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            sb.append("Yeaah, your device has a magnetometer ! All features will be available !");
        }
        else {
            sb.append("Ooh, your device does not have a magnetometer... we will have to disable some features.");
        }

        ((TextView) findViewById(R.id.sensorList)).setText(Html.fromHtml(sb.toString()));
    }
}
