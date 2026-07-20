package com.example.dsm_investigacion01

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.activity.viewModels
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.dsm_investigacion01.databinding.ActivityPomodoroBinding
import kotlinx.coroutines.launch

class PomodoroLogic : AppCompatActivity(){
    private lateinit var binding: ActivityPomodoroBinding
    private val viewModel: PomodoroViewModel by viewModels();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPomodoroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnStartPause.setOnClickListener {viewModel.startPauseTimer()}
        binding.btnReset.setOnClickListener {viewModel.ResetTimer()}

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                viewModel.timeLeftMillis.collect { millis ->
                    val minutes = (millis / 1000) / 60
                    val seconds = (millis / 1000) % 60
                    binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRunning.collect { isRunning ->
                    binding.btnStartPause.text = if (isRunning) "Pausar" else "Iniciar"
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentState.collect { state ->
                    binding.tvPhaseStatus.text = state.displayName
                }
            }
        }


    }
    }