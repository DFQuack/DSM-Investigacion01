package com.example.dsm_investigacion01

import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.activity.viewModels
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.dsm_investigacion01.databinding.ActivityPomodoroBinding
import kotlinx.coroutines.launch

class PomodoroLogic : AppCompatActivity() {
    private lateinit var binding: ActivityPomodoroBinding
    private val viewModel: PomodoroViewModel by viewModels();
    private lateinit var sessionStorage: SessionStorage

    private var currentSessionDurationMinutes = 25
    private var currentTaskName = "Sesión Libre"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPomodoroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionStorage = SessionStorage(this)

        val taskName = intent.getStringExtra("TASK_NAME")
        if (taskName != null) {
            currentTaskName = taskName
            binding.tvWorkingOn.text = "Trabajando en: $taskName"
        }

        binding.btnSetTime.setOnClickListener {
            val minText = binding.etCustomMinutes.text.toString().trim()
            val secText = binding.etCustomSeconds.text.toString().trim()

            val minutes = if (minText.isNotEmpty()) minText.toIntOrNull() ?: 0 else 0
            val seconds = if (secText.isNotEmpty()) secText.toIntOrNull() ?: 0 else 0

            if (minutes > 0 || seconds > 0) {
                currentSessionDurationMinutes = if (minutes == 0) 1 else minutes
                viewModel.setCustomWorkDuration(minutes, seconds)

                binding.etCustomMinutes.text.clear()
                binding.etCustomSeconds.text.clear()
                binding.etCustomMinutes.clearFocus()
                binding.etCustomSeconds.clearFocus()
            } else {
                Toast.makeText(this, "Ingresa un tiempo válido", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.btnStartPause.setOnClickListener { viewModel.startPauseTimer() }
        binding.btnReset.setOnClickListener { viewModel.ResetTimer() }

        binding.btnVolverPrincipal.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                viewModel.progress.collect { porcentaje -> binding.pomodoroProgressBar.progress = porcentaje }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                viewModel.sessionCompletedEvent.collect {
                    val session = Session(
                        durationMinutes = currentSessionDurationMinutes,
                        taskName = currentTaskName
                    )
                    sessionStorage.addSession(session)
                }
            }


        }


    }
    private fun updateButtonState() {
        val isRunning = viewModel.isRunning.value
        val hasStarted = viewModel.isTimerStarted()

        binding.btnStartPause.text = when {
            isRunning -> "Pausar"
            hasStarted -> "Reanudar"
            else -> "Iniciar"
        }
    }
}