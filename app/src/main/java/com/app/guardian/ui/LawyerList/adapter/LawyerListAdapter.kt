package com.app.guardian.ui.Lawyer.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.google.android.material.card.MaterialCardView
import de.hdodenhof.circleimageview.CircleImageView

class LawyerListAdapter(
    var context: Activity,
    var arrayList: ArrayList<LawyerListResp> ,
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

    inner class myViewHolder(view: View?): RecyclerView.ViewHolder(view!!){

        var imgPicture = view?.findViewById<CircleImageView>(R.id.imgRowLawyerPicture)
        var cvRowSupportGroup = view?.findViewById<MaterialCardView>(R.id.cvRowSupportGroup)
        var imgRowLawyerCall = view?.findViewById<ImageView>(R.id.imgRowLawyerCall)
        var imgRowLawyerVideo = view?.findViewById<ImageView>(R.id.imgRowLawyerVideo)
        var imgRowLawyerChat = view?.findViewById<ImageView>(R.id.imgRowLawyerChat)
        var tvLawyerName = view?.findViewById<TextView>(R.id.tvLawyerName)
        var tvLawyerExp = view?.findViewById<TextView>(R.id.tvLawyerExp)
        var tvLawyerSpecialization= view?.findViewById<TextView>(R.id.tvSpecialization)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int){

            val lawyerProfileData = arrayList[position]
            cvRowSupportGroup?.setOnClickListener {
                listeners.onSubclick(lawyerProfileData.id)
            }

            tvLawyerName?.text = lawyerProfileData.full_name
            tvLawyerExp?.text ="Experience - "+ lawyerProfileData.years_of_experience
            tvLawyerSpecialization?.text = lawyerProfileData.specialization

        }

    }


    interface onItemClicklisteners {
        fun onSubclick(selectedLawyerId: Int?)
    }
}