package com.example.facb

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.facb.ui.theme.FacbTheme

object GlobalVariables {
    var vflag = mutableIntStateOf(0)
}
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FacbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FirstScreen {
                        navigateToAccelerometerActivity() // Navigate to AccelerometerActivity on button click
                    }
                }
            }
        }
    }

    private fun navigateToAccelerometerActivity( ) {
        // Start AccelerometerActivity
        startActivity(Intent(this, AccelerometerActivity::class.java))
    }
}

@Composable
fun FirstScreen(onNavigateToAccelerometerActivity: () -> Unit) {
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
                text = "Accelerometer \nbased \nRelative Orientation",
                fontSize = 45.sp,
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.Black
                ),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(16.dp).padding(16.dp))
            Text(
                text = "Frenetic Accelerometer & Sensor Control Beacon",
                fontSize = 16.sp,
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Normal,
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = {onNavigateToAccelerometerActivity() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Intiate Sensor Data Collection")

            }

        }
    }
}
