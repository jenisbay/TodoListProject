package com.example.todolistproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.db.entities.Category

class CategoryListAdapter : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    private var categories = emptyList<Category>()

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
        val btnActionEditCategory: ImageButton = view.findViewById(R.id.btnActionEditCategory)
        val btnActionDeleteCategory: ImageButton = view.findViewById(R.id.btnActionDeleteCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        with(callback.currentList[position]) {
            holder.tvCategoryName.text = title

            if (title == "Default") {
                holder.btnActionDeleteCategory.visibility = View.GONE
                holder.btnActionEditCategory.visibility = View.GONE

            } else {

                holder.btnActionEditCategory.setOnClickListener {
                    onEditActionClickListener?.let { it(this) }
                }
                holder.btnActionDeleteCategory.setOnClickListener {
                    onDeleteActionClickListener?.let { it(this) }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return callback.currentList.size
    }

    fun setCategories(newCategories: List<Category>) {
        callback.submitList(newCategories)
    }

    private val callBackDiffer = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    private val callback = AsyncListDiffer(this, callBackDiffer)

    private var onEditActionClickListener: ((category: Category) -> Unit)? =
        null
    private var onDeleteActionClickListener: ((category: Category) -> Unit)? =
        null

    fun setOnEditActionClickListener(listener: (category: Category) -> Unit) {
        onEditActionClickListener = listener
    }

    fun setOnDeleteActionClickListener(listener: (category: Category) -> Unit) {
        onDeleteActionClickListener = listener
    }

}