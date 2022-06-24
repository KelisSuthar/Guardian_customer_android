package com.app.guardian.ui.chatting

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.extentions.changeDateFormat
import com.app.guardian.model.AskModeQResp.ChatDetail
import com.app.guardian.model.AskModeQResp.ChatListResp
import com.kodmap.library.kmrecyclerviewstickyheader.KmStickyListener
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("NewApi")
class ChatConversationAdapter(
    var context: Context?,
    listMessages: ArrayList<ChatDetail>,
    var fragment: ChattingFragment?,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KmStickyListener {
    private var listNewMessages: ArrayList<Any>? = null


    init {
        listNewMessages = ArrayList<Any>()
        try {
            for (baseMessage in listMessages) {
                val createdAt = baseMessage.message_time
                val date = changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", createdAt!!)
                Log.e("ChatConversationAd ", "date formerter:From: $createdAt TO $date")

                if (!listNewMessages?.contains(date)!!) {
                    listNewMessages?.add(date)
                }

                if (baseMessage.message != null) {
                    listNewMessages?.add(baseMessage)
                }

            }

            Log.e("ChatConversationAd ", "data listNewMessages" + listNewMessages)


            if (fragment is ChattingFragment) {
                (fragment as ChattingFragment).chatRecyListDisplay(listNewMessages!!)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class MySenderHolder(rowView: View) : RecyclerView.ViewHolder(rowView) {

        val txtSendMsg = rowView.findViewById<AppCompatTextView>(R.id.txtSendMsg)

    }

    class MyReceiverHolder(rowView: View) : RecyclerView.ViewHolder(rowView) {
        val txtReciver = rowView.findViewById<AppCompatTextView>(R.id.txtRecieveMsg)
    }

    class MyHeaderHolder(rowView: View) : RecyclerView.ViewHolder(rowView) {
        val txtHeader = rowView.findViewById(R.id.txt_header_chat_header_row) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ChattingFragment.TYPE_HEADER -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.row_chat_header, parent, false)
                MyHeaderHolder(
                    view
                )
            }
            ChattingFragment.TYPE_SENDER -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.chat_sender_row, parent, false)
                MySenderHolder(
                    view
                )
            }
            ChattingFragment.TYPE_RECEIVER -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.chat_receiver_row, parent, false)
                MyReceiverHolder(
                    view
                )
            }
            else -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.chat_sender_row, parent, false)
                MySenderHolder(
                    view
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = listNewMessages?.get(position)

        when (holder.itemViewType) {
            ChattingFragment.TYPE_HEADER -> {

                val messageHolder = holder as? MyHeaderHolder
                if (message is String) {

                    val todayDate: Date = Date()

                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                    val today_date = dateFormat.format(todayDate)

                    val mydate = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)
                    val previous_date = dateFormat.format(mydate)

                    Log.e("date", "Previous date :" + previous_date)


                    if (today_date.equals(message)) {
                        messageHolder?.txtHeader?.text = "Today"

                    } else {

                        if (message.equals(previous_date)) {
                            messageHolder?.txtHeader?.text = "Yesterday"
                        } else {
                            messageHolder?.txtHeader?.text = message
                        }
                    }

                } else {

                    messageHolder?.txtHeader?.text = message.toString()

                }

            }
            ChattingFragment.TYPE_SENDER -> {

//
//                if (message is ChatListResp) {
//                    val messageHolder = holder as? MySenderHolder
//                    messageHolder?.txtSendMsg?.text = message.message
//                }
            }
            ChattingFragment.TYPE_RECEIVER -> {

//                if (message is ChatListResp) {
//                    val receiverHolder = holder as MyReceiverHolder
//                    receiverHolder.txtReciver.text = message.message
//                }


            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        //val item: ConversationDO = listMessages.get(position)
        val message = listNewMessages?.get(position)
        var retunViewType: Int? = 20

        try {
            if (message is ChatListResp) {

//                if (message.chat_detail.message != null) {
//                    retunViewType =
//                        if (message.from_id!! == SharedPreferenceManager.getUser()!!.user.id) {
//                            ChattingFragment.TYPE_SENDER
//                        } else {
//                            ChattingFragment.TYPE_RECEIVER
//                        }
//                }
            } else {
                retunViewType = ChattingFragment.TYPE_HEADER
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return retunViewType!!
    }

    override fun getItemCount(): Int {
        return listNewMessages?.size!!
    }

    override fun getHeaderPositionForItem(itemPos: Int): Int {
        var headerPosition: Int? = 0
        for (i in itemPos downTo 1) {
            if (isHeader(i)) {
                headerPosition = i
                return headerPosition
            }
        }
        return headerPosition!!
    }

    override fun getHeaderLayout(headerPosition: Int?): Int {
        return R.layout.row_chat_header
    }

    override fun bindHeaderData(header: View?, headerPosition: Int?) {
        try {
            val txtHeader = header?.findViewById(R.id.txt_header_chat_header_row) as TextView

            if (listNewMessages?.get(headerPosition!!) is String) {
                val strHeader = listNewMessages?.get(headerPosition!!) as String

                val todayDate = Date()

                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                val today_date = dateFormat.format(todayDate)

                val mydate = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)
                val previous_date = dateFormat.format(mydate)

                //    Log.e("date", "Previous date :" + previous_date)

                if (today_date.equals(strHeader)) {
                    txtHeader.text = "Today"

                } else {

                    if (strHeader == previous_date) {
                        txtHeader.text = "Yesterday"
                    } else {
                        txtHeader.text = strHeader

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun isHeader(itemPosition: Int?): Boolean {
        var isretrun = true
        try {
            val message = listNewMessages?.get(itemPosition!!)
            isretrun = (message !is ChatListResp)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


        return isretrun
    }


}