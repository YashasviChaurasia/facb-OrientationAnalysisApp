package com.example.facb

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room

import com.example.facb.ui.theme.FacbTheme
import kotlinx.coroutines.runBlocking


class AccelerometerActivity: ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var rotationVectorSensor: Sensor? = null
    private var rotationMatrix: FloatArray = FloatArray(9)
    private var orientationAngles: FloatArray = FloatArray(3)
    companion object {
        lateinit var database: AppDatabase
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "my_database")
            .build()
        setContent {
            FacbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccelerometerScreen(
                        roll = orientationAngles[0],
                        pitch = orientationAngles[1],
                        yaw = orientationAngles[2]
                    )
                }
            }
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    override fun onResume() {
        super.onResume()
        rotationVectorSensor?.also { sensor ->

            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_FASTEST
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
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            // Convert angles from radians to degrees
            orientationAngles = orientationAngles.map { it * 180 / Math.PI.toFloat() }.toFloatArray()

            // Ensure that pitch value is not null
            val pitch = orientationAngles.getOrElse(1) { 0f }

            updateUI(orientationAngles[0], pitch, orientationAngles[2])
        }
    }

    private fun updateUI(roll: Float, pitch: Float, yaw: Float) {
        // Re-compose the activity
        setContent {
            FacbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccelerometerScreen(roll, pitch, yaw)
                }
            }
        }
    }

}

@Composable
fun AccelerometerScreen(roll: Float, pitch: Float, yaw: Float) {
    val orientationDataDao = AccelerometerActivity.database.orientationDataDao()

    fun saveOrientationData(roll: Float, pitch: Float, yaw: Float, timestamp: Long) {
        val orientationData = OrientationData(roll = roll, pitch = pitch, yaw = yaw, timestamp = timestamp)
        runBlocking {
            orientationDataDao.insertData(orientationData)
        }
    }


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
                text = "Roll: ${String.format("%.2f", roll)}",
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Pitch: ${String.format("%.2f", pitch)}",
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Yaw: ${String.format("%.2f", yaw)}",
                modifier = Modifier.padding(16.dp)
            )
            val currentTimeMillis = System.currentTimeMillis()
            saveOrientationData(roll, pitch, yaw, currentTimeMillis)

            val context = LocalContext.current

            Button(
                onClick = {
                    val intent = Intent(context, GraphActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "View Graph")
            }

        }
    }
}
