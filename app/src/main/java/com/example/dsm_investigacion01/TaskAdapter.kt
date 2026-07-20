package com.example.dsm_investigacion01

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dsm_investigacion01.databinding.ItemTaskBinding

/*
 * TaskAdapter: puente entre los datos y las filas de tareas
 * Es de tipo ListAdapter para manejar de forma eficiente el renderizado de los elementos cuando estos cambian
 */
class TaskAdapter(
    private val tasks: MutableList<Task>,
    // Funciones declaradas como propiedades. Unit indica que no devuelven nada.
    private val onTaskToggled: (Task, Boolean) -> Unit,
    private val onTaskDeleted: (Task) -> Unit,
    private val onTaskClicked: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {



    /*
    * La clase no tiene brackets porque al colocar val en el parámetro, Kotlin lo interpreta así:
    * la propiedad binding se crea y se asigna con el valor del parámetro
    */
    class TaskViewHolder(val binding: ItemTaskBinding) :
    RecyclerView.ViewHolder(binding.root)

    // Infla una nueva fila cuando no hay suficientes creadas para reciclar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    // Coloca los datos en las vistas cada vez que una tarea necesita ser mostrada en una fila específica
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        val context = holder.itemView.context
        val binding = holder.binding

        binding.taskNameText.text = task.name
        // Se coloca como null para evitar activar el evento al establecer el valor del chequeado
        binding.taskCheckbox.setOnCheckedChangeListener(null)
        binding.taskCheckbox.isChecked = task.completed

        if (task.completed) {
            binding.taskNameText.paintFlags = binding.taskNameText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.taskNameText.setTextColor(ContextCompat.getColor(context, R.color.task_completed))
        } else {
            binding.taskNameText.paintFlags = binding.taskNameText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            binding.taskNameText.setTextColor(ContextCompat.getColor(context, R.color.task_pending))
        }

        binding.root.setOnClickListener { onTaskClicked(task) }
        /*
        * Por convención, un parámetro lambda no usado se coloca como _
        * En este contexto, se refiere al checkbox presionado.
         */
        binding.taskCheckbox.setOnCheckedChangeListener {
            _, isChecked -> onTaskToggled(task, isChecked)
        }
        binding.deleteTaskButton.setOnClickListener {
            onTaskDeleted(task)
        }
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}