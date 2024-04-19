package com.example.facb

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.facb.ui.theme.FacbTheme

import android.Manifest
import android.util.Log


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*


import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.facb.AccelerometerActivity.Companion.database
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.UUID

class GraphActivity : AppCompatActivity() {
    private val orientationDataList = mutableListOf<OrientationData>()
    private val orientationDataListfull = mutableListOf<OrientationData>()
    var permflag by mutableIntStateOf(1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)


        setContent {
            FacbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val rstring = (UUID.randomUUID().mostSignificantBits and 0b1111111111).toString()
                    GraphScreen(orientationDataList,exportDataToCSV = { exportDataToCSV(rstring)},perm = permflag)
                }
            }
        }

        // Fetch data from the database asynchronously
        fetchDataFromDatabase()
        fetchDataFromDatabasefull()
        requestStoragePermission()
    }
    private fun fetchDataFromDatabasefull() {
        // Use coroutine to fetch data from the database asynchronously
        lifecycleScope.launch(Dispatchers.IO) {
            val data = AccelerometerActivity.database.orientationDataDao().getAllOrientationDatafull()
            orientationDataListfull.addAll(data)

        }

    }
    private fun fetchDataFromDatabase() {
        // Use coroutine to fetch data from the database asynchronously
        lifecycleScope.launch(Dispatchers.IO) {
            val data = AccelerometerActivity.database.orientationDataDao().getAllOrientationData()
            orientationDataList.addAll(data)

        }

    }
    private fun requestStoragePermission() {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_STORAGE_PERMISSION_CODE)
        } else {
            // Permission already granted, proceed with file operations
//            exportDataToCSV()
            showToast("Permission previously granted to write to external storage")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file operations
//                exportDataToCSV()
                showToast("Permission granted to write to external storage")
            } else {
                // Permission denied, show a message or handle it accordingly
                permflag=0
                showToast("Permission denied to write to external storage")

//                return
            }
        }
    }


    private fun exportDataToCSV(rstring:String) {
        val csvData = StringBuilder()
        // Append CSV header
        csvData.append("i;pitch;roll;yaw\n")

        // Append data rows
        orientationDataListfull.forEach { data ->
            csvData.append("${data.id};${data.pitch};${data.roll};${data.yaw}\n")
        }

        // Write CSV data to a file
        val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val csvFile = File(downloadsFolder, "orientation_data_v-"+ rstring+".csv")

        try {
            // Create the file

            // Write data to the file using FileWriter
            FileWriter(csvFile,false).use { writer ->
                writer.write(csvData.toString())
            }

            showToast("CSV file saved to: ${csvFile.absolutePath}")
        } catch (e: IOException) {
            showToast("Error saving CSV file: ${e.message}")
            e.printStackTrace() // Log the exception for debugging
        }
    }


    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_STORAGE_PERMISSION_CODE = 123
    }
}




@Composable
fun GraphScreen(orientationDataList: List<OrientationData>,exportDataToCSV: () -> Unit,perm:Int=0) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pitchData = orientationDataList.indices.toList()
            val pitchAngles: List<Float> = orientationDataList.map { it.pitch.toFloat() }


            val rollData = orientationDataList.map { (it.id.toInt()) }
            val rollAngles = orientationDataList.map { (it.roll.toDouble()).toFloat() }

            val yawData = orientationDataList.map { (it.id.toInt()) }
            val yawAngles = orientationDataList.map { (it.yaw.toDouble()).toFloat() }

            Graph(title = "Roll", xData = rollData, yData = rollAngles, visibleDataPoints = 50)
            Graph(title = "Pitch", xData = pitchData, yData = pitchAngles,visibleDataPoints = 50)
            Graph(title = "Yaw", xData = yawData, yData = yawAngles,visibleDataPoints = 50)
            Button(onClick = { if(perm==1){ exportDataToCSV()}
            }) {
                Text("Export Data")
            }
            Text(text ="Export works with Permission!")
        }
    }
}

@Composable
fun Graph(title: String, xData: List<Int>, yData: List<Float>, visibleDataPoints: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)
        val visibleXData = xData.takeLast(visibleDataPoints)
        val visibleYData = yData.takeLast(visibleDataPoints)
        CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
            ),
            remember {
                CartesianChartModelProducer.build().apply {
                    tryRunTransaction {
                        lineSeries {

                            series(visibleYData)
                        }
                    }
                }
            }
        )
    }
}







//@Composable
//fun Graph(title: String, data: List<Pair<Int, Float>>) {
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = title)
//        CartesianChartHost(
//            rememberCartesianChart(
//                rememberLineCartesianLayer(),
//                startAxis = rememberStartAxis(),
//                bottomAxis = rememberBottomAxis(),
//            ),
//            remember { CartesianChartModelProducer.build().apply { tryRunTransaction { lineSeries { data.forEach { series(it.first, it.second) } } } } }
//        )
//    }
//}

//@Composable
//fun Graph(title: String, data: List<Pair<Long, Float>>) {
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = title)
//        CartesianChartHost(
//            rememberCartesianChart(
//                rememberLineCartesianLayer(),
//                startAxis = rememberStartAxis(),
//                bottomAxis = rememberBottomAxis(),
//            ),
//            remember { CartesianChartModelProducer.build().apply { tryRunTransaction { lineSeries { data.forEach { series(it.first, it.second) } } } } }
//        )
//    }
//}

//
//@Composable
//fun GraphScreen(orientationDataList: List<OrientationData>) {
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        testDisplay(orientationDataList)
//    }
//}
//@Composable
//fun testDisplay(orientationDataDao: List<OrientationData>) {
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            Text(text = "Test Text")
//            val modelProducer = remember { CartesianChartModelProducer.build() }
//
//// Assuming orientationDataDao is a List<OrientationData>
//            LaunchedEffect(orientationDataDao) {
//                modelProducer.tryRunTransaction {
//                    lineSeries {
//                        orientationDataDao.forEachIndexed { index, data ->
//                            series( data.timestamp,data.pitch)
//                        }
//                    }
//                }
//            }
//
//            CartesianChartHost(
//                rememberCartesianChart(
//                    rememberLineCartesianLayer(),
//                    startAxis = rememberStartAxis(),
//                    bottomAxis = rememberBottomAxis(),
//                ),
//                modelProducer,
//            )
//
//        }
//    }
//}