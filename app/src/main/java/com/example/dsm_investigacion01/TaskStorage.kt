package com.example.dsm_investigacion01

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class TaskStorage(context: Context) {
    // App-specific external storage: no permissions needed on modern Android,
    // and it's automatically cleaned up if the app is uninstalled.
    private val file: File = File(context.getExternalFilesDir(null), "tasks.json")

    fun loadTasks(): MutableList<Task> {
        if (!file.exists()) return mutableListOf()
        val content = file.readText()
        if (content.isBlank()) return mutableListOf()

        val tasks = mutableListOf<Task>()
        val jsonArray = JSONArray(content)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            tasks.add(
                Task(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    completed = obj.getBoolean("completed")
                )
            )
        }
        return tasks
    }

    fun saveTasks(tasks: List<Task>) {
        val jsonArray = JSONArray()
        for (task in tasks) {
            val obj = JSONObject()
            obj.put("id", task.id)
            obj.put("name", task.name)
            obj.put("completed", task.completed)
            jsonArray.put(obj)
        }
        file.writeText(jsonArray.toString())
    }
}