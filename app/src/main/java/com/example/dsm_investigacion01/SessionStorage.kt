package com.example.dsm_investigacion01

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class SessionStorage(context: Context) {
    private val file: File = File(context.getExternalFilesDir(null), "sessions.json")

    fun loadSessions(): MutableList<Session> {
        if (!file.exists()) return mutableListOf()
        val content = file.readText()
        if (content.isBlank()) return mutableListOf()

        val sessions = mutableListOf<Session>()
        val jsonArray = JSONArray(content)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            sessions.add(
                Session(
                    id = obj.getString("id"),
                    timestamp = obj.getLong("timestamp"),
                    durationMinutes = obj.getInt("durationMinutes")
                )
            )
        }
        return sessions
    }

    fun addSession(session: Session) {
        val sessions = loadSessions()
        // Agregamos la nueva sesión al inicio para que el historial muestre lo más reciente arriba
        sessions.add(0, session)

        val jsonArray = JSONArray()
        for (s in sessions) {
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("timestamp", s.timestamp)
            obj.put("durationMinutes", s.durationMinutes)
            jsonArray.put(obj)
        }
        file.writeText(jsonArray.toString())
    }
}