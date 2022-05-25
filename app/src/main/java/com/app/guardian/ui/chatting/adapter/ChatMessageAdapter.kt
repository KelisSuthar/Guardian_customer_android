package com.app.guardian.ui.chatting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible

class ChatMessageAdapter(var context: Context, var array: ArrayList<String>) :
    RecyclerView.Adapter<ChatMessageAdapter.myViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatMessageAdapter.myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatMessageAdapter.myViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var txtRecieveMsg: TextView = view!!.findViewById(R.id.txtRecieveMsg)
        var txtSendMsg: TextView = view!!.findViewById(R.id.txtSendMsg)

        fun bindItem(position: Int) {
            txtRecieveMsg.text = array[position]
            txtSendMsg.text = array[position]

            if (position % 2 == 0) {
                txtRecieveMsg.visible()
                txtSendMsg.gone()
            } else {
                txtRecieveMsg.gone()
                txtSendMsg.visible()
            }
        }

    }
}