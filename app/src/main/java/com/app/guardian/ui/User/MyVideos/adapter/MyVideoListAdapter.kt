package com.app.guardian.ui.User.MyVideos.adapter

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.BaseRecyclerViewAdapter
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.Video.VideoResp
import com.bumptech.glide.Glide

class MyVideoListAdapter(val context: Context,val isShow: Boolean, val listener: onItemClicklisteners) :
    BaseRecyclerViewAdapter<VideoResp>(context) {
    override fun getViewHolder(view: View): RecyclerView.ViewHolder {
        return CustomViewHolder(view)
    }

    override fun getView(): Int {
        return R.layout.row_videos_layout
    }

    override fun setData(holder: RecyclerView.ViewHolder, data: VideoResp, position: Int) {
        val customViewHolder = holder as CustomViewHolder
        customViewHolder.bindData(data, position)
    }

    open inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var positions: Int? = 0
        var appCompatImageView = view.findViewById<AppCompatImageView>(R.id.appCompatImageView)
        var ivClose = view.findViewById<AppCompatImageView>(R.id.ivClose)
        init {
            if(isShow){
                ivClose.visible()
            }else{
                ivClose.gone()
            }
        }
        open fun bindData(data: VideoResp, position: Int) {
            positions = position
            Glide.with(context).load(data.path).placeholder(R.drawable.ic_video_placeholder)
                .into(appCompatImageView)

            itemView.setOnClickListener { listener.onItemClick(position) }
            ivClose.setOnClickListener { listener.onItemDeleteClick(position) }
        }
    }

    interface onItemClicklisteners {
        fun onItemClick(position: Int?)
        fun onItemDeleteClick(position: Int?)
    }


}