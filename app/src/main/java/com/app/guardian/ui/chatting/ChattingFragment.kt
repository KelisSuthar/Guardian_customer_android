package com.app.guardian.ui.chatting

import android.os.Handler
import android.view.*
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.changeToDay
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.ReusedMethod.Companion.getCurrentDay
import com.app.guardian.common.extentions.changeDateFormat
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentChattingBinding
import com.app.guardian.model.Chat.ChatListResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.chatting.adapter.ChatMessageAdapter
import com.app.guardian.utils.Config
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class ChattingFragment(
    var selectUserId: Int? = 0,
    var selectUserFullName: String? = "",
    var profilePicUrl: String? = "",
    var to_role: String? = ""
) : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentChattingBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    var chatMessageAdapter: ChatMessageAdapter? = null
    var chatArray = ArrayList<ChatListResp>()
    var hasMap = HashMap<String, ArrayList<ChatListResp>>()
    val timer = Timer()
    val handler = Handler()
    override fun getInflateResource(): Int {
        return R.layout.fragment_chatting
    }

    override fun initView() {
        mBinding = getBinding()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        mBinding.txtChatUserName.text = selectUserFullName
        Glide.with(requireActivity())
            .load(profilePicUrl)
            .placeholder(R.drawable.profile)
            .into(mBinding.imgChatUserProfilePic)

        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.lawyer_list),
            false,
            false
        )

        mBinding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
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
        chatMessageAdapter = ChatMessageAdapter(requireActivity(), chatArray)
        mBinding.rvChat.adapter = chatMessageAdapter
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
                            if (!data.isNullOrEmpty()) {

//                                getHeadderTime(data)
                                chatArray.addAll(data)
                                setData(data)
                                chatMessageAdapter!!.notifyDataSetChanged()
//                                for (i in data.indices) {
//                                    if (changeDateFormat(
//                                            "yyyy-MM-dd HH:mm:ss",
//                                            "yyyy-MM-dd",
//                                            data[i].message_time!!
//                                        ) == changeDateFormat(
//                                            "yyyy-MM-dd HH:mm:ss",
//                                            "yyyy-MM-dd",
//                                            ReusedMethod.getCurrentDate()
//                                        )
//                                    ) {
//                                        chatArray[i].header_time = "Today"
//                                        chatArray[i].is_header_show = true
//                                    }
//                                }


                            } else {
                                stopTimers()
                                mBinding.noInternetChat.llNointernet.gone()
                                mBinding.rvChat.gone()
                            }
                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
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
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
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

    private fun setData(data: MutableList<ChatListResp>) {
        var date = ""
        for (i in data.indices) {
            if (changeDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd",
                    data[i].message_time!!
                ) == changeDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd",
                    ReusedMethod.getCurrentDate()
                )
            ) {
                chatArray.clear()
                chatArray.add(data[i])
                hasMap[AppConstants.EXTRA_TODAY] = chatArray


            } else if ((getCurrentDay().toInt() - 1) == (changeToDay(data[i].message_time!!).toInt())) {
                chatArray.clear()
                chatArray.add(data[i])
                hasMap[AppConstants.EXTRA_TODAY] = chatArray

            } else if ((getCurrentDay()
                    .toInt() - 1) == 0 && (changeToDay(
                    data[i].message_time!!
                ) == "31") || (changeToDay(
                    data[i].message_time!!
                ) == "30")
                || (changeToDay(
                    data[i].message_time!!
                ) == "28")
                || (changeToDay(
                    data[i].message_time!!
                ) == "29")
            ) {
                chatArray.clear()
                chatArray.add(data[i])
                hasMap[AppConstants.EXTRA_TODAY] = chatArray

            }else{
                chatArray.clear()
                chatArray.add(data[i])
                hasMap[AppConstants.EXTRA_TODAY] = chatArray
            }
            chatMessageAdapter!!.notifyDataSetChanged()

        }
    }


    private fun getHeadderTime(data: MutableList<ChatListResp>) {
        for (i in data.size - 1 downTo 0) {
            if (changeDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd",
                    data[i].message_time!!
                ) == changeDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd",
                    ReusedMethod.getCurrentDate()
                )
            ) {
                chatArray[i].header_time = "Today"
                chatArray[i].is_header_show = true
                chatMessageAdapter?.notifyDataSetChanged()
                break

            } else if ((getCurrentDay().toInt() - 1) == (changeToDay(data[i].message_time!!).toInt())) {
                chatArray[i].header_time = "Yesterday"
                chatArray[i].is_header_show = true
                chatMessageAdapter?.notifyDataSetChanged()
                break
            } else if ((getCurrentDay()
                    .toInt() - 1) == 0 && (changeToDay(
                    data[i].message_time!!
                ) == "31") || (changeToDay(
                    data[i].message_time!!
                ) == "30")
                || (changeToDay(
                    data[i].message_time!!
                ) == "28")
                || (changeToDay(
                    data[i].message_time!!
                ) == "29")
            ) {
                chatArray[i].header_time = "Yesterday"
                chatArray[i].is_header_show = true
                chatMessageAdapter?.notifyDataSetChanged()
            }


        }

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
            mViewModel.sendChatMessage(
                true,
                context as BaseActivity,
                selectUserId.toString(),
                mBinding.txtMessage.text?.trim().toString(),
                ReusedMethod.getCurrentDate(),
                to_role.toString()
            )
        } else {
            mBinding.noInternetChat.llNointernet.visible()
            mBinding.rvChat.gone()
        }
    }


    private fun callChatListApi() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {

            timer.schedule(object : TimerTask() {

                override fun run() {
                    handler.post {
                        mViewModel.getChatData(
                            true,
                            requireActivity() as BaseActivity,
                            selectUserId.toString()
                        )
                    }
                }
            }, 0, 1000)


        } else {
            mBinding.noInternetChat.llNointernet.visible()
            mBinding.rvChat.gone()
        }
    }

    fun stopTimers() {
        timer.cancel()
        handler.removeCallbacksAndMessages(null);
    }

    fun resumeTimers() {
        timer.cancel()
        handler.removeCallbacksAndMessages(null);
    }

}