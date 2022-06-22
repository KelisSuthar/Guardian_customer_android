package com.app.guardian.ui.notification.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.extentions.changeDateFormat
import com.app.guardian.model.Notification.NotificationResp
import com.app.guardian.ui.Lawyer.adapter.LawyerListAdapter

class NotificationListAdapter(
    var context: Activity,
    var arrayList: ArrayList<NotificationResp>,
    var listeners: onItemClicklisteners
) : RecyclerView.Adapter<NotificationListAdapter.myViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_notification_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
//        return  3
    }

    inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var notifactionTxt = view.findViewById<AppCompatTextView>(R.id.txt)
        var notifactionTime = view.findViewById<AppCompatTextView>(R.id.txtTime)
        var imgDelete = view.findViewById<ImageView>(R.id.imgDelete)


        @SuppressLint("SetTextI18n")
        fun bindItem(position: Int) {
            val array = arrayList[position]


            imgDelete.setOnClickListener {
                listeners.onDelectclick(position)
            }

            notifactionTxt.text = array.title + "\n" +
                    array.message
            notifactionTime.text = changeDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "YYYY-MM-DD HH:mm a",
                array.created_at.toString()
            )

        }
    }

    interface onItemClicklisteners {
        fun onDelectclick(position: Int)
        fun onItemClick(position: Int)
    }
}