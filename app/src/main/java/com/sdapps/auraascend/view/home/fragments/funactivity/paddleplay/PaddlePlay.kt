package com.sdapps.auraascend.view.home.fragments.funactivity.paddleplay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.CustomProgressDialog
import com.sdapps.auraascend.databinding.ActivityPaddlePlayBinding
import com.sdapps.auraascend.view.home.fragments.funactivity.paddleplay.PaddleView.Companion.PADDLE_WIDTH

class PaddlePlay : AppCompatActivity(), BallView.OnGameOverListener {

    private lateinit var binding: ActivityPaddlePlayBinding
    private var handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var highScoreSharedPreference: SharedPreferences
    private lateinit var spEditor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaddlePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.apply {
            decorView.systemUiVisibility = (
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }
        highScoreSharedPreference =
            applicationContext.getSharedPreferences("HighScorePref", MODE_PRIVATE)
        spEditor = highScoreSharedPreference.edit()
        setupHighScore()
        binding.ball.setUpGameOver(this)
        init()

    }

    private fun setupHighScore() {
        val highScoreValue = highScoreSharedPreference.getInt("HIGH_SCORE", 0)
        val str = "HI: $highScoreValue"
        binding.highScore.text = str
    }


    private fun init() {

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val initialPaddleX = (binding.root.width - PADDLE_WIDTH) / 2f
                binding.paddle.setPaddlePosition(initialPaddleX)
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        binding.ball.resetBall()
        startGameLoop()
    }

    private fun handleCollisions() {
        updatePaddleHitCount(binding.ball.paddleHitCount)
    }

    private fun startGameLoop() {
        val frameRate = 16
        runnable = object : Runnable {
            override fun run() {
                binding.ball.moveBallWithPaddle(binding.paddle.paddleXAxis, PADDLE_WIDTH)
                handleCollisions()
                handler.postDelayed(this, frameRate.toLong())
            }
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)

    }

    override fun gameOver() {
        val savedScore = highScoreSharedPreference.getInt("HIGH_SCORE", 0)
        val currentScore = binding.ball.paddleHitCount

        if (savedScore == 0) {
            spEditor.putInt("HIGH_SCORE", currentScore)
            spEditor.apply()
        } else if (currentScore >= savedScore) {
            spEditor.clear()
            spEditor.putInt("HIGH_SCORE", currentScore)
            spEditor.apply()
        }
        setupHighScore()
        binding.ball.stopGame()
        binding.ball.resetBall()
        showRestartDialog()
        updatePaddleHitCount(0)

    }

    private fun showRestartDialog() {
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle(R.string.game_over)
            .setMessage("High Score is : ${binding.ball.paddleHitCount}. Do you want to restart?")
            .setCancelable(false)
            .setPositiveButton(R.string.restart) { dialog, _ ->
                restartGame()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.quit) { dialog, _ ->
                dialog.dismiss()
                finish()
            }.create()



        // Overriding the theme of dialog with custom font and size.
        dialog.setOnShowListener {
            val typF = ResourcesCompat.getFont(this, R.font.manrope_bold)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                typeface = typF
                setTextColor(ContextCompat.getColor(context, R.color.Olive))
                textSize = 16f
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                typeface = typF
                setTextColor(ContextCompat.getColor(context, R.color.Olive))
                textSize = 16f

            }
        }

        dialog.show()
    }

    private fun restartGame() {
        binding.ball.restartGame()
        binding.paddle.setPaddlePosition((resources.displayMetrics.widthPixels - PADDLE_WIDTH) / 2f)
        binding.ball.isGameOver = false
    }

    private fun updatePaddleHitCount(hitCount: Int) {
        binding.counter.text = hitCount.toString()
    }
}