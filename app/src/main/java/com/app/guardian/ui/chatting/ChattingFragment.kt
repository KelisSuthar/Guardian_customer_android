package com.app.guardian.ui.chatting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.ReusedMethod.Companion.getCurrentDate
import com.app.guardian.common.extentions.changeDateFormat
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentChattingBinding
import com.app.guardian.model.AskModeQResp.ChatDetail
import com.app.guardian.model.AskModeQResp.ChatListResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.chatting.adapter.ChatMessageAdapter
import com.app.guardian.utils.Config
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class ChattingFragment(
    var selectUserId: Int? = 0,
    var isNotification : Boolean?=false
) : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentChattingBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    var to_role = ""
    var chatMessageAdapter: ChatMessageAdapter? = null

    //    var chatMessageAdapter: ChatConversationAdapter? = null
    var chatArray = ArrayList<ChatDetail>()
    var hasMap = HashMap<String, ArrayList<ChatListResp>>()
    var handler = Handler()
    var runnable: Runnable? = null


    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_SENDER = 1
        const val TYPE_RECEIVER = 2
        const val TYPE_SENDER_IMAGE = 3
        const val TYPE_RECEIVER_IMAGE = 4
    }

    override fun getInflateResource(): Int {
        return R.layout.fragment_chatting
    }

    override fun initView() {
        mBinding = getBinding()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.lawyer_list),
            false,
            false
        )

        mBinding.ivBack.setOnClickListener {
            if(isNotification!!){

                startActivity(
                    Intent(
                        requireActivity(),

                        HomeActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                )
            }
            else{
                requireActivity().onBackPressed()

            }
        }

        mBinding.txtMessage.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                CallSendMessageAPI()
                return@OnEditorActionListener true
            }
            false
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun lastSeenChecker(lastSeen: String?) {
        mBinding.txtLastSeen.visible()
        Log.i("THIS_APP", "LAST_SEEN_DATE: " + lastSeen.toString())

        if (lastSeen != "" && lastSeen != "null" && lastSeen != null) {
            var days = 0
            var hrs = 0
            var min = 0

            val date2: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getCurrentDate())
            val date1: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastSeen.toString())
            val difference: Long = date2.time - date1.time

            days = ((difference / (1000 * 60 * 60 * 24)).toInt());
            hrs = (((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60)).toInt());
            min =
                ((difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hrs)) / (1000 * 60)).toInt();

            if (days == 0) {

                if (hrs == 0) {
                    if (min == 0) {
                        mBinding.txtLastSeen.text = "Active a Min Ago"
                    } else {
                        mBinding.txtLastSeen.text = "Active $min Min Ago"
                    }

                } else if (hrs >= 1) {
                    mBinding.txtLastSeen.text = "Active $hrs Hrs Ago"
                }
            } else if (days == 1) {

                mBinding.txtLastSeen.text = "Active 1 Day Ago"

            } else if (days > 1) {

                mBinding.txtLastSeen.text = "Active " +
                        SimpleDateFormat("dd-MM-yyyy").format(date1)
                            .toString()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        setAdapter()
        callChatListApi()
        mBinding.noInternetChat.llNointernet.gone()
        mBinding.rvChat.visible()
    }


    private fun setAdapter() {
        mBinding.rvChat.adapter = null
        chatMessageAdapter =
            ChatMessageAdapter(requireActivity(), chatArray)
        mBinding.rvChat.adapter = chatMessageAdapter
    }

    fun chatRecyListDisplay(listmessages: ArrayList<Any>) {
        mBinding.rvChat.scrollToPosition(listmessages.size - 1)
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.btnSend.setOnClickListener(this)
        mBinding.appCompatImageView3.setOnClickListener(this)
        mBinding.noInternetChat.llNointernet.setOnClickListener(this)
    }

    override fun initObserver() {
//        CHAT LIST RESP
        mViewModel.getChatListResp().observe(this) { response ->
            response?.let { requestState ->

                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            chatArray.clear()

                            to_role = data.user_detail.user_role!!
                            mBinding.txtChatUserName.text = data.user_detail.full_name
                            Glide.with(requireActivity())
                                .load(data.user_detail.profile_avatar)
                                .placeholder(R.drawable.profile)
                                .into(mBinding.imgChatUserProfilePic)
                            if (data.user_detail.user_role != AppConstants.APP_ROLE_USER) {
                                if (data.user_detail.is_online == 1) {
                                    mBinding.imgIsOnline.visible()
                                } else {
                                    mBinding.imgIsOnline.gone()
                                }
                                if (!data.user_detail.last_seen.isNullOrEmpty() || data.user_detail.last_seen != null && data.user_detail.is_online == 0) {
                                    lastSeenChecker(data.user_detail.last_seen)
                                } else {
                                    mBinding.txtLastSeen.visible()
                                    mBinding.txtLastSeen.text = "Online"
                                }
                            } else {
                                mBinding.txtLastSeen.gone()
                            }
                            chatArray.addAll(data.chat_detail)
                            if (chatArray.isNotEmpty()) {
                                setData()
                                mBinding.rvChat.visible()
                                chatMessageAdapter!!.notifyDataSetChanged()
                            } else {
                                mBinding.noInternetChat.llNointernet.gone()
                                mBinding.rvChat.gone()
                            }
                        } else {
                            displayMessage(requireActivity(), it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            displayMessage(
                                requireActivity(),
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {}
                    }
                }
            }
        }
//SEND MESSAGE RESP
        mViewModel.getSendChatResp().observe(this) { response ->
            response?.let { requestState ->

                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            mBinding.txtMessage.setText("")
                        } else {
                            displayMessage(requireActivity(), it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            displayMessage(
                                requireActivity(),
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {}
                    }
                }
            }
        }

    }

    private fun setData() {
        var isToday = true
        var yesterday = true
        var other_day = true

        val fmt = SimpleDateFormat("dd MMM yyyy")
        val currentTime: Date = Calendar.getInstance().time
        val curant_date = fmt.format(currentTime)
        var other_date = curant_date
        val dateFormat = SimpleDateFormat("dd MMM yyyy")
        val mydate = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)
        val previous_date = dateFormat.format(mydate)

        Log.i("THIS_APP", "PREV DATE   $previous_date")
        Log.i("THIS_APP", "CURRENT DATE   $curant_date")
