package com.app.guardian.ui.SubscriptionPlan.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R

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
        //        return arrayList.size
        return 3
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        fun bindItem(position: Int) {

        }

    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.bindItem(position)
    }


}