package com.example.dsm_investigacion01

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dsm_investigacion01.databinding.ItemSessionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SessionAdapter(private var sessions: List<Session>) :
    RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    class SessionViewHolder(val binding: ItemSessionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]

        // Formatear el timestamp a una fecha y hora legible
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val dateString = sdf.format(Date(session.timestamp))

        holder.binding.tvSessionDate.text = "Pomodoro Completado: $dateString"
        holder.binding.tvSessionDuration.text = "Duración: ${session.durationMinutes} minutos"
    }

    override fun getItemCount() = sessions.size

    fun updateData(newSessions: List<Session>) {
        sessions = newSessions
        notifyDataSetChanged()
    }
}