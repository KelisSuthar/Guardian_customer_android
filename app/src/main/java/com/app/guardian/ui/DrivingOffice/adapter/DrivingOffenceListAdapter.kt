package com.app.guardian.ui.DrivingOffice.adapter

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.BaseRecyclerViewAdapter
import com.app.guardian.model.DrivingOffenceList.DrivingOffenceListResp
import com.app.guardian.ui.User.MyVideos.adapter.MyVideoListAdapter

class DrivingOffenceListAdapter(
    val context: Context,
    val listener: onItemClicklisteners
) :
    BaseRecyclerViewAdapter<DrivingOffenceListResp>(context) {
    interface onItemClicklisteners {
        fun onItemClick(position: Int?)
    }

    override fun getViewHolder(view: View): RecyclerView.ViewHolder {
        return CustomViewHolder(view)
    }

    override fun getView(): Int {
        return R.layout.row_support_group
    }

    override fun setData(
        holder: RecyclerView.ViewHolder,
        data: DrivingOffenceListResp,
        position: Int
    ) {
        val customViewHolder = holder as CustomViewHolder
        customViewHolder.bindData(data, position)
    }

    open inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtRowSupportGroup = view?.findViewById<AppCompatTextView>(R.id.txtRowSupportGroup)
        var rlRowSupportGroup = view?.findViewById<RelativeLayout>(R.id.rlRowSupportGroup)
        fun bindData(data: DrivingOffenceListResp, position: Int) {
            txtRowSupportGroup.text = data.title
            itemView.setOnClickListener { listener.onItemClick(position) }
        }

    }
}