package com.example.myapplication


import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.Arrays


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var vibrator: Vibrator
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val list = mutableListOf<String>()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Initialize the Vibrator service
//        val vibrationManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
//        vibrator = vibrationManager.defaultVibrator
//        val vibrateButton: Button = findViewById(R.id.button_vibrate)

        val timings: LongArray = longArrayOf(600, 167, 167, 167, 167, 167, 167, 1000, 167, 167, 167, 167, 167, 167)
        val amplitudes: IntArray = intArrayOf(255, 0, 0, 0, 255, 0, 255, 0, 255, 0, 0, 255, 0, 255)

        val repeatIndex = -1 // Do not repeat.

//        vibrateButton.setOnClickListener {
            //HI
//            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))

//        }
    }

    // this function will be called every time a
    // players tap in an empty box of the grid
    fun playerTap(view: View) {
        val img = view as ImageView
        val tappedImage = img.tag.toString().toInt()

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
//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
//        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // You can handle changes in sensor accuracy here if needed
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x = event?.values?.get(0)
        val y = event?.values?.get(1)
        val z = event?.values?.get(2)

//        val tvAccelerometerData: TextView = findViewById(R.id.tvAccelerometerData)

//        tvAccelerometerData.text = "X: $x\nY: $y\nZ: $z"
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