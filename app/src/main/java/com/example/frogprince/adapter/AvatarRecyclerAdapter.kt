package com.example.frogprince.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frogprince.R
import com.example.frogprince.db.model.AvatarModel

class AvatarRecyclerAdapter() : RecyclerView.Adapter<AvatarRecyclerAdapter.ViewHolder>() {

    private var avatars = emptyList<AvatarModel>()
    var onItemClick: ((AvatarModel) -> Unit)? = null

    inner class ViewHolder(liview: View?) : RecyclerView.ViewHolder(liview!!) {
        val avaTitle = liview?.findViewById<TextView>(R.id.txtListAvaTitle)
        val avaBody = liview?.findViewById<TextView>(R.id.txtListAvaBody)
        var avaPosition = 0
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(avatars[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val avatar = avatars[position]
        holder.avaTitle?.text = "NickName : "+avatar.nickName
        holder.avaBody?.text = "Hint: "+avatar.memoryAid
        holder.avaPosition = position
    }

    override fun getItemCount(): Int {
        return avatars.size
    }

    fun setAvatars(avatars : List<AvatarModel>) {
        this.avatars = avatars
        notifyDataSetChanged()
    }

}