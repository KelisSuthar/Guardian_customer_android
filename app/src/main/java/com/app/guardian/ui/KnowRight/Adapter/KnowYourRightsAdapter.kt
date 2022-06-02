package com.app.guardian.ui.KnowRight.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
        var rlRowKnowRight = view?.findViewById<RelativeLayout>(R.id.rlRowKnowRight)
        var txtText1 = view?.findViewById<TextView>(R.id.txtText1)
        var txtText2 = view?.findViewById<TextView>(R.id.txtText2)
        fun bindItem(position: Int) {
            txtText1!!.text = arrayList[position].title
            txtText2!!.text = arrayList[position].code

//            cvRowSupportGroup?.setOnTouchListener { v, event ->
//                if (event!!.actionMasked == MotionEvent.ACTION_DOWN) {
//                    rlRowKnowRight!!.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
//                    txtText1!!.setTextColor(ContextCompat.getColor(context,R.color.white))
//                    txtText2!!.setTextColor(ContextCompat.getColor(context,R.color.white))
//                    listeners.onItemClick(position)
//                }else{
//                    rlRowKnowRight!!.setBackgroundColor(ContextCompat.getColor(context, R.color.lightBlue_2))
//                    txtText1!!.setTextColor(ContextCompat.getColor(context,R.color.txt_dark))
//                    txtText2!!.setTextColor(ContextCompat.getColor(context,R.color.txt_dark))
//                }
//                true
//            }

            cvRowSupportGroup?.setOnClickListener {
                listeners.onItemClick(position)
            }
        }
    }

    interface onItemClicklisteners {
        fun onItemClick(position: Int)
    }
}