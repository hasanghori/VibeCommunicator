package com.example.myapplication


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
    
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Initialize the Vibrator service
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrateButton: Button = findViewById(R.id.button_vibrate)

        val timings: LongArray = longArrayOf(600, 167, 167, 167, 167, 167, 167, 1000, 167, 167, 167, 167, 167, 167)
        val amplitudes: IntArray = intArrayOf(255, 0, 0, 0, 255, 0, 255, 0, 255, 0, 0, 255, 0, 255)

        val repeatIndex = -1 // Do not repeat.

        vibrateButton.setOnClickListener {
            //HI
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))

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