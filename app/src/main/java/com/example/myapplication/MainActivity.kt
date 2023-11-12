package com.example.myapplication


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
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
    var file = File(Environment.getExternalStorageDirectory(), "/Users/hasan/Documents/College/MastersFall/accelerometer_data_android.csv")
    var fileWriter: FileWriter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        // Initialize the Vibrator service
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrateButton: Button = findViewById(R.id.button_vibrate)

        vibrateButton.setOnClickListener {
            // Vibrate for 1000 milliseconds (1 second)
            vibrator.vibrate(1000)
            Thread.sleep(3 * 1000)
            vibrator.vibrate(1000)
            Thread.sleep(3 * 1000)
            vibrator.vibrate(1000)
        }


    }
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // You can handle changes in sensor accuracy here if needed
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val tvAccelerometerData: TextView = findViewById(R.id.tvAccelerometerData)

            tvAccelerometerData.text = "X: $x\nY: $y\nZ: $z"
            list.add("$x, $y, $z")
            println(";$x, $y, $z;")
            //val myData = arrayOf(x.toString(), y.toString(), z.toString())
            //writeToCSV(myData);
        }
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