//        for (i in chatArray.indices) {
        for (i in chatArray.size - 1 downTo 0) {
            Log.i(
                "THIS_APP", "MESSAGE DATE   " + changeDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    "dd MMM yyyy",
                    chatArray[i].message_time.toString()
                )
            )
            val chat_date = changeDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                "dd MMM yyyy",
                chatArray[i].message_time.toString()
            )
            if (chat_date == curant_date
            ) {
                if (isToday) {
                    isToday = false
                    chatArray[i].is_header_show = true
                    chatArray[i].header_time = AppConstants.EXTRA_TODAY
                    continue

                }

            } else if (chat_date == previous_date
            ) {
                if (yesterday) {
                    yesterday = false
                    chatArray[i].is_header_show = true
                    chatArray[i].header_time = AppConstants.EXTRA_YESTERDAY
                    continue
                }


            } else {
                if (other_date == chat_date) {
                    other_day = false
                } else {
                    other_day = true
                    other_date = chat_date
                }
                if (other_day) {
                    other_day = false
                    chatArray[i].is_header_show = true
                    chatArray[i].header_time = chat_date
                }

            }
        }
        Log.i("THIS_APP_CHAT_ARRAY", chatArray.toString())
        chatMessageAdapter?.notifyDataSetChanged()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSend -> {
                CallSendMessageAPI()
            }
            R.id.appCompatImageView3 -> {
                displayMessage(requireActivity(), resources.getString(R.string.come_soon))
            }
        }
    }

    private fun CallSendMessageAPI() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            if (!TextUtils.isEmpty(mBinding.txtMessage.text?.trim().toString())) {
                mViewModel.sendChatMessage(
                    true,
                    context as BaseActivity,
                    selectUserId.toString(),
                    mBinding.txtMessage.text?.trim().toString(),
                    ReusedMethod.getCurrentDate(),
                    to_role.toString()
                )
            }
        } else {
            mBinding.noInternetChat.llNointernet.visible()
            mBinding.rvChat.gone()
        }
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            handler.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
            displayMessage(requireActivity(), e.toString())
        }

    }


    private fun callChatListApi() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            handler.postDelayed(Runnable {
                handler.postDelayed(runnable!!, 2000)
                mViewModel.getChatData(
                    true,
                    requireActivity() as BaseActivity,
                    selectUserId.toString()
                )
            }.also { runnable = it }, 0)


//            mViewModel.getChatData(
//                true,
//                requireActivity() as HomeActivity,
//                selectUserId.toString()
//            )
        } else {
            mBinding.noInternetChat.llNointernet.visible()
            mBinding.rvChat.gone()
        }

    }

    fun stopTimers() {
        handler.removeCallbacksAndMessages(runnable);


    }

    fun resumeTimers() {
        handler.removeCallbacksAndMessages(null);
    }

}