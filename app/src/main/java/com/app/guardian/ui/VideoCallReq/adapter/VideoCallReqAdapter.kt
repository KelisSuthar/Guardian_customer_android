package com.app.guardian.ui.VideoCallReq.adapter

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.BaseRecyclerViewAdapter
import com.app.guardian.common.extentions.gone
import com.app.guardian.model.GetVideoCallRequestResp.VideoCallRequestListResp
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class VideoCallReqAdapter(val context: Context, val listener: onItemClicklisteners) :
    BaseRecyclerViewAdapter<VideoCallRequestListResp>(context) {
    override fun getViewHolder(view: View): RecyclerView.ViewHolder {
        return CustomViewHolder(view)
    }

    override fun getView(): Int {
        return R.layout.row_lawyer_contact
    }

    override fun setData(
        holder: RecyclerView.ViewHolder,
        data: VideoCallRequestListResp,
        position: Int
    ) {
        val customViewHolder = holder as CustomViewHolder
        customViewHolder.bindData(data, position)
    }

    open inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var positions: Int? = 0
        var imgRowLawyerPicture = view.findViewById<CircleImageView>(R.id.imgRowLawyerPicture)
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
        open fun bindData(data: VideoCallRequestListResp, position: Int) {
            imgRowLawyerCall.gone()
            imgRowLawyerNotes.gone()
            imgRowLawyerChat.gone()
            positions = position
            Glide.with(context).load(data.profile_avatar)
                .placeholder(R.drawable.profile)
                .into(imgRowLawyerPicture)
            txtName.text = data.full_name
            itemView.setOnClickListener { listener.onItemClick(position) }
            imgRowLawyerVideoCall.setOnClickListener { listener.onVideoCallClick(position) }
            when (data.user_role) {
                AppConstants.APP_ROLE_USER -> {
                    txtExp.text = AppConstants.USER
                    txtSpTitle.text = "Location :"
                    txtSpecialization.text = data.state!!.replace("/", ",")
                }
                AppConstants.APP_ROLE_LAWYER -> {
                    txtExp.text = AppConstants.LAWYEER
                    txtSpTitle.text = "Specialization :"
                    txtSpecialization.text = data.specialization
                }
                AppConstants.APP_ROLE_MEDIATOR -> {
                    txtExp.text = AppConstants.MEDIATOR
                }
            }

            txtDateTime.text = "Status : " + data.request_status
        }
    }

    interface onItemClicklisteners {
        fun onItemClick(position: Int?)
        fun onVideoCallClick(position: Int?)
    }
}