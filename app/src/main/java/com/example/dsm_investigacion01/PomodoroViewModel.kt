package com.example.dsm_investigacion01

import android.content.IntentSender
import androidx.lifecycle.ViewModel
import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
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
    private var customWorkTimeMs: Long = PomodoroState.WORK.timeInMs
    private val _timeLeftMillis = MutableStateFlow(customWorkTimeMs)
    val timeLeftMillis: StateFlow<Long> = _timeLeftMillis

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning
    private val _progress = MutableStateFlow(100)
    val progress: StateFlow<Int> = _progress
    private val _sessionCompletedEvent = MutableSharedFlow<Unit>()

    val sessionCompletedEvent = _sessionCompletedEvent.asSharedFlow()

    fun setCustomWorkDuration(minutes: Int, seconds: Int) {
        customWorkTimeMs = (minutes * 60L + seconds) * 1000L
        if (_currentState.value == PomodoroState.WORK && !_isRunning.value) {
            _timeLeftMillis.value = customWorkTimeMs
            _progress.value = 100
        }
    }
    fun isTimerStarted(): Boolean {
        val maxTime = if (_currentState.value == PomodoroState.WORK) customWorkTimeMs else _currentState.value.timeInMs
        return _timeLeftMillis.value < maxTime && _timeLeftMillis.value > 0
    }


    private fun startTimer(duration: Long){
        _isRunning.value = true
        timer = object : CountDownTimer(duration,1000){
            override fun onTick(milisUntilFinished: Long) {
                _timeLeftMillis.value = milisUntilFinished
                val totalTime = if (_currentState.value == PomodoroState.WORK) customWorkTimeMs else _currentState.value.timeInMs
                val porcentaje = ((milisUntilFinished.toDouble() / totalTime) * 100).toInt()
                _progress.value = porcentaje
            }

            override fun onFinish() {
                _isRunning.value = false
                handlePhaseSwitch()

            }
        }.start()
    }


    private fun handlePhaseSwitch(){
        if (_currentState.value == PomodoroState.WORK) {
            viewModelScope.launch { _sessionCompletedEvent.emit(Unit) }
            currentPhaseCount++
            if (currentPhaseCount % 4 == 0) {
                _currentState.value = PomodoroState.LONG_BREAK
            } else {
                _currentState.value = PomodoroState.SHORT_BREAK
            }
        } else {
            _currentState.value = PomodoroState.WORK
        }

        val newTime = if (_currentState.value == PomodoroState.WORK) customWorkTimeMs else _currentState.value.timeInMs
        _timeLeftMillis.value = newTime
        _progress.value = 100
    }

    private fun pauseTimer(){
        timer?.cancel()
        _isRunning.value = false
    }
    private fun resetTimer(){
        timer?.cancel()
        _isRunning.value = false
        val newTime = if (_currentState.value == PomodoroState.WORK) customWorkTimeMs else _currentState.value.timeInMs
        _timeLeftMillis.value = newTime
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