package com.app.guardian.ui.User.MyVideos.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.BaseRecyclerViewAdapter

class MyVideoListAdapter(val context: Context, val listener: onItemClicklisteners) :
    BaseRecyclerViewAdapter<String>(context) {
    override fun getViewHolder(view: View): RecyclerView.ViewHolder {
        return CustomViewHolder(view)
    }

    override fun getView(): Int {
        return R.layout.row_videos_layout
    }

    override fun setData(holder: RecyclerView.ViewHolder, data: String, position: Int) {
        val customViewHolder = holder as CustomViewHolder
        customViewHolder.bindData(data, position)
    }

    open inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var positions: Int? = 0
        open fun bindData(data: String, position: Int) {
            positions = position
        }
    }

    interface onItemClicklisteners {
        fun onItemClick(position: Int?)
    }
}