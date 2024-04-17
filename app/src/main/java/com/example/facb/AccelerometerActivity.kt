package com.example.facb

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.facb.ui.theme.FacbTheme

class AccelerometerActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var mAccelerometer: Sensor? = null
    private var xValue: Float = 0f
    private var yValue: Float = 0f
    private var zValue: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FacbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccelerometerScreen(xValue, yValue, zValue)
                }
            }
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        mAccelerometer?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        xValue = event.values[0]
        yValue = event.values[1]
        zValue = event.values[2]
        updateUI()
    }

    private fun updateUI() {
        setContent {
            FacbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccelerometerScreen(xValue, yValue, zValue)
                }
            }
        }
    }
}

@Composable
fun AccelerometerScreen(xValue: Float, yValue: Float, zValue: Float) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "X: ${String.format("%.2f", xValue)}",
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Y: ${String.format("%.2f", yValue)}",
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Z: ${String.format("%.2f", zValue)}",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
