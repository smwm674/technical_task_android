package com.sliide.technicaltask.ui.mainactivity.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sliide.technicaltask.data.model.User
import com.sliide.technicaltask.databinding.ItemLayoutBinding
import com.sliide.technicaltask.utils.Utils.setText

class RecyclerViewAdapter(
    private var userList: ArrayList<User>,
    private var listener: longClickListener
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(userList.get(position))
    }

    override fun getItemCount() = userList.size

    interface longClickListener {
        fun onLongClick(data: User, position: Int)
    }

    inner class ViewHolder(val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: User) {
            binding.apply {
                setText(name, data.name)
                setText(email, data.email)
                setText(creationTime, data.id.toString())
                item.setOnLongClickListener {
                    if (listener != null)
                        listener.onLongClick(data, adapterPosition)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    fun addUser(user: User) {
        userList.add(user)
        notifyDataSetChanged()
    }

    fun removeUser(position: Int) {
        userList.removeAt(position)
        notifyItemRemoved(position)
    }
}
