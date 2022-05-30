package com.app.guardian.ui.SupportGroups.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.model.SupportGroup.SupportGroupResp
import com.app.guardian.ui.Lawyer.adapter.LawyerListAdapter
import com.google.android.material.card.MaterialCardView
import de.hdodenhof.circleimageview.CircleImageView

class SupportGroupListAdapter(
    var context: Activity,

    var arrayList: ArrayList<SupportGroupResp>,
    var listeners: SupportGroupListAdapter.onItemClicklisteners
) :

    RecyclerView.Adapter<SupportGroupListAdapter.myViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SupportGroupListAdapter.myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_support_group, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SupportGroupListAdapter.myViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        var txtRowSupportGroup = view?.findViewById<AppCompatTextView>(R.id.txtRowSupportGroup)


        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            txtRowSupportGroup!!.text = arrayList[position].title

            itemView.setOnClickListener { listeners.onClick(position) }
        }

    }


    interface onItemClicklisteners {
        fun onClick(position: Int)
    }
}