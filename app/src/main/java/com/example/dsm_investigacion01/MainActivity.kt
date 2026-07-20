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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskStorage = TaskStorage(this)
        taskAdapter = TaskAdapter(
            tasks = tasks,
            onTaskToggled = { task, isChecked ->
                task.completed = isChecked
                sortAndPersist()
            },
            onTaskDeleted = { task -> deleteTask(task) },
            onTaskClicked = { task ->
                val intent = Intent(this, PomodoroLogic::class.java).apply {
                    putExtra("TASK_NAME", task.name)
                }
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

        binding.btnIrPomodoro.setOnClickListener{
            val intent = Intent(this, PomodoroLogic::class.java)
            startActivity(intent)
        }
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
        // false (pending) sorts before true (completed) — completed tasks sink to the bottom.
        // sortBy is stable, so order within each group is preserved.
        tasks.sortBy { it.completed }
    }
    private fun refreshUi() {
        // Se pasa una lista no mutable de las tareas a ListAdapter para que este compare las diferencias con la última lista dada
        taskAdapter.submitList(tasks.toList())
        val hasTasks = tasks.isNotEmpty()
        binding.taskRecyclerView.visibility = if (hasTasks) View.VISIBLE else View.GONE
        binding.emptyTasksMessage.visibility = if (hasTasks) View.GONE else View.VISIBLE
    }
    private fun showMessage(text: String, colorRes: Int) {
        binding.taskMessage.text = text
        binding.taskMessage.setTextColor(ContextCompat.getColor(this, colorRes))
        binding.taskMessage.visibility = View.VISIBLE
    }
}