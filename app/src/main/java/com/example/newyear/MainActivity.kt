package com.example.newyear

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    lateinit var infotext: TextView
    lateinit var bellImage: ImageView
    lateinit var targetCalendar: Calendar
    private var isActive = false
    val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    var nextYear: Int? = null
    var actualBell = 0
    private var toneGenerator: ToneGenerator? = null
    private var vibrator: Vibrator? = null
    var isAnimating = false
    private lateinit var alphaAnimator: ValueAnimator
    val baseColor = Color.argb(0, 255, 251, 48)
    val changedColor = Color.argb(30, 255, 251, 48)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val startButton: Button = findViewById(R.id.startButton)
        infotext = findViewById(R.id.timeText)
        bellImage = findViewById(R.id.bellImage)

        val animator = ObjectAnimator.ofFloat(
            startButton,
            "translationX",
            resources.displayMetrics.widthPixels.toFloat() * -1,
            0f
        )
        animator.duration = 1000L
        animator.start()

        targetCalendar = Calendar.getInstance()
        nextYear = targetCalendar.weekYear + 1
        targetCalendar.set(nextYear!!, Calendar.JANUARY, 1, 0, 0, 0)
        startButton.setOnClickListener {
            if (!isActive) {
                isActive = true
                startButton.visibility = View.GONE
                val timeDifference =
                    targetCalendar.timeInMillis - Calendar.getInstance().timeInMillis
                countdown(abs(timeDifference))
            }
        }
    }

    fun countdown(timeDifference: Long) {
        object : CountDownTimer(timeDifference, 1L) {
            override fun onTick(millisUntilFinished: Long) {
                val timeDiff =
                    targetCalendar.timeInMillis - Calendar.getInstance().timeInMillis
                if (abs(timeDiff) <= 10000 && !isAnimating) {
                    isAnimating = true
                    val scaleUp = ScaleAnimation(
                        1f,
                        0.8f,
                        1f,
                        0.8f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    scaleUp.duration = 500L
                    scaleUp.repeatCount = Animation.INFINITE
                    scaleUp.repeatMode = Animation.REVERSE
                    infotext.startAnimation(scaleUp)
                    alphaAnimator = ValueAnimator.ofFloat(1f, 0.3f)
                    alphaAnimator.duration = 1000L
                    alphaAnimator.repeatCount = ValueAnimator.INFINITE
                    alphaAnimator.repeatMode = ValueAnimator.REVERSE
                    alphaAnimator.addUpdateListener { animator ->
                        val alpha = animator.animatedValue as Float
                        infotext.alpha = alpha
                    }
                    alphaAnimator.start()
                }
                val time: String = simpleDateFormat.format(timeDiff - 3600000L)
                infotext.text = "Quedan:$time"
            }

            override fun onFinish() {
                infotext.clearAnimation()
                alphaAnimator.cancel()
                infotext.alpha = 1f
                infotext.text = actualBell.toString()
                bellImage.visibility = View.VISIBLE
                bellCounter()
            }
        }.start()
    }

    fun bellCounter() {
        object : CountDownTimer(33000L, 3000L) {
            override fun onTick(millisUntilFinished: Long) {
                actualBell++
                infotext.text = actualBell.toString()
                vibrator?.vibrate(400)
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP)
                bellImage.rotation *= -1
            }

            override fun onFinish() {
                actualBell++
                infotext.text = actualBell.toString()
                bellImage.rotation *= -1
                infotext.setShadowLayer(5f, 5f, 5f, Color.BLACK)
                infotext.setTextColor(Color.WHITE)
                val finalText = findViewById<TextView>(R.id.finalText)
                finalText.text = "FELIZ AÑO NUEVO"
                val scaleUp = ScaleAnimation(
                    1f,
                    1.2f,
                    1f,
                    1.2f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                scaleUp.duration = 500L
                scaleUp.repeatCount = Animation.INFINITE
                scaleUp.repeatMode = Animation.REVERSE
                finalText.startAnimation(scaleUp)
                vibrator?.vibrate(400)
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP)
                val newYearBackground = findViewById<ImageView>(R.id.newYearBackgroundImg)
                val newYearImg = findViewById<ImageView>(R.id.happyNewYearImg)
                newYearBackground.visibility = View.VISIBLE
                newYearImg.visibility = View.VISIBLE
                val animator = ObjectAnimator.ofFloat(
                    newYearImg,
                    "translationY",
                    resources.displayMetrics.heightPixels.toFloat(),
                    0f
                )
                animator.duration = 1000L
                animator.start()
                val animatorImg = ObjectAnimator.ofFloat(
                    findViewById(R.id.linearLayout),
                    "rotation",
                    25f,
                    -25f
                )
                animatorImg.duration = 500L
                animatorImg.repeatCount = ValueAnimator.INFINITE
                animatorImg.repeatMode = ValueAnimator.REVERSE
                animatorImg.start()
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), baseColor, changedColor)
                colorAnimation.duration = 500L
                colorAnimation.repeatCount = ValueAnimator.INFINITE
                colorAnimation.repeatMode = ValueAnimator.REVERSE
                colorAnimation.addUpdateListener { animator ->
                    val color = animator.animatedValue as Int
                    findViewById<ImageView>(R.id.newYearBackgroundImg).setColorFilter(color)
                }
                colorAnimation.start()
            }
        }.start()
    }
}