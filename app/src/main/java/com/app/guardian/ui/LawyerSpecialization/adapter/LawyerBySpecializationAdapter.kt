package com.app.guardian.ui.LawyerSpecialization.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.LawyerBySpecialization.LawyerBySpecializationResp
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.ui.Lawyer.adapter.LawyerListAdapter
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.LawyerSpecialization.LawyerSpecializationFragment
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class LawyerBySpecializationAdapter(
    var context: Activity,
    var fragment: LawyerSpecializationFragment,
    var isDialLawyer: Boolean,
    var arrayList: ArrayList<LawyerBySpecializationResp>,
    var listeners: onItemClicklisteners
) : RecyclerView.Adapter<LawyerBySpecializationAdapter.myViewHolder>(), Filterable {
    var list: ArrayList<LawyerBySpecializationResp> = arrayList

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LawyerBySpecializationAdapter.myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_lawyer_list, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: LawyerBySpecializationAdapter.myViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        var imgPicture = view?.findViewById<CircleImageView>(R.id.imgRowLawyerPicture)
        var lyLawyerDetails = view?.findViewById<LinearLayout>(R.id.lyLawyerDetails)
        var imgRowLawyerCall = view?.findViewById<ImageView>(R.id.imgRowLawyerCall1)
        var imgRowLawyerVideo = view?.findViewById<ImageView>(R.id.imgRowLawyerVideo)
        var imgRowLawyerChat = view?.findViewById<ImageView>(R.id.imgRowLawyerChat)
        var tvLawyerName = view?.findViewById<TextView>(R.id.tvLawyerName)
        var tvLawyerExp = view?.findViewById<TextView>(R.id.tvLawyerExp)
        var tvLawyerSpecialization = view?.findViewById<TextView>(R.id.tvSpecialization)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {

            if (isDialLawyer) {
                imgRowLawyerVideo!!.gone()
                imgRowLawyerChat!!.gone()
            } else {
                imgRowLawyerVideo!!.visible()
                imgRowLawyerChat!!.visible()

            }

            val array = list[position]
            lyLawyerDetails?.setOnClickListener {
                listeners.onSubclick(array.id)
            }

            Glide.with(context).load(array.profile_avatar).placeholder(R.drawable.profile).into(
                imgPicture!!
            )
            imgRowLawyerCall?.setOnClickListener {
                if (!array.profile_avatar.isNullOrEmpty()) {
                    fragment.callShowLawyerContactDetails(
                        array.full_name!!,
                        "array.email",
                        array.dialing_code + array.phone!!,
                        array.profile_avatar!!,
                    )
                } else {
                    fragment.callShowLawyerContactDetails(
                        array.full_name!!,
                        "array.email!!",
                        array.dialing_code + array.phone!!,
                        "",
                    )
                }

            }

            imgPicture!!.setOnClickListener {
                lyLawyerDetails!!.performClick()
            }


            imgRowLawyerVideo?.setOnClickListener {
                if (SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_USER) {
                    fragment.displayVideoCallDialog(array.id)
                } else {
                    fragment.callVideoCallRequestAPI(
                        array.id!!,
                        AppConstants.APP_ROLE_LAWYER,
                        0,
                        "",
                    0
                    )
                }

            }

            imgRowLawyerChat?.setOnClickListener {
                if (array.profile_avatar.isNullOrEmpty() || array.last_seen.isNullOrEmpty()) {
                    fragment.callChatPageOpe(
                        array.id!!,
                        array.full_name!!,
                        "",
                        ""
                    )
                } else {
                    fragment.callChatPageOpe(
                        array.id!!,
                        array.full_name!!,
                        array.profile_avatar!!,
                        array.last_seen!!
                    )
                }

            }

            tvLawyerName?.text = array.full_name

            if ((array.years_of_experience?.toInt() ?: 0) == 1) {
                tvLawyerExp?.text =
                    "Experience - " + array.years_of_experience + "+ Year"
            } else {
                tvLawyerExp?.text =
                    "Experience - " + array.years_of_experience + "+ Years"
            }
            tvLawyerSpecialization?.text = array.specialization

        }


    }


    interface onItemClicklisteners {
        fun onSubclick(selectedLawyerId: Int?)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {

                val charString = charSequence.toString()
                list = if (charString.isEmpty()) {
                    arrayList
                } else {
                    val filterList: ArrayList<LawyerBySpecializationResp> = ArrayList()
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
                list = results?.values as ArrayList<LawyerBySpecializationResp>
                notifyDataSetChanged()
            }

        }
    }
}
