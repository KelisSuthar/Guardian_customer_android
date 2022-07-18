package com.app.guardian.ui.ContactedHistory.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.formatTimeInGMT2
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class ConnectedHistoryAdapter(

    var context: Activity,
    var type: String,
    var arrayList: ArrayList<ConnectedHistoryResp>,
    var listeners: onItemClicklisteners
) : RecyclerView.Adapter<ConnectedHistoryAdapter.myViewHolder>(), Filterable {
    var list: ArrayList<ConnectedHistoryResp>

    init {
        list = arrayList
    }

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
        return list.size
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {


        var txtName = view?.findViewById<TextView>(R.id.txtName)
        var txtExp = view?.findViewById<TextView>(R.id.txtExp)
        var txtDateTime = view?.findViewById<TextView>(R.id.txtDateTime)
        var txtSpecialization = view?.findViewById<TextView>(R.id.txtSpecialization)
        var imgRowLawyerPicture = view?.findViewById<CircleImageView>(R.id.imgRowLawyerPicture)
        var imgRowLawyerCall = view?.findViewById<ImageView>(R.id.imgRowLawyerCall)
        var imgRowLawyerChat = view?.findViewById<ImageView>(R.id.imgRowLawyerChat)
        var imgRowLawyerVideoCall = view?.findViewById<ImageView>(R.id.imgRowLawyerVideoCall)
        var imgRowLawyerNote = view?.findViewById<ImageView>(R.id.imgRowLawyerNotes)
        var txtSpTitle = view?.findViewById<TextView>(R.id.txtSpTitle)


        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val array = list[position]
            txtSpecialization!!.text = array.specialization
            if (SharedPreferenceManager.getString(
                    AppConstants.USER_ROLE,
                    ""
                ) == AppConstants.APP_ROLE_MEDIATOR
            ) {
                imgRowLawyerNote!!.gone()
            } else if (SharedPreferenceManager.getString(
                    AppConstants.USER_ROLE,
                    ""
                ) == AppConstants.APP_ROLE_USER
            ) {
                if (type == AppConstants.APP_ROLE_MEDIATOR) {
                    imgRowLawyerNote!!.gone()
                    imgRowLawyerChat!!.gone()
                    imgRowLawyerCall!!.gone()
                    imgRowLawyerVideoCall!!.gone()
                } else {
                    imgRowLawyerNote!!.gone()
                    imgRowLawyerChat!!.visible()
                    imgRowLawyerCall!!.visible()
                    imgRowLawyerVideoCall!!.visible()
                }

            } else if (SharedPreferenceManager.getString(
                    AppConstants.USER_ROLE,
                    ""
                ) == AppConstants.APP_ROLE_LAWYER
            ) {
                if (type == AppConstants.APP_ROLE_USER) {

                    imgRowLawyerNote!!.gone()
                    imgRowLawyerChat!!.visible()
                    imgRowLawyerCall!!.gone()
                    imgRowLawyerVideoCall!!.visible()

                    txtExp?.text = "User"
                    txtSpTitle?.text = "Location :"
                    txtSpecialization?.text = array.state
                } else {
                    imgRowLawyerNote!!.gone()
                    imgRowLawyerChat!!.visible()
                    imgRowLawyerCall!!.visible()
                    imgRowLawyerVideoCall!!.visible()
                }
            }

            if (type == AppConstants.APP_ROLE_LAWYER) {
                if (!array.years_of_experience.isNullOrEmpty()) {
                    if (array.years_of_experience!!.toInt() == 1) {
                        txtExp?.text =
                            "Experience - " + array.years_of_experience + "+ Year"
                    } else {
                        txtExp?.text =
                            "Experience - " + array.years_of_experience + "+ Years"
                    }
                }
            }

            imgRowLawyerCall?.setOnClickListener {
                listeners.onCallClick(
                    position,
                    array.id,
                    array.full_name,
                    array.email,
                    array.phone,
                    array.profile_avatar
                )
            }
            imgRowLawyerChat?.setOnClickListener { listeners.onChatClick(position, array.id) }
            imgRowLawyerVideoCall?.setOnClickListener {
                listeners.onVideCallClick(
                    position,
                    array.id,
                    array.full_name,
                )
            }
//            itemView.setOnClickListener { listeners.onNotesClick(position) }
            itemView.setOnClickListener { listeners.onItemClick(position, array.id) }

            txtName!!.text = array.full_name
            if (!array.years_of_experience.isNullOrEmpty()) {
                if (array.years_of_experience!!.toInt() == 1) {
                    txtExp?.text =
                        "Experience - " + array.years_of_experience + "+ Year"
                } else {
                    txtExp?.text =
                        "Experience - " + array.years_of_experience + "+ Years"
                }
            }


            if (!array.from_time.isNullOrEmpty()) {
                txtDateTime!!.text = array.from_time.formatTimeInGMT2()
            }
            Glide.with(context)
                .load(array.profile_avatar)
                .placeholder(R.drawable.profile)
                .into(imgRowLawyerPicture!!)
        }

    }

    interface onItemClicklisteners {
        fun onCallClick(
            position: Int,
            id: Int?,
            fullName: String?,
            email: String?,
            phone: String?,
            profileAvatar: String?
        )

        fun onChatClick(position: Int, id: Int?)
        fun onNotesClick(position: Int, id: Int?)
        fun onItemClick(position: Int, id: Int?)
        fun onVideCallClick(position: Int, id: Int?, fullName: String?)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {

                val charString = charSequence.toString()
                list = if (charString.isEmpty()) {
                    arrayList
                } else {
                    val filterList: ArrayList<ConnectedHistoryResp> = ArrayList()
                    for (i in list!!.indices) {
                        if (list[i].full_name!!.lowercase().contains(charString.lowercase())) {
                            filterList.add(list[i])
                        }
                    }
                    filterList
                }
                val filterResults = FilterResults()
                filterResults.values = list
                return filterResults

            }

            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                list = results?.values as ArrayList<ConnectedHistoryResp>
                notifyDataSetChanged()
            }

        }
    }
}