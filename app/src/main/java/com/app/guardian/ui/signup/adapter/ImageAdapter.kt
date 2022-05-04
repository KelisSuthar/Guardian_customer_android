package com.app.guardian.ui.signup.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.bumptech.glide.Glide

class ImageAdapter(
    var context: Activity,
    var arrayList: ArrayList<String>,
    var listeners: onItemClicklisteners
) : RecyclerView.Adapter<ImageAdapter.myViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter.myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.image_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageAdapter.myViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int {
//        return arrayList.size
        return 5

    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var img = view?.findViewById<ImageView>(R.id.ivImage)
        var cancel = view?.findViewById<ImageView>(R.id.ivCancel)
        fun bindItem(position: Int) {
//
//            Glide.with(context)
//                .load(arrayList[position])
//                .into(img!!)
            cancel?.setOnClickListener { listeners.onCancelCick(position) }
        }
    }

    interface onItemClicklisteners {
        fun onCancelCick(position: Int)
    }
}