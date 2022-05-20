package com.app.guardian.ui.KnowRight.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.model.KnowYourRights.KnowYourRightsResp
import com.google.android.material.card.MaterialCardView

class KnowYourRightsAdapter(
    var context: Activity,
    var arrayList: ArrayList<KnowYourRightsResp>,
    var listeners: onItemClicklisteners
) : RecyclerView.Adapter<KnowYourRightsAdapter.myViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KnowYourRightsAdapter.myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_know_right, parent, false)
        )
    }

    override fun onBindViewHolder(holder: KnowYourRightsAdapter.myViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
//        return 10
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var cvRowSupportGroup = view?.findViewById<MaterialCardView>(R.id.cvRowSupportGroup)
        var txtText1 = view?.findViewById<TextView>(R.id.txtText1)
        var txtText2 = view?.findViewById<TextView>(R.id.txtText2)
        fun bindItem(position: Int) {
            txtText1!!.text = arrayList[position].title
            txtText2!!.text = arrayList[position].code
            cvRowSupportGroup?.setOnClickListener {
                listeners.onItemClick(position)
            }
        }
    }

    interface onItemClicklisteners {
        fun onItemClick(position: Int)
    }
}