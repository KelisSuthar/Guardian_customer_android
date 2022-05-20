package com.app.guardian.ui.ContactedHistory.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.extentions.gone
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.ui.Lawyer.adapter.LawyerListAdapter
import com.google.android.material.card.MaterialCardView
import de.hdodenhof.circleimageview.CircleImageView

class ConnectedHistoryAdapter(
    var isUser: Boolean,
    var context: Activity,
    var arrayList: ArrayList<ConnectedHistoryResp>,
    var listeners: ConnectedHistoryAdapter.onItemClicklisteners
) : RecyclerView.Adapter<ConnectedHistoryAdapter.myViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConnectedHistoryAdapter.myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_lawyer_contact, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ConnectedHistoryAdapter.myViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {


        var txtName = view?.findViewById<TextView>(R.id.txtName)
        var txtExp = view?.findViewById<TextView>(R.id.txtExp)
        var txtDateTime = view?.findViewById<TextView>(R.id.txtDateTime)
        var txtSpecialization = view?.findViewById<TextView>(R.id.txtSpecialization)
        var imgRowLawyerPicture = view?.findViewById<CircleImageView>(R.id.imgRowLawyerPicture)
        var imgRowLawyerCall = view?.findViewById<ImageView>(R.id.imgRowLawyerCall)
        var imgRowLawyerChat = view?.findViewById<ImageView>(R.id.imgRowLawyerChat)


        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val array = arrayList[position]
            if (isUser) {
                imgRowLawyerCall!!.gone()
                imgRowLawyerChat!!.gone()
            }
            imgRowLawyerCall?.setOnClickListener { listeners.onCallClick(position) }
            imgRowLawyerChat?.setOnClickListener { listeners.onChatClick(position) }
            itemView.setOnClickListener { listeners.onItemClick(position) }

        }

    }

    interface onItemClicklisteners {
        fun onCallClick(position: Int)
        fun onChatClick(position: Int?)
        fun onItemClick(position: Int?)
    }
}