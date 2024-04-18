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
import androidx.compose.ui.unit.dp
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
            // Button to navigate to AccelerometerActivity
            Button(
                onClick = {onNavigateToAccelerometerActivity() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Next Activity")

            }

        }
    }
}
