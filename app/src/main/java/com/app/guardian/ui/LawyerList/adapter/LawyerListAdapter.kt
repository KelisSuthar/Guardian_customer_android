package com.app.guardian.ui.Lawyer.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class LawyerListAdapter(
    var context: Activity,
    var lawyerListFragment: LawyerListFragment,
    var isDialLawyer: Boolean,
    var arrayList: ArrayList<LawyerListResp>,
    var listeners: onItemClicklisteners
) :

    RecyclerView.Adapter<LawyerListAdapter.myViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LawyerListAdapter.myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_lawyer_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LawyerListAdapter.myViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        var imgPicture = view?.findViewById<CircleImageView>(R.id.imgRowLawyerPicture)

        //        var cvRowSupportGroup = view?.findViewById<MaterialCardView>(R.id.cvRowSupportGroup)
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
            var lawyerProfileData: LawyerListResp? = null
            lawyerProfileData = arrayList[position]
            lyLawyerDetails?.setOnClickListener {
                listeners.onSubclick(lawyerProfileData.id)
            }
            Glide.with(context)
                .load(lawyerProfileData.profile_avatar)
                .placeholder(R.drawable.profile)
                .into(imgPicture!!)


            imgRowLawyerCall?.setOnClickListener {
                if (lawyerProfileData.profile_avatar.isNullOrEmpty()) {
                    lawyerListFragment.callShowLawyerContactDetails(
                        lawyerProfileData.full_name!!,
                        lawyerProfileData.email!!,
                        lawyerProfileData.phone!!,
                        "",
                    )
                } else {
                    lawyerListFragment.callShowLawyerContactDetails(
                        lawyerProfileData.full_name!!,
                        lawyerProfileData.email!!,
                        lawyerProfileData.phone!!,
                        lawyerProfileData.profile_avatar!!,
                    )
                }
            }

            imgPicture!!.setOnClickListener {
                lyLawyerDetails!!.performClick()
            }


            imgRowLawyerVideo?.setOnClickListener {
                if (SharedPreferenceManager.getString(
                        AppConstants.USER_ROLE,
                        ""
                    ) == AppConstants.APP_ROLE_USER
                ) {
                    lawyerListFragment.displayVideoCallDialog(lawyerProfileData.id)
                } else {
                    lawyerListFragment.callVideoCallRequestAPI(
                        lawyerProfileData.id!!,
                        AppConstants.APP_ROLE_LAWYER,
                        0,
                        ReusedMethod.getCurrentDate()
                    )
                }

            }

            imgRowLawyerChat?.setOnClickListener {
                if (lawyerProfileData.profile_avatar.isNullOrEmpty() || lawyerProfileData.last_seen.isNullOrEmpty()) {
                    lawyerListFragment.callChatPageOpe(
                        lawyerProfileData.id!!,
                        lawyerProfileData.full_name!!,
                        "",
                        "",
                    )
                } else {
                    lawyerListFragment.callChatPageOpe(
                        lawyerProfileData.id!!,
                        lawyerProfileData.full_name!!,
                        lawyerProfileData.profile_avatar!!,
                        lawyerProfileData.last_seen!!,
                    )
                }

            }

            tvLawyerName?.text = lawyerProfileData.full_name
            if (lawyerProfileData.years_of_experience!!.toInt() == 1) {
                tvLawyerExp?.text =
                    "Experience - " + lawyerProfileData.years_of_experience + " Year"
            } else {
                tvLawyerExp?.text =
                    "Experience - " + lawyerProfileData.years_of_experience + " Years"
            }

            tvLawyerExp?.text =
                "Experience - " + lawyerProfileData.years_of_experience + " " + context.resources.getString(
                    R.string.years
                )
            tvLawyerSpecialization?.text = lawyerProfileData.specialization

        }


    }


    interface onItemClicklisteners {
        fun onSubclick(selectedLawyerId: Int?)
    }
}