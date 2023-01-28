package com.example.todolistproject.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.db.entities.ToDo
import com.example.todolistproject.ui.utils.convertMillisecondsIntoDateTimeString

import com.google.android.material.card.MaterialCardView
import java.util.Calendar

class ToDoListAdapter(val context: Context) :
    RecyclerView.Adapter<ToDoListAdapter.ToDoViewHolder>() {
    val calendar = Calendar.getInstance()

    class ToDoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val description = view.findViewById<TextView>(R.id.tvDescription)
        val dueDateTime = view.findViewById<TextView>(R.id.tvDueDateTime)
        val categoryTitle = view.findViewById<TextView>(R.id.tvCategoryTitle)
        val cardView = view.findViewById<MaterialCardView>(R.id.materialCardView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ToDoListAdapter.ToDoViewHolder {
        return ToDoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        )
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "ResourceAsColor")
    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val todo = differ.currentList[position]

        todo.apply {
            holder.description.text = description
            holder.dueDateTime.text = convertMillisecondsIntoDateTimeString(dueDateTime)
            if (calendar.timeInMillis > dueDateTime) {
                holder.dueDateTime.setTextColor(context.resources.getColor(R.color.red_700))
            }else {
                holder.dueDateTime.setTextColor(context.resources.getColor(R.color.white))
            }
            holder.categoryTitle.text = ""
            holder.cardView.setOnLongClickListener {
                onItemLongClickListener?.let { it1 -> it1(this, holder.cardView) }
                true
            }

            holder.cardView.setOnClickListener {
                onItemClickListener?.let { it1 -> it1(this, holder.cardView) }
            }
        }

    }

    private val differUtilCallback = object : DiffUtil.ItemCallback<ToDo>() {

        override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differUtilCallback)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setTodos(newTodos: List<ToDo>) {
        differ.submitList(newTodos)
    }

    private var onItemClickListener: ((todo: ToDo, view: MaterialCardView) -> Unit)? = null
    private var onItemLongClickListener: ((todo: ToDo, view: MaterialCardView) -> Unit)? = null

    fun setOnItemLongClickListener(listener: (todo: ToDo, view: MaterialCardView) -> Unit) {
        onItemLongClickListener = listener
    }

    fun setOnItemClickListener(listener: (todo: ToDo, view: MaterialCardView) -> Unit) {
        onItemClickListener = listener
    }


}
