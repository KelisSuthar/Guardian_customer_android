package com.app.guardian.ui.LawyerVideoCallReq.adapter

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
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.Circle
import de.hdodenhof.circleimageview.CircleImageView

class LawyerVideoCallReqAdapter(val context: Context, val listener: onItemClicklisteners) :
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
        var imgRowLawyerPicture = view.findViewById<CircleImageView>(R.id.imgRowLawyerPicture)
        var txtName = view.findViewById<AppCompatTextView>(R.id.txtName)
        var txtExp = view.findViewById<AppCompatTextView>(R.id.txtExp)
        var txtDateTime = view.findViewById<AppCompatTextView>(R.id.txtDateTime)
        var txtSpTitle = view.findViewById<AppCompatTextView>(R.id.txtSpTitle)
        var txtSpecialization = view.findViewById<AppCompatTextView>(R.id.txtSpecialization)
        var lyRowLawyerContact = view.findViewById<LinearLayout>(R.id.lyRowLawyerContact)
        open fun bindData(data: GetVideoCallRequestListResp, position: Int) {
            lyRowLawyerContact.gone()
            positions = position
            Glide.with(context).load(data.user_detail.profile_avatar)
                .placeholder(R.drawable.profile)
                .into(imgRowLawyerPicture)

            itemView.setOnClickListener { listener.onItemClick(position) }
        }
    }


    interface onItemClicklisteners {
        fun onItemClick(position: Int?)
    }

}