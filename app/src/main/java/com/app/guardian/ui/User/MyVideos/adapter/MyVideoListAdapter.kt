package com.app.guardian.ui.User.MyVideos.adapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.BaseRecyclerViewAdapter
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.Video.VideoResp
import com.bumptech.glide.Glide

class MyVideoListAdapter(
    val context: Context,
    val isShow: Boolean,
    val listener: onItemClicklisteners
) :
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
        var ivSelect = view.findViewById<AppCompatImageView>(R.id.ivSelect)
        var llplay = view.findViewById<LinearLayout>(R.id.llplay)

        init {
            if (isShow) {
                ivSelect.visible()
            } else {
                ivSelect.gone()
            }
        }

        open fun bindData(data: VideoResp, position: Int) {
            if (data.is_Show == true) {
                itemView.gone()
            } else {
                itemView.visible()
            }
            if (data.isSelected == true) {
                ivSelect.setImageResource(R.drawable.ic_outline_check_circle_outline)
            } else {
                ivSelect.setImageResource(R.drawable.ic_outline_circle)
            }
            positions = position
            Glide.with(context).load(data.path).placeholder(R.drawable.ic_video_placeholder)
                .into(appCompatImageView)

            llplay.setOnClickListener { listener.onItemClick(position) }
            itemView.setOnClickListener { listener.onItemSelect(position) }
            ivClose.setOnClickListener { listener.ontItemDelete(position) }
        }
    }

    interface onItemClicklisteners {
        fun onItemClick(position: Int?)
        fun onItemSelect(position: Int?)
        fun ontItemDelete(position: Int)
    }


}