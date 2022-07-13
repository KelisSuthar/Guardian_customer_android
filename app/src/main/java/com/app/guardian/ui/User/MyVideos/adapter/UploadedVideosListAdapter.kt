package com.app.guardian.ui.User.MyVideos.adapter

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.BaseRecyclerViewAdapter
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.OfflineVideos.OfflineUploadedVideoResp
import com.bumptech.glide.Glide

class UploadedVideosListAdapter(
    val context: Context,
    val listener: onItemClicklisteners
) :
    BaseRecyclerViewAdapter<OfflineUploadedVideoResp>(context) {
    interface onItemClicklisteners {
        fun onItemClick(position: Int)
        fun onItemDeleteClick(position: Int)
    }

    override fun getViewHolder(view: View): RecyclerView.ViewHolder {
        return CustomViewHolder(view)
    }


    override fun getView(): Int {
        return R.layout.row_videos_layout
    }

    override fun setData(
        holder: RecyclerView.ViewHolder,
        data: OfflineUploadedVideoResp,
        position: Int
    ) {
        val customViewHolder = holder as CustomViewHolder
        customViewHolder.bindData(data, position)
    }

    open inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var appCompatImageView = view.findViewById<AppCompatImageView>(R.id.appCompatImageView)
        var ivClose = view.findViewById<AppCompatImageView>(R.id.ivClose)
        var ivSelect = view.findViewById<AppCompatImageView>(R.id.ivSelect)

        open fun bindData(data: OfflineUploadedVideoResp, position: Int) {
            Glide.with(context).load(data.video_url).placeholder(R.drawable.ic_video_placeholder)
                .into(appCompatImageView)

            itemView.setOnClickListener { listener.onItemClick(position) }
            ivClose.setOnClickListener { listener.onItemDeleteClick(position) }
        }
    }
}