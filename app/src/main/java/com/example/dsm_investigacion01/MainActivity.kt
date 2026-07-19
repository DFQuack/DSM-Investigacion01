package com.example.dsm_investigacion01

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dsm_investigacion01.R.color
import com.example.dsm_investigacion01.R.string
import com.example.dsm_investigacion01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addTaskButton.setOnClickListener {
            val taskName = binding.taskTextbox.text
            if (taskName.isNullOrBlank()) {
                binding.taskMessage.text = getString(string.task_message_failure)
                binding.taskMessage.setTextColor(ContextCompat.getColor(this, color.error))
                binding.taskMessage.visibility = View.VISIBLE
            } else {
                binding.taskMessage.text = getString(string.task_message_success)
                binding.taskMessage.setTextColor(ContextCompat.getColor(this, color.success))
                binding.taskMessage.visibility = View.VISIBLE
            }
        }
    }
}