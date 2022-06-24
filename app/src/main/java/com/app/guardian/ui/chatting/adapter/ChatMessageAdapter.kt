package com.app.guardian.ui.chatting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.AskModeQResp.ChatDetail

class ChatMessageAdapter(var context: Context, var array: ArrayList<ChatDetail>) :
    RecyclerView.Adapter<ChatMessageAdapter.myViewHolder>() {
    var SENDER = 0
    var RECIEVER = 1
    var HEADER = 2


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatMessageAdapter.myViewHolder {

//        return when (viewType) {
//            SENDER -> {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.chat_row_layout, parent, false)
        )
//            }
//            RECIEVER -> {
//                myViewHolder(
//                    LayoutInflater.from(context).inflate(R.layout.chat_receiver_row, parent, false)
//                )
//            }
//            else -> {
//                myViewHolder(
//                    LayoutInflater.from(context).inflate(R.layout.chat_layout_header, parent, false)
//                )
//            }
//        }


    }

    override fun onBindViewHolder(holder: ChatMessageAdapter.myViewHolder, position: Int) {
        holder.bindItems(position)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class myViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val txtRecieveMsg = view!!.findViewById<AppCompatTextView>(R.id.txtRecieveMsg)
        val txtSendMsg = view!!.findViewById<AppCompatTextView>(R.id.txtSendMsg)
        val headderTime = view!!.findViewById<AppCompatTextView>(R.id.appCompatTextView)
        fun bindItems(position: Int) {
            val data = array[position]

            txtRecieveMsg.text = data.message
            txtSendMsg.text = data.message
            headderTime.text = data.header_time

            if (data.is_header_show == true) {
                headderTime.visible()
            } else {
                headderTime.gone()
            }
            if (array[position].from_id == (SharedPreferenceManager.getUser()!!.id)) {
                txtRecieveMsg.gone()
                txtSendMsg.visible()
            } else {
                txtRecieveMsg.visible()
                txtSendMsg.gone()
            }
        }
    }
//
//    override fun getHeaderPositionForItem(itemPosition: Int?): Int? {
//
////        return if (array[itemPosition!!].is_header_show == true) {
//          return  itemPosition
////        } else {
//            0
////        }
//
//    }
//
//    override fun getHeaderLayout(headerPosition: Int?): Int {
//        return R.layout.chat_layout_header
//    }
//
//    override fun bindHeaderData(header: View?, headerPosition: Int?) {
//        val txtHeader = header?.findViewById(R.id.appCompatTextView) as TextView
//        if(array[headerPosition!!].header_time.isNullOrEmpty())
//        {
//            txtHeader.gone()
//        }else{
//            txtHeader.visible()
//        }
//        txtHeader.text = array[headerPosition!!].header_time
//    }
//
//    override fun isHeader(itemPosition: Int?): Boolean {
//        return false
//    }



//    override fun getItemViewType(position: Int): Int {
//        var type = -1
//        if (array[position].from_id == (SharedPreferenceManager.getUser()!!.user.id)) {
//            type = SENDER
//        } else if (array[position].from_id != (SharedPreferenceManager.getUser()!!.user.id)) {
//            type = RECIEVER
//        }
//        if (array[position].is_header_show == true) {
//            type = HEADER
//        }
////        if(array[position].from_id == (SharedPreferenceManager.getUser()!!.user.id))
////        {
////           type = SENDER
////
////        }else{
////            type = RECIEVER
////        }
//        return type
//    }
}