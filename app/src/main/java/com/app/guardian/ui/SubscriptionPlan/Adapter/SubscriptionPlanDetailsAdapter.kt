package com.app.guardian.ui.SubscriptionPlan.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.extentions.gone
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp

class SubscriptionPlanDetailsAdapter(
    var context: Activity,
    var arrayList: ArrayList<String>,

    ) :
    RecyclerView.Adapter<SubscriptionPlanDetailsAdapter.myViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.subscription_plan_detail_list_layout, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return arrayList.size

    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var tv = view?.findViewById<AppCompatTextView>(R.id.txtData)
        fun bindItem(position: Int) {
            if (!arrayList[position].isNullOrEmpty()) {
                tv!!.text = arrayList[position]
            } else {
                tv?.gone()
            }

        }

    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.bindItem(position)
    }


}