package com.sdapps.auraascend.view.home.fragments.funactivity.mindfulness

import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.SharedPrefHelper.Companion.SP_NAME

class MindfulnessActivity : AppCompatActivity() {

    private var countDownTimer: CountDownTimer? = null
    private var timerInSeconds: Long = 0
    private var activityCompletedCount = 0
    private lateinit var activityTrackerSP: SharedPreferences
    private lateinit var spEditor: SharedPreferences.Editor
    private lateinit var timerText: TextView
    private var currentPhase = INHALE

    companion object {
        const val INHALE = "INHALE"
        const val EXHALE = "EXHALE"
        const val BREATHE = "BREATHE"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mindfulness)
        activityTrackerSP = applicationContext.getSharedPreferences(SP_NAME, MODE_PRIVATE)
        spEditor = activityTrackerSP.edit()
        init()
    }

    private fun checkForPreviousAchievements(): Int {
        val sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE)
        val spCounter = sharedPreferences.getInt(SP_NAME, 0)
        return spCounter
    }

    private fun init() {
        timerText = findViewById(R.id.timerText)
        activityCompletedCount = checkForPreviousAchievements()
        startCount()
    }

    fun startCount() {
        val seconds = 30

        timerText.visibility = View.VISIBLE
        timerInSeconds = seconds.toLong()

        countDownTimer = object : CountDownTimer(timerInSeconds * 1000, 5000) {
            override fun onTick(millisUntilFinished: Long) {
                timerInSeconds = millisUntilFinished / 1000
                timerText.text = currentPhase

                when (currentPhase) {
                    INHALE -> {
                        findViewById<LottieAnimationView>(R.id.lottieAnimationView).apply {
                            visibility = View.VISIBLE
                            setAnimation(R.raw.breathe_in_animation)
                            playAnimation()
                        }
                        currentPhase = EXHALE
                    }

                    EXHALE -> {
                        currentPhase = INHALE
                    }
                }
            }

            override fun onFinish() {
                timerText.visibility = View.GONE
                findViewById<LottieAnimationView>(R.id.lottieAnimationView).apply {
                    visibility = View.VISIBLE
                    setAnimation(R.raw.completed_animation)
                    playAnimation()
                }
                findViewById<TextView>(R.id.activityCompleted).visibility = View.VISIBLE
                activityCompletedCount += 1
                sendToSp(activityCompletedCount)


                findViewById<Button>(R.id.finishBtn).apply {
                    visibility = View.VISIBLE
                    setOnClickListener { finish() }
                }
            }
        }.start()

    }

    private fun sendToSp(counter: Int) {
        spEditor.putInt(BREATHE, counter);
        spEditor.apply()
    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

}