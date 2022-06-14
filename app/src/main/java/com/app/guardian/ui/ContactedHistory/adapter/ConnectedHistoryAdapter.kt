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
import com.app.guardian.common.AppConstants
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.formatTimeInGMT2
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.loadImage
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.ui.Lawyer.adapter.LawyerListAdapter
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import de.hdodenhof.circleimageview.CircleImageView

class ConnectedHistoryAdapter(

    var context: Activity,
    var type: String,
    var arrayList: ArrayList<ConnectedHistoryResp>,
    var listeners: onItemClicklisteners
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
        var imgRowLawyerNote = view?.findViewById<ImageView>(R.id.imgRowLawyerNotes)
        var txtSpTitle = view?.findViewById<TextView>(R.id.txtSpTitle)


        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val array = arrayList[position]
            if (SharedPreferenceManager.getString(
                    AppConstants.USER_ROLE,
                    ""
                ) == AppConstants.APP_ROLE_MEDIATOR
            ) {

            } else if (SharedPreferenceManager.getString(
                    AppConstants.USER_ROLE,
                    ""
                ) == AppConstants.APP_ROLE_USER
            ) {

            } else if (SharedPreferenceManager.getString(
                    AppConstants.USER_ROLE,
                    ""
                ) == AppConstants.APP_ROLE_LAWYER
            ) {
                if (type == AppConstants.APP_ROLE_USER) {
                    imgRowLawyerNote!!.gone()
                    imgRowLawyerChat!!.visible()
                    imgRowLawyerCall!!.gone()
                } else {
                    imgRowLawyerNote!!.visible()
                    imgRowLawyerChat!!.visible()
                    imgRowLawyerCall!!.visible()
                }
            }

            if (type == AppConstants.APP_ROLE_LAWYER) {
                if (!array.years_of_experience.isNullOrEmpty()) {
                    if (array.years_of_experience!!.toInt() == 1) {
                        txtExp?.text =
                            "Experience - " + array.years_of_experience + " Year"
                    } else {
                        txtExp?.text =
                            "Experience - " + array.years_of_experience + " Years"
                    }
                }
            }

            imgRowLawyerCall?.setOnClickListener { listeners.onCallClick(position) }
            imgRowLawyerChat?.setOnClickListener { listeners.onChatClick(position) }
            itemView.setOnClickListener { listeners.onNotesClick(position) }

            txtName!!.text = array.full_name
            if (!array.years_of_experience.isNullOrEmpty()) {
                if (array.years_of_experience!!.toInt() == 1) {
                    txtExp?.text =
                        "Experience - " + array.years_of_experience + " Year"
                } else {
                    txtExp?.text =
                        "Experience - " + array.years_of_experience + " Years"
                }
            }
            txtSpecialization!!.text = array.specialization

            txtDateTime!!.text = array.from_time!!.formatTimeInGMT2()



            if (array.user_role == AppConstants.APP_ROLE_USER) {
                imgRowLawyerChat!!.visible()
                imgRowLawyerCall!!.gone()
                txtExp?.text = "User"
                txtSpTitle?.text = "Location :"
                txtSpecialization!!.text = array.state
            }


            Glide.with(context)
                .load(array.profile_avatar)
                .placeholder(R.drawable.profile)
                .into(imgRowLawyerPicture!!)


        }

    }

    interface onItemClicklisteners {
        fun onCallClick(position: Int)
        fun onChatClick(position: Int?)
        fun onNotesClick(position: Int?)
        fun onItemClick(position: Int?)
    }
}