package com.app.guardian.ui.signup.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.model.specializationList.SpecializationListResp

class SpecializationAdapter(
    var context: Activity,
    var arrayList: ArrayList<SpecializationListResp>,
    var selectedid: Int,
    var listeners: onItemClicklisteners
) : RecyclerView.Adapter<SpecializationAdapter.myViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpecializationAdapter.myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.specialization_list_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SpecializationAdapter.myViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size

    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        var rb = view?.findViewById<RadioButton>(R.id.rb)
        fun bindItem(position: Int) {
            rb!!.isChecked = selectedid == arrayList[position].id
            rb!!.text = arrayList[position].title
            rb!!.setOnClickListener {
                listeners.onItemClick(position)
            }
        }
    }

    interface onItemClicklisteners {
        fun onItemClick(position: Int)
    }


}