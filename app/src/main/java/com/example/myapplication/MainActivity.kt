package com.example.myapplication


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
import java.util.Arrays
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
    private var buffer_1: MutableList<Double> = mutableListOf()
    private val buffer_2: List<Double> = listOf()

    var gameActive = true

    // Player representation
    // 0 - X
    // 1 - O
    var activePlayer = 0

    // State meanings:
    //    0 - X
    //    1 - O
    //    2 - Null
    var gameState = intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2)

    // put all win positions in a 2D array
    var winPositions = arrayOf(
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8),
        intArrayOf(2, 4, 6)
    )
    var counter = 0
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
            buffer_1 = mutableListOf()
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

    private fun numberAmplitudes(char: Char): IntArray {
        return when (char) {
            '0' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0)
            '1' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 128, 0, 128, 0, 128, 0, 255, 0)
            '2' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0)
            '3' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 128, 0, 128, 0, 255, 0, 255, 0)
            '4' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 128, 0, 255, 0, 128, 0, 128, 0)
            '5' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 128, 0, 255, 0, 128, 0, 255, 0)
            '6' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 128, 0, 255, 0, 255, 0, 128, 0)
            '7' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 128, 0, 255, 0, 255, 0, 255, 0)
            '8' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 255, 0, 128, 0, 128, 0, 128, 0)
            '9' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 255, 0, 128, 0, 128, 0, 255, 0)
            ':' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 255, 0, 128, 0, 255, 0, 128, 0)
            ';' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 255, 0, 128, 0, 255, 0, 255, 0)
            '<' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 255, 0, 255, 0, 128, 0, 128, 0)
            '=' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 255, 0, 255, 0, 128, 0, 255, 0)
            '>' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 128, 0)
            '?' -> intArrayOf(128, 0, 128, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0)
            '@' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0, 128, 0, 128, 0)
            'A' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0, 128, 0, 255, 0)
            'B' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0, 255, 0, 128, 0)
            'C' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0, 255, 0, 255, 0)
            'D' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0)
            'E' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 255, 0, 128, 0, 255, 0)
            'F' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 255, 0, 255, 0, 128, 0)
            'G' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 255, 0, 255, 0, 255, 0)
            'H' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0)
            'I' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0)
            'J' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 255, 0, 128, 0)
            'K' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 255, 0, 255, 0)
            'L' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 255, 0, 128, 0, 128, 0)
            'M' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 255, 0, 128, 0, 255, 0)
            'N' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 255, 0, 255, 0, 128, 0)
            'O' -> intArrayOf(128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 255, 0, 255, 0, 255, 0)
            'P' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0)
            'Q' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 255, 0)
            'R' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0)
            'S' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 255, 0)
            'T' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 128, 0)
            'U' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 255, 0)
            'V' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 128, 0)
            'W' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 255, 0)
            'X' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 128, 0, 128, 0, 128, 0)
            'Y' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 128, 0, 128, 0, 255, 0)
            'Z' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 128, 0, 255, 0, 128, 0)
            '[' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 128, 0, 255, 0, 255, 0)
            ']' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 255, 0, 128, 0, 255, 0)
            '^' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 255, 0, 255, 0, 128, 0)
            '_' -> intArrayOf(128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0)
            '`' -> intArrayOf(128, 0, 255, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0, 128, 0)
            '{' -> intArrayOf(128, 0, 255, 0, 255, 0, 255, 0, 255, 0, 128, 0, 255, 0, 255, 0)
            '|' -> intArrayOf(128, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 128, 0, 128, 0)
            '}' -> intArrayOf(128, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 128, 0, 255, 0)
            '~' -> intArrayOf(128, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 128, 0)
            '!' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0, 255, 0)
            '"' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 255, 0, 128, 0)
            '#' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 255, 0, 255, 0)
            '$' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 128, 0)
            '%' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 128, 0, 255, 0)
            '&' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0, 255, 0, 128, 0)
            '(' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0)
            ')' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 128, 0, 255, 0)
            '*' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 255, 0, 128, 0)
            '+' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 255, 0, 128, 0, 255, 0, 255, 0)
            ',' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 128, 0, 128, 0)
            '-' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 128, 0, 255, 0)
            '.' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 255, 0, 128, 0)
            '/' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 255, 0, 255, 0, 255, 0, 255, 0)
            ' ' -> intArrayOf(128, 0, 128, 0, 255, 0, 128, 0, 128, 0, 128, 0, 128, 0, 128, 0)
            else -> intArrayOf()
        }
    }
    /*
    fun playerTapReciever(view) {
        val img = view as ImageView
        val tappedImage = img.tag.toString().toInt()
        println(tappedImage)

        // game reset function will be called
        // if someone wins or the boxes are full
        if (!gameActive) {
            gameReset(view)
            //Reset the counter
            counter = 0
        }

        // if the tapped image is empty
        if (gameState[tappedImage] === 2) {
            // increase the counter
            // after every tap
            counter++

            // check if its the last box
            if (counter == 9) {
                // reset the game
                gameActive = false
            }

            // mark this position
            gameState[tappedImage] = activePlayer

            // this will give a motion
            // effect to the image
            img.translationY = -1000f

            // change the active player
            // from 0 to 1 or 1 to 0
            if (activePlayer == 0) {
                // set the image of x
                img.setImageResource(R.drawable.x)
                activePlayer = 1
                val status = findViewById<TextView>(R.id.status)

                // change the status
                status.text = "O's Turn - Tap to play"
            } else {
                // set the image of o
                img.setImageResource(R.drawable.o)
                activePlayer = 0
                val status = findViewById<TextView>(R.id.status)

                // change the status
                status.text = "X's Turn - Tap to play"
            }
            img.animate().translationYBy(1000f).duration = 300

            // send message
            val START: LongArray = longArrayOf(188, 188, 188, 188)
            val START_amplitudes: IntArray = intArrayOf(255, 0, 128, 0)
            vibrator.vibrate(VibrationEffect.createWaveform(START, START_amplitudes, -1))
            val timings: LongArray =
                longArrayOf(85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85)
            val duration = timings.sum()

            var amplitudes: IntArray = numberAmplitudes(tappedImage.toString().first())
            println("SIZE")
            println(tappedImage.toString().first())
            println(amplitudes.size)
            println(timings.size)
            Handler(Looper.getMainLooper()).postDelayed({
                amplitudes = numberAmplitudes(tappedImage.toString().first())

                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }, duration)
        }
        var flag = 0
        // Check if any player has won if counter is > 4 as min 5 taps are
        // required to declare a winner
        if (counter > 4) {
            for (winPosition in winPositions) {
                if (gameState[winPosition[0]] === gameState[winPosition[1]] && gameState[winPosition[1]] === gameState[winPosition[2]] && gameState[winPosition[0]] !== 2) {
                    flag = 1

                    // Somebody has won! - Find out who!
                    var winnerStr: String

                    // game reset function be called
                    gameActive = false
                    winnerStr = if (gameState[winPosition[0]] === 0) {
                        "X has won"
                    } else {
                        "O has won"
                    }
                    // Update the status bar for winner announcement
                    val status = findViewById<TextView>(R.id.status)
                    status.text = winnerStr
                }
            }
            // set the status if the match draw
            if (counter == 9 && flag == 0) {
                val status = findViewById<TextView>(R.id.status)
                status.text = "Match Draw"
            }
        }
    }
    */


    // this function will be called every time a
    // players tap in an empty box of the grid
    fun playerTap(view: View) {
        val img = view as ImageView
        val tappedImage = img.tag.toString().toInt()
        println(tappedImage)

        // game reset function will be called
        // if someone wins or the boxes are full
        if (!gameActive) {
            gameReset(view)
            //Reset the counter
            counter = 0
        }

        // if the tapped image is empty
        if (gameState[tappedImage] === 2) {
            // increase the counter
            // after every tap
            counter++

            // check if its the last box
            if (counter == 9) {
                // reset the game
                gameActive = false
            }

            // mark this position
            gameState[tappedImage] = activePlayer

            // this will give a motion
            // effect to the image
            img.translationY = -1000f

            // change the active player
            // from 0 to 1 or 1 to 0
            if (activePlayer == 0) {
                // set the image of x
                img.setImageResource(R.drawable.x)
                activePlayer = 1
                val status = findViewById<TextView>(R.id.status)

                // change the status
                status.text = "O's Turn - Tap to play"
            } else {
                // set the image of o
                img.setImageResource(R.drawable.o)
                activePlayer = 0
                val status = findViewById<TextView>(R.id.status)

                // change the status
                status.text = "X's Turn - Tap to play"
            }
            img.animate().translationYBy(1000f).duration = 300

            // send message
            val START: LongArray = longArrayOf(188, 188, 188, 188)
            val START_amplitudes: IntArray = intArrayOf(255, 0, 128, 0)
            vibrator.vibrate(VibrationEffect.createWaveform(START, START_amplitudes, -1))
            val timings: LongArray =
                longArrayOf(85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85)
            val duration = timings.sum()

            var amplitudes: IntArray = numberAmplitudes(tappedImage.toString().first())
            println("SIZE")
            println(tappedImage.toString().first())
            println(amplitudes.size)
            println(timings.size)
            Handler(Looper.getMainLooper()).postDelayed({
                amplitudes = numberAmplitudes(tappedImage.toString().first())

                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }, duration)
        }
        var flag = 0
        // Check if any player has won if counter is > 4 as min 5 taps are
        // required to declare a winner
        if (counter > 4) {
            for (winPosition in winPositions) {
                if (gameState[winPosition[0]] === gameState[winPosition[1]] && gameState[winPosition[1]] === gameState[winPosition[2]] && gameState[winPosition[0]] !== 2) {
                    flag = 1

                    // Somebody has won! - Find out who!
                    var winnerStr: String

                    // game reset function be called
                    gameActive = false
                    winnerStr = if (gameState[winPosition[0]] === 0) {
                        "X has won"
                    } else {
                        "O has won"
                    }
                    // Update the status bar for winner announcement
                    val status = findViewById<TextView>(R.id.status)
                    status.text = winnerStr
                }
            }
            // set the status if the match draw
            if (counter == 9 && flag == 0) {
                val status = findViewById<TextView>(R.id.status)
                status.text = "Match Draw"
            }
        }
    }

    fun gameReset(view: View?) {
        gameActive = true
        activePlayer = 0
        //set all position to Null
        Arrays.fill(gameState, 2)
        // remove all the images from the boxes inside the grid
        (findViewById<View>(R.id.imageView0) as ImageView).setImageResource(0)
        (findViewById<View>(R.id.imageView1) as ImageView).setImageResource(0)
        (findViewById<View>(R.id.imageView2) as ImageView).setImageResource(0)
        (findViewById<View>(R.id.imageView3) as ImageView).setImageResource(0)
        (findViewById<View>(R.id.imageView4) as ImageView).setImageResource(0)
        (findViewById<View>(R.id.imageView5) as ImageView).setImageResource(0)
        (findViewById<View>(R.id.imageView6) as ImageView).setImageResource(0)
        (findViewById<View>(R.id.imageView7) as ImageView).setImageResource(0)
        (findViewById<View>(R.id.imageView8) as ImageView).setImageResource(0)
        val status = findViewById<TextView>(R.id.status)
        status.text = "X's Turn - Tap to play"
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


        val data = calculateMagnitude(x, y, z)

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