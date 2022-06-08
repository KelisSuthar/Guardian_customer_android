package com.app.guardian.ui.chatting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.Chat.ChatListResp

class ChatMessageAdapter(var context: Context, var array: ArrayList<ChatListResp>) :
    RecyclerView.Adapter<ChatMessageAdapter.myViewHolder>() {
    var isShow = true
    var SENDER = 0
    var RECIEVER = 1
    var HEADER = 2
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatMessageAdapter.myViewHolder {


        return when (viewType) {
            SENDER -> {
                myViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_layout_right, parent, false))
            }
            RECIEVER -> {
                myViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_layout_left, parent, false))
            }
            else -> {
                myViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_layout_header, parent, false))
            }
        }



    }

    override fun onBindViewHolder(holder: ChatMessageAdapter.myViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val txtRecieveMsg = view!!.findViewById<AppCompatTextView>(R.id.txtRecieveMsg)
        val txtSendMsg = view!!.findViewById<AppCompatTextView>(R.id.txtSendMsg)
        val headderTime= view!!.findViewById<AppCompatTextView>(R.id.appCompatTextView)

        fun bindItem(position: Int) {
            if(!array[position].message.isNullOrEmpty()){
                txtRecieveMsg.text = array[position].message
            }
            if(!array[position].message.isNullOrEmpty()){
                txtSendMsg.text = array[position].message
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (array[position].from_id == (SharedPreferenceManager.getUser()!!.user.id)) {
            SENDER
        } else {
            RECIEVER
        }
    }
}