package com.example.myapplication


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
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
import java.io.File
import java.io.FileWriter
import java.io.IOException


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var vibrator: Vibrator
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val list = mutableListOf<String>()
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

        val END: LongArray = longArrayOf(200)
        val END_amplitudes: IntArray = intArrayOf(255)

        vibrateButton.setOnClickListener {
            vibrator.vibrate(VibrationEffect.createWaveform(START, START_amplitudes, -1))

            // Calculate the total duration of the first vibration pattern
            val firstPatternDuration = START.sum()
            // val timings: LongArray = longArrayOf(600, 167, 167, 167, 167, 167, 167, 1000, 167, 167, 167, 167, 167, 167)

//        while input is not None:
            val timings: LongArray =
                longArrayOf(85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85)
            val amplitudes: IntArray = numberAmplitudes('0')
            // vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))

            // Start the second vibration pattern after the first one completes
            Handler(Looper.getMainLooper()).postDelayed({
                val amplitudes: IntArray = numberAmplitudes('0')
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))

                // Calculate the total duration of the second vibration pattern
                val secondPatternDuration = timings.sum()

                // Handler to delay the execution of the third vibration pattern
                Handler(Looper.getMainLooper()).postDelayed({
                    vibrator.vibrate(VibrationEffect.createWaveform(END, END_amplitudes, -1))
                }, secondPatternDuration)
            }, firstPatternDuration)
        }
        // val amplitudes: IntArray = intArrayOf(255, 0, 0, 0, 255, 0, 255, 0, 255, 0, 0, 255, 0, 255)

//        val timings: LongArray = longArrayOf(600, 167, 167, 167, 167, 167, 167, 1000, 167, 167, 167, 167, 167, 167)
//        val amplitudes: IntArray = intArrayOf(255, 0, 0, 0, 255, 0, 255, 0, 255, 0, 0, 255, 0, 255)

//        val repeatIndex = -1 // Do not repeat.
//
//        vibrateButton.setOnClickListener {
//            //HI
//            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
//
//        }

        /*
        vibrateButton.setOnClickListener {
            // Vibrate for 1000 milliseconds (1 second)
            vibrator.vibrate(1000)
            Thread.sleep(3 * 1000)
            vibrator.vibrate(1000)
            Thread.sleep(3 * 1000)
            vibrator.vibrate(1000)
        }
        */

    }

    fun numberAmplitudes(char: Char): IntArray {
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
        val x = event?.values?.get(0)
        val y = event?.values?.get(1)
        val z = event?.values?.get(2)

        val tvAccelerometerData: TextView = findViewById(R.id.tvAccelerometerData)

        tvAccelerometerData.text = "X: $x\nY: $y\nZ: $z"
        list.add("$x, $y, $z")
        println(";$x, $y, $z;")
        //val myData = arrayOf(x.toString(), y.toString(), z.toString())
        //writeToCSV(myData);

    }

    fun writeToCSV(data: Array<String>) {
        val file = File(Environment.getExternalStorageDirectory(), "myData.csv")
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(file, true) // true to append, false to overwrite.
            for (field in data) {
                fileWriter.append(field)
                fileWriter.append(",")
            }
            fileWriter.append("\n") // Newline at the end of the row.
            fileWriter.flush()
        } catch (e: IOException) {
            // Handle exception
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: IOException) {
                    // Handle exception
                }
            }
        }
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
