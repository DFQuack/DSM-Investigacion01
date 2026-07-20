package com.example.dsm_investigacion01

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.dsm_investigacion01.databinding.ActivityPomodoroBinding
import kotlinx.coroutines.launch

class PomodoroLogic : AppCompatActivity() {
    private lateinit var binding: ActivityPomodoroBinding
    private lateinit var sessionStorage: SessionStorage
    private val viewModel: PomodoroViewModel by viewModels()

    private var currentSessionDurationMinutes = 25

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPomodoroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionStorage = SessionStorage(this)

        val taskName = intent.getStringExtra("TASK_NAME")
        if (taskName != null) {
            binding.tvWorkingOn.text = "Trabajando en: $taskName"
        }

        // Lógica para aplicar minutos y segundos
        binding.btnSetTime.setOnClickListener {
            val minText = binding.etCustomMinutes.text.toString().trim()
            val secText = binding.etCustomSeconds.text.toString().trim()

            val minutes = if (minText.isNotEmpty()) minText.toIntOrNull() ?: 0 else 0
            val seconds = if (secText.isNotEmpty()) secText.toIntOrNull() ?: 0 else 0

            if (minutes > 0 || seconds > 0) {
                // Guardamos para el historial (mínimo 1 minuto en historial si solo hiciste prueba de segundos)
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

        binding.btnStartPause.setOnClickListener { viewModel.startPauseTimer() }
        binding.btnReset.setOnClickListener { viewModel.ResetTimer() }

        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Concentramos todos los observadores en un solo Lifecycle
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.timeLeftMillis.collect { millis ->
                        val minutes = (millis / 1000) / 60
                        val seconds = (millis / 1000) % 60
                        binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
                        updateButtonState() // Actualiza texto de botón si el tiempo cambia
                    }
                }

                launch {
                    viewModel.isRunning.collect {
                        updateButtonState() // Actualiza texto de botón al pausar/iniciar
                    }
                }

                launch {
                    viewModel.currentState.collect { state ->
                        binding.tvPhaseStatus.text = state.displayName
                    }
                }

                launch {
                    viewModel.sessionCompletedEvent.collect {
                        val session = Session(durationMinutes = currentSessionDurationMinutes)
                        sessionStorage.addSession(session)
                    }
                }
            }
        }
    }

    // NUEVO: Función que evalúa qué texto poner en el botón
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