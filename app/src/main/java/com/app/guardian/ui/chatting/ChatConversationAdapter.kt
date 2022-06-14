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
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.changeDateFormat
import com.app.guardian.model.Chat.ChatListResp
import com.kodmap.library.kmrecyclerviewstickyheader.KmStickyListener
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("NewApi")
class ChatConversationAdapter(
    var context: Context?,
    var listMessages: ArrayList<ChatListResp>,
    var fragment: ChattingFragment?,
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), KmStickyListener {
    private var listNewMessages: ArrayList<Any>? = null
    private var mIsMessageListLoading: Boolean = false

    private var listGroupImgUrls: HashMap<String, ArrayList<ChatListResp>>? = null

    init {

        listGroupImgUrls = HashMap()
        listNewMessages = ArrayList<Any>()


        try {

            val listKeyDateTime: ArrayList<String> = ArrayList()


            for (baseMessage in listMessages) {

                val createdAt = baseMessage.message_time

//                val  sdate: Date =Date(createdAt/10000L)
// api side getTime : 2022-06-13 16:03:11

                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                //val dateTime1  = formatter.parse(createdAt)
//                val  sdate: Date =Date(dateTime1/10000L)
               val date =  changeDateFormat("yyyy-MM-dd HH:mm:ss","dd MMM yyyy", createdAt!!)
                Log.e("ChatConversationAd ", "date formerter:From: $createdAt TO $date")
//                val dateTImeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
//                val dateTime = dateTImeFormat.format(dateTime1)

//                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
//                val date = dateFormat.format(dateTime1)

                if (!listNewMessages?.contains(date)!!) {
                    listNewMessages?.add(date)
                }

                if (baseMessage.message != null) {
                    listNewMessages?.add(baseMessage)
                }

            }

            Log.e("ChatConversationAd ", "data listGroupImgUrls" + listGroupImgUrls)
            Log.e("ChatConversationAd ", "data listNewMessages" + listNewMessages)


            if(fragment is ChattingFragment)
            {
                (fragment as ChattingFragment).chatRecyListDisplay(listNewMessages!!)
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    class MySenderHolder(rowView: View) : RecyclerView.ViewHolder(rowView) {

        val txtSendMsg = rowView!!.findViewById<AppCompatTextView>(R.id.txtSendMsg)

    }

    class MyReceiverHolder(rowView: View) : RecyclerView.ViewHolder(rowView) {
        val txtReciver = rowView!!.findViewById<AppCompatTextView>(R.id.txtRecieveMsg)
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

        when(holder.itemViewType){
            ChattingFragment.TYPE_HEADER->{

                val messageHolder = holder as? MyHeaderHolder
                if(message is String) {

                    val todayDate:Date=Date()

                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                    val today_date=dateFormat.format(todayDate)

                    val mydate = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)
                    val previous_date = dateFormat.format(mydate)

                    Log.e("date", "Previous date :" + previous_date)


                    if(today_date.equals(message)){
                        messageHolder?.txtHeader?.setText("Today")

                    }else{

                        // val previous_date = todayDate.date - 1
                        //   Log.e("date", "Previous date :" + previous_date)

                        if (message.equals(previous_date)) {
                            messageHolder?.txtHeader?.setText("Yesterday")
                        } else {
                            messageHolder?.txtHeader?.setText(message)
                        }

//                        messageHolder?.txtHeader?.setText(message)


                    }

                }
                else{

                    messageHolder?.txtHeader?.setText(message.toString())

                }

            }
            ChattingFragment.TYPE_SENDER->{



                if (message is ChatListResp){


                    val messageHolder = holder as? MySenderHolder




//                    messageHolder?.txtSenderMessage?.visibility = View.VISIBLE
//                    messageHolder?.txtSenderDate?.visibility = View.VISIBLE
//                    messageHolder?.imgArrowSender?.visibility = View.VISIBLE


                    val txtmessageLenth = message.message!!.length
                    messageHolder?.txtSendMsg?.text = message.message

//                    val senderDate = message.timesteap
//                    val  sdate: Date=Date(senderDate/10000L)
//
//
//                    if (message.isMarkRead) {
//                        messageHolder!!.img_mark.visibility = View.VISIBLE
//                    } else {
//                        messageHolder!!.img_mark.visibility = View.GONE
//                    }


                    //  val date :String= DateFormat.format("dd-MM-yyyy hh:mm a", cal).toString()
                    val format = SimpleDateFormat("HH:mm", Locale.US)

                    //    val date :String= DateFormat.format("hh:mm a", cal).toString()
//                    val date = format.format(sdate)

                    //  Log.e("Sender date:","date :"+senderDate.toString())


                    //text date code update according to the message size
//                    if (txtmessageLenth < 6) {
//
//                        val txtparms: ViewGroup.LayoutParams? = messageHolder?.txtSendMsg?.layoutParams
//                        txtparms?.width = 180
//
//                        messageHolder?.txtSendMsg?.layoutParams = txtparms
//                        messageHolder?.txtSendMsg?.gravity = Gravity.CENTER
//                    } else {
//
//                        val txtparms: ViewGroup.LayoutParams? = messageHolder?.txtSendMsg?.layoutParams
//                        txtparms?.width = ViewGroup.LayoutParams.WRAP_CONTENT
//                        messageHolder?.txtSendMsg?.gravity = Gravity.CENTER_VERTICAL
//                        messageHolder?.txtSendMsg?.layoutParams = txtparms
//
//                    }

//                    messageHolder?.txtSenderDate?.text = date
                }

//                }
            }
            ChattingFragment.TYPE_RECEIVER->{

                if(message is ChatListResp){

                    //  val usermessage:UserMessage = message as UserMessage


                    val receiverHolder = holder as MyReceiverHolder
//                    receiverHolder.txtReceiverMessage.visibility = View.VISIBLE
//                    receiverHolder.txtReceiverDate.visibility = View.VISIBLE
//                    receiverHolder.imgArrowReceiver.visibility = View.VISIBLE
//
//
//                    val receiverDate = message.timesteap
//                    val  sdate: Date=Date(receiverDate/10000L)
//
//                    val format = SimpleDateFormat("HH:mm", Locale.US)
//                    //    val date :String= DateFormat.format("hh:mm a", cal).toString()
//
//                    val date = format.format(sdate)
//
////                Log.e("Sender date:","date :"+receiverDate.toString())


                    receiverHolder.txtReciver.text = message.message

                    val txtmessageLenth =message.message!!.length

                    //text date code update according to the message size
//                    if (txtmessageLenth < 6) {
//
//                        val txtparms: ViewGroup.LayoutParams? = receiverHolder.txtReciver.layoutParams
//
//                        txtparms?.width = 180
//
//                        receiverHolder.txtReciver.layoutParams = txtparms
//                        receiverHolder.txtReciver.gravity = Gravity.CENTER
//                    } else {
//                        val txtparms: ViewGroup.LayoutParams? = receiverHolder.txtReciver.layoutParams
//                        txtparms?.width = ViewGroup.LayoutParams.WRAP_CONTENT
//                        receiverHolder.txtReciver.gravity= Gravity.CENTER_VERTICAL
//
//                        receiverHolder.txtReciver.layoutParams = txtparms
//                    }

//                    receiverHolder.txtReceiverDate.text = date
                }



            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        //val item: ConversationDO = listMessages.get(position)
        val message = listNewMessages?.get(position)
        var retunViewType: Int ?= 20

        try{
            if(message is ChatListResp){

                if(message.message!=null)
                {
                    if(message.from_id!!.equals(SharedPreferenceManager.getUser()!!.user.id)){
                        retunViewType = ChattingFragment.TYPE_SENDER
                    }else{
                        retunViewType = ChattingFragment.TYPE_RECEIVER
                    }
                }
            }else{
                retunViewType = ChattingFragment.TYPE_HEADER
            }
        }catch (e:Exception){
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

            if(listNewMessages?.get(headerPosition!!) is String) {
                val strHeader = listNewMessages?.get(headerPosition!!) as String

                val todayDate: Date = Date()

                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                val today_date = dateFormat.format(todayDate)

                val mydate = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)
                val previous_date = dateFormat.format(mydate)

                //    Log.e("date", "Previous date :" + previous_date)

                if (today_date.equals(strHeader)) {
                    txtHeader.setText("Today")

                } else {

                    if (strHeader.equals(previous_date)) {
                        txtHeader.setText("Yesterday")
                    } else {
                        txtHeader.setText(strHeader)

                    }

//                    txtHeader.setText(strHeader)
                }

                //  txtHeader.setText(strHeader)


            }
            else{
//            Log.e("chat","Chat update Header "+)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun isHeader(itemPosition: Int?): Boolean {
        var isretrun : Boolean =true
        try {
            val message = listNewMessages?.get(itemPosition!!)
            isretrun= (!(message is ChatListResp))
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }


        return isretrun
    }


}