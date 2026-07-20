package com.example.dsm_investigacion01

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dsm_investigacion01.R.color
import com.example.dsm_investigacion01.R.string
import com.example.dsm_investigacion01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskStorage: TaskStorage
    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<Task>()

    private lateinit var sessionStorage: SessionStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskStorage = TaskStorage(this)
        sessionStorage = SessionStorage(this) // Inicializamos SessionStorage

        taskAdapter = TaskAdapter(
            tasks = tasks,
            onTaskToggled = { task, isChecked ->
                task.completed = isChecked
                sortAndPersist()
            },
            onTaskDeleted = { task -> deleteTask(task) },
            onTaskClicked = { task ->
                val intent = Intent(this, PomodoroLogic::class.java)
                intent.putExtra("TASK_NAME", task.name)
                startActivity(intent)
            }
        )

        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.taskRecyclerView.adapter = taskAdapter

        loadTasks()

        binding.addTaskButton.setOnClickListener {
            val taskName = binding.taskTextbox.text?.toString()?.trim()
            if (taskName.isNullOrBlank()) {
                showMessage(getString(string.task_message_failure), color.error)
            } else {
                tasks.add(Task(name = taskName))
                sortAndPersist()
                binding.taskTextbox.text?.clear()
                showMessage(getString(string.task_message_success), color.success)
            }
        }

        binding.btnIrPomodoro.setOnClickListener {
            val intent = Intent(this, PomodoroLogic::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateSummary() // Se actualiza al volver de la pantalla de Pomodoro
    }

    private fun loadTasks() {
        tasks.clear()
        tasks.addAll(taskStorage.loadTasks())
        sortTasksInPlace()
        refreshUi()
    }

    private fun deleteTask(task: Task) {
        tasks.remove(task)
        sortAndPersist()
    }

    private fun sortAndPersist() {
        sortTasksInPlace()
        taskStorage.saveTasks(tasks)
        refreshUi()
    }

    private fun sortTasksInPlace() {
        tasks.sortBy { it.completed }
    }

    private fun refreshUi() {
        taskAdapter.submitList(tasks.toList())
        val hasTasks = tasks.isNotEmpty()
        binding.taskRecyclerView.visibility = if (hasTasks) View.VISIBLE else View.GONE
        binding.emptyTasksMessage.visibility = if (hasTasks) View.GONE else View.VISIBLE
        updateSummary() // Actualiza las estadísticas de tareas y sesiones
    }

    private fun updateSummary() {
        val pendingCount = tasks.count { !it.completed }

        // Carga las sesiones completadas guardadas
        val completedCount = try {
            sessionStorage.loadSessions().size
        } catch (e: Exception) {
            0
        }

        binding.tvSummary.text = "$pendingCount pendientes · $completedCount sesiones completadas"
    }

    private fun showMessage(text: String, colorRes: Int) {
        binding.taskMessage.text = text
        binding.taskMessage.setTextColor(ContextCompat.getColor(this, colorRes))
        binding.taskMessage.visibility = View.VISIBLE
    }
}

