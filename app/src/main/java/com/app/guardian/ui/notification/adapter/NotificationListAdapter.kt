package com.app.guardian.ui.notification.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
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
        return  arrayList.size
//        return  3
    }

    inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var notifactionTxt = view.findViewById<AppCompatTextView>(R.id.txt)
        var notifactionTime = view.findViewById<AppCompatTextView>(R.id.txtTime)
        var imgDelete = view.findViewById<ImageView>(R.id.imgDelete)


        fun bindItem(position: Int){
            val array = arrayList[position]


            imgDelete.setOnClickListener {
                listeners.onDelectclick(array.id)
            }

            notifactionTxt.text = array.title
            notifactionTime.text = array.created_at
        }
    }

    interface onItemClicklisteners {
        fun onDelectclick(selectedNotificationId: Int?)
    }
}