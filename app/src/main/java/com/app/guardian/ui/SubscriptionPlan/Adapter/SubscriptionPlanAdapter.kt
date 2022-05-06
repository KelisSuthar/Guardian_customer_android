package com.app.guardian.ui.SubscriptionPlan.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.extentions.gone
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp

class SubscriptionPlanAdapter(
    var context: Activity,
    var arrayList: ArrayList<SubscriptionPlanResp>,
    var listeners: onItemClicklisteners
) :
    RecyclerView.Adapter<SubscriptionPlanAdapter.myViewHolder>() {

    var subscriptionPlanDetailsAdapter: SubscriptionPlanDetailsAdapter? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubscriptionPlanAdapter.myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.subscription_list_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SubscriptionPlanAdapter.myViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
//        return 3
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var btn = view?.findViewById<Button>(R.id.btnSubscription)
        var rv = view?.findViewById<RecyclerView>(R.id.recyclerView)
        var txtTitle = view?.findViewById<TextView>(R.id.txtTitle)
        var txtPlanType = view?.findViewById<TextView>(R.id.txtPlanType)
        var txtPlanPrice = view?.findViewById<TextView>(R.id.txtPlanPrice)
        var txtIntroOffer = view?.findViewById<TextView>(R.id.txtIntroOffer)
        var txtFeature = view?.findViewById<TextView>(R.id.txtFeature)
        fun bindItem(position: Int) {
            var array = arrayList[position]
            rv?.gone()

            btn?.setOnClickListener {
                listeners.onSubclick(position)
            }
            if(array.offer_detail == ""){
                txtIntroOffer?.gone()
            }
            txtTitle!!.text = array.plan_duration
            txtPlanType!!.text = array.plan_type
            txtPlanPrice!!.text = array.pricing
            txtIntroOffer!!.text = array.offer_detail
            txtFeature!!.text = array.features
            rv?.adapter = null
            subscriptionPlanDetailsAdapter = SubscriptionPlanDetailsAdapter(context, arrayList)
            rv?.adapter = subscriptionPlanDetailsAdapter
        }
    }


    interface onItemClicklisteners {
        fun onSubclick(position: Int)
    }
}