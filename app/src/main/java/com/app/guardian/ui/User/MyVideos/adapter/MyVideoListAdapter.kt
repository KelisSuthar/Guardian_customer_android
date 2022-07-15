package com.app.guardian.ui.User.MyVideos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.BaseRecyclerViewAdapter
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.Notification.NotificationResp
import com.app.guardian.model.Video.VideoResp
import com.bumptech.glide.Glide

class MyVideoListAdapter(
    val context: Context,
    val isShow: Boolean,
    var arrayList: ArrayList<VideoResp>,
    val listener: onItemClicklisteners
) : RecyclerView.Adapter<MyVideoListAdapter.CustomViewHolder>() {

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
//            if (data.is_Show == true) {
                if (data.isSelected == true) {
                    ivSelect.setImageResource(R.drawable.ic_outline_check_circle_outline)
                } else {
                    ivSelect.setImageResource(R.drawable.ic_outline_circle)
                }
                Glide.with(context).load(data.path).placeholder(R.drawable.ic_video_placeholder)
                    .into(appCompatImageView)

                llplay.setOnClickListener { listener.onItemClick(position) }
                itemView.setOnClickListener { listener.onItemSelect(position) }
                ivClose.setOnClickListener { listener.ontItemDelete(position) }
//            }
        }
    }

    interface onItemClicklisteners {
        fun onItemClick(position: Int?)
        fun onItemSelect(position: Int?)
        fun ontItemDelete(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_videos_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bindData(arrayList[position], position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


}