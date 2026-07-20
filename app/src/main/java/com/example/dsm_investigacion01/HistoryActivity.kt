package com.example.dsm_investigacion01

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dsm_investigacion01.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var sessionStorage: SessionStorage
    private lateinit var sessionAdapter: SessionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionStorage = SessionStorage(this)

        // Configuramos el RecyclerView
        binding.rvSessions.layoutManager = LinearLayoutManager(this)

        // Cargamos las sesiones guardadas
        val sessions = sessionStorage.loadSessions()
        sessionAdapter = SessionAdapter(sessions)
        binding.rvSessions.adapter = sessionAdapter
    }
}