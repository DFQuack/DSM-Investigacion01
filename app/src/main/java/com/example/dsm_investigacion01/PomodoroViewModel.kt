package com.example.dsm_investigacion01

import android.content.IntentSender
import androidx.lifecycle.ViewModel
import android.os.CountDownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

class PomodoroViewModel : ViewModel(){

    enum class PomodoroState(val timeInMs: Long, val displayName: String) {
        WORK(25 * 60 * 1000L, "Tiempo de Enfoque"),
        SHORT_BREAK(5 * 60 * 1000L, "Descanso Corto"),
        LONG_BREAK(15 * 60 * 1000L, "Descanso Largo")
    }

    private var timer : CountDownTimer? = null
    private  var currentPhaseCount = 0
    private val _currentState = MutableStateFlow(PomodoroState.WORK)
    val currentState: StateFlow<PomodoroState> = _currentState

    private val _timeLeftMillis = MutableStateFlow(PomodoroState.WORK.timeInMs)
    val timeLeftMillis: StateFlow<Long> = _timeLeftMillis

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning
    private val _progress = MutableStateFlow(100)
    val progress: StateFlow<Int> = _progress

    private fun startTimer(duration: Long){
        _isRunning.value = true
        timer = object : CountDownTimer(duration,1000){
            override fun onTick(milisUntilFinished: Long) {
                _timeLeftMillis.value = milisUntilFinished
                val totaltime = _currentState.value.timeInMs
                val porcentage = ((milisUntilFinished.toDouble() / totaltime)* 100).toInt()
                _progress.value = porcentage
            }

            override fun onFinish() {
                _isRunning.value = false
                handlePhaseSwitch()

            }
        }.start()
    }


    private fun handlePhaseSwitch(){
        if (_currentState.value == PomodoroState.WORK)
        {
            currentPhaseCount++
            if (currentPhaseCount % 4 == 0){
                _currentState.value = PomodoroState.LONG_BREAK
            }
            else
            {
                _currentState.value = PomodoroState.SHORT_BREAK
            }
        }
        else
        {
            _currentState.value = PomodoroState.WORK
        }
        _timeLeftMillis.value = _currentState.value.timeInMs

    }
    private fun pauseTimer(){
        timer?.cancel()
        _isRunning.value = false
    }
    private fun resetTimer(){
        timer?.cancel()
        _isRunning.value = false
        _timeLeftMillis.value = _currentState.value.timeInMs
        _progress.value = 100
    }
    fun startPauseTimer(){
        if(_isRunning.value){
        pauseTimer()
        }else{
        startTimer(_timeLeftMillis.value)
        }
    }
    fun ResetTimer()
    {
        resetTimer()
    }

}