package com.app.guardian.ui.VideoCallReq.adapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.BaseRecyclerViewAdapter
import com.app.guardian.common.extentions.gone
import com.app.guardian.model.GetVideoCallRequestResp.GetVideoCallRequestListResp
import com.app.guardian.ui.LawyerVideoCallReq.adapter.LawyerVideoCallReqAdapter
import com.bumptech.glide.Glide

class VideoCallReqAdapter(val context: Context, val listener: onItemClicklisteners) :
    BaseRecyclerViewAdapter<GetVideoCallRequestListResp>(context) {
    override fun getViewHolder(view: View): RecyclerView.ViewHolder {
        return CustomViewHolder(view)
    }

    override fun getView(): Int {
        return R.layout.row_lawyer_contact
    }

    override fun setData(
        holder: RecyclerView.ViewHolder,
        data: GetVideoCallRequestListResp,
        position: Int
    ) {
        val customViewHolder = holder as CustomViewHolder
        customViewHolder.bindData(data, position)
    }

    open inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var positions: Int? = 0
        var imgRowLawyerPicture = view.findViewById<AppCompatImageView>(R.id.imgRowLawyerPicture)
        var imgRowLawyerCall = view.findViewById<AppCompatImageView>(R.id.imgRowLawyerCall)
        var imgRowLawyerNotes = view.findViewById<AppCompatImageView>(R.id.imgRowLawyerNotes)
        var imgRowLawyerVideoCall =
            view.findViewById<AppCompatImageView>(R.id.imgRowLawyerVideoCall)
        var imgRowLawyerChat = view.findViewById<AppCompatImageView>(R.id.imgRowLawyerChat)
        var txtName = view.findViewById<AppCompatTextView>(R.id.txtName)
        var txtExp = view.findViewById<AppCompatTextView>(R.id.txtExp)
        var txtDateTime = view.findViewById<AppCompatTextView>(R.id.txtDateTime)
        var txtSpTitle = view.findViewById<AppCompatTextView>(R.id.txtSpTitle)
        var txtSpecialization = view.findViewById<AppCompatTextView>(R.id.txtSpecialization)
        open fun bindData(data: GetVideoCallRequestListResp, position: Int) {
            imgRowLawyerCall.gone()
            imgRowLawyerNotes.gone()
            imgRowLawyerChat.gone()
            positions = position
            Glide.with(context).load(data.user_detail.profile_avatar)
                .placeholder(R.drawable.ic_video_placeholder)
                .into(imgRowLawyerPicture)

            itemView.setOnClickListener { listener.onItemClick(position) }
            imgRowLawyerVideoCall.setOnClickListener { listener.onVideoCallClick(position) }
        }
    }

    interface onItemClicklisteners {
        fun onItemClick(position: Int?)
        fun onVideoCallClick(position: Int?)
    }
}