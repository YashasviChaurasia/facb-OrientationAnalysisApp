package com.example.facb

import android.os.Bundle
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



import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GraphActivity : AppCompatActivity() {
    private val orientationDataList = mutableListOf<OrientationData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)


        setContent {
            FacbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GraphScreen(orientationDataList)
                }
            }
        }

        // Fetch data from the database asynchronously
        fetchDataFromDatabase()
    }

    private fun fetchDataFromDatabase() {
        // Use coroutine to fetch data from the database asynchronously
        lifecycleScope.launch(Dispatchers.IO) {
            val data = AccelerometerActivity.database.orientationDataDao().getAllOrientationData()
            orientationDataList.addAll(data)
        }
    }
}
@Composable
fun GraphScreen(orientationDataList: List<OrientationData>) {
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