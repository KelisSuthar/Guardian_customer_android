package com.app.guardian.ui.SubscriptionPlan.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R

class SubscriptionPlanAdapter(
    var context: Activity,
    var arrayList: ArrayList<String>,
    var listeners: onItemClicklisteners
) :
    RecyclerView.Adapter<SubscriptionPlanAdapter.myViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubscriptionPlanAdapter.myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.subscription_list_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SubscriptionPlanAdapter.myViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
//        return arrayList.size
        return 3
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var btn = view?.findViewById<Button>(R.id.btnSubscription)
        fun bindItem(position: Int) {
            btn?.setOnClickListener {
                listeners.onSubclick(position)
            }
        }
    }


    interface onItemClicklisteners {
        fun onSubclick(position: Int)
    }
}