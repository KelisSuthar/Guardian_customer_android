package com.app.guardian.ui.chatting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.Chat.ChatListResp

class ChatMessageAdapter(var context: Context, var array: ArrayList<ChatListResp>) :
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
            txtRecieveMsg.text = array[position].message
            txtSendMsg.text = array[position].message

            if (array[position].from_id == (SharedPreferenceManager.getUser()?.user?.id ?: 0)) {
                txtSendMsg.visible()
                txtRecieveMsg.gone()
            } else {
                txtSendMsg.visible()
                txtRecieveMsg.gone()
            }
        }

    }
}