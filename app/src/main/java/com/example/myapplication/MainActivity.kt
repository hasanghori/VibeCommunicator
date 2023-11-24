package com.example.myapplication


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.apache.commons.math3.ml.clustering.*
import org.apache.commons.math3.ml.distance.EuclideanDistance
import java.io.File
import java.io.FileWriter
import kotlin.math.abs
import kotlin.math.max


// Create a data class for your one-dimensional points that implements Clusterable
data class OneDimensionalPoint(val value: Double) : Clusterable {
    override fun getPoint(): DoubleArray = doubleArrayOf(value)

    fun getEuclideanDistanceFromOrigin(): Double {
        return abs(value)  // For a one-dimensional point, the distance from the origin is the absolute value
    }
}
class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var vibrator: Vibrator
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val list = mutableListOf<String>()
    private val buffer_1: MutableList<Double> = mutableListOf()
    private val buffer_2: List<Double> = listOf()

    var file = File(
        Environment.getExternalStorageDirectory(),
        "/Users/hasan/Documents/College/MastersFall/accelerometer_data_android.csv"
    )
    var fileWriter: FileWriter? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Initialize the Vibrator service
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrateButton: Button = findViewById(R.id.button_vibrate)

        /*
        val effect = VibrationEffect.startComposition()
            .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE, 0.5f)
            .addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL, 0.5f)
            .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 1.0f, 100)
            .compose()
        */


        //val mVibratePattern = longArrayOf(0, 100, 100, 300, 300, 500) // Timing pattern

        //val mAmplitudes = intArrayOf(0, 50, 100, 150, 200, 255) // Amplitude pattern
        val START: LongArray = longArrayOf(188, 188, 188, 188)
        val START_amplitudes: IntArray = intArrayOf(255, 0, 128, 0)

        //H
        val H_timings: LongArray = longArrayOf(84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84)
        val H_amplitudes: IntArray = intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0)

       // I
        val I_timings: LongArray = longArrayOf(84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84)
        val I_amplitudes: IntArray = intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0)

        //!
        val excl_timings: LongArray = longArrayOf(84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84)
        val excl_amplitudes: IntArray = intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0, 255, 0)

        //val agg_timings: LongArray = longArrayOf(188, 188, 188, 188, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84)
        //val agg_amplitudes: IntArray = intArrayOf(255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0, 255, 0)

        val agg_timings: LongArray = longArrayOf(188, 188, 188, 188, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84)
        val agg_amplitudes: IntArray = intArrayOf(255, 0, 100, 0, 100, 0, 255, 0, 100, 0, 100, 0, 255, 0, 100, 0, 100, 0, 100, 0, 100, 0, 255, 0, 100, 0, 100, 0, 255, 0, 100, 0, 100, 0, 255, 0, 100, 0, 100, 0, 255, 0, 100, 0, 100, 0, 100, 0, 100, 0, 255, 0)

        val repeatIndex = -1 // Do not repeat.

        vibrateButton.setOnClickListener {
            //HI!
            //vibrator.vibrate(VibrationEffect.createWaveform(agg_timings, agg_amplitudes, repeatIndex))
            val centers = processData()
            println(centers)

        }

    }

    fun processData(): String {
        val windowSize = 10
        val stepSize = 7
        val numWindows = ((buffer_1.size - windowSize) / stepSize) + 1
        var collectWindows = mutableListOf<OneDimensionalPoint>()
        var valuesSet: Boolean = false
        var adding: Boolean = false
        var old_val: Int = -2
        var new_val: Int = -2
        var word = mutableListOf<Int>()

        println("Window_Size: ")
        println(numWindows)
        var clusterResults: List<CentroidCluster<OneDimensionalPoint>> = listOf()
        var sortedClusterResults: List<CentroidCluster<OneDimensionalPoint>> = listOf()
        for (i in 0 until numWindows) {
            val start = i * stepSize
            val end = start + windowSize
            val windowData = buffer_1.subList(start, end)
            val amplitude = calculateAmplitude(windowData)

            if ((amplitude > .3 && !valuesSet) || adding){
                adding = true
                collectWindows.add(OneDimensionalPoint(amplitude))
                if (collectWindows.size >= 45){
                    valuesSet = true
                    // call kmeans here
                    val clusterer = KMeansPlusPlusClusterer<OneDimensionalPoint>(3, 1000, EuclideanDistance())
                    clusterResults = clusterer.cluster(collectWindows)
                    sortedClusterResults = clusterResults.sortedBy  { it.center.point[0] }

                    println(i)
                    println(sortedClusterResults.joinToString(separator = ", ", prefix = "[", postfix = "]") { cluster ->
                        // Define how each CentroidCluster<OneDimensionalPoint> should be converted to String
                        // For example, this could be a summary of the cluster, like its centroid or size
                        "Cluster with centroid ${cluster.center} and size ${cluster.points.size}"
                    })
                    adding = false


                }
                continue
            } else if (!valuesSet){
                continue
            }
            println(amplitude)
            val nearestClusterIndex = sortedClusterResults.indices.minByOrNull { index ->
                calculateDistance(amplitude, sortedClusterResults[index].center)
            } ?: -1

            new_val = nearestClusterIndex - 1
            println(new_val)
            if (old_val != new_val && new_val == -1){
                if (old_val != -2) {
                    word.add(old_val)
                }
                old_val = new_val
            } else if (old_val != new_val) {
                old_val = max(new_val, old_val)
            }



        }
        print(word)
        val ascii: String = chunkAndConvertToIntegers(word)
        return ascii
    }


    fun binaryDigitsToAscii(binaryDigits: List<Int>): Char {
        val binaryString = binaryDigits.joinToString("") { it.toString() }
        return binaryString.toInt(2).toChar()
    }

    fun chunkAndConvertToIntegers(longList: List<Int>): String {
        return longList
            .chunked(8)  // Split the list into chunks of 8
            .filter { it.size == 8 }  // Keep only chunks that have exactly 8 elements
            .joinToString("") { chunk ->
                binaryDigitsToAscii(chunk).toString()
            }
    }

    // Function to calculate Euclidean distance between a point and a centroid
    fun calculateDistance(point: Double, centroid: Clusterable): Double {
        // Assuming centroid is an instance of OneDimensionalPoint
        // Cast the centroid to OneDimensionalPoint and calculate the distance
        return abs(point - centroid.point[0])
    }
    fun calculateAmplitude(dataPoints: List<Double>): Double {
        val max = dataPoints.maxOrNull() ?: 0.0
        val min = dataPoints.minOrNull() ?: 0.0
        return max - min
    }
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // You can handle changes in sensor accuracy here if needed
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x = event?.values?.get(0)?.toDouble()?: 0.0
        val y = event?.values?.get(1)?.toDouble()?: 0.0
        val z = event?.values?.get(2)?.toDouble()?: 0.0

        val tvAccelerometerData: TextView = findViewById(R.id.tvAccelerometerData)

        val data = calculateMagnitude(x, y, z)

        tvAccelerometerData.text = "X: $x\nY: $y\nZ: $z"
        buffer_1.add(data)
        //println(";$x, $y, $z;")
        //val myData = arrayOf(x.toString(), y.toString(), z.toString())
        //writeToCSV(myData);

    }
    fun calculateMagnitude(x: Double, y: Double, z: Double): Double {
        return kotlin.math.sqrt(x * x + y * y + z * z)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}