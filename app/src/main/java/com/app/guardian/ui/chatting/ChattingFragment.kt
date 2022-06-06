package com.app.guardian.ui.chatting

import android.os.Handler
import android.view.*
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
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
    var selectUserId: Int?=0,
    var selectUserFullName: String?="",
    var profilePicUrl: String?="",
    var to_role: String?=""
) : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentChattingBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    var chatMessageAdapter: ChatMessageAdapter? = null
    var chatArray = ArrayList<ChatListResp>()
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
        mBinding.nodataChat.gone()
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
                                chatArray.addAll(data)
                                chatMessageAdapter?.notifyDataSetChanged()
                            } else {
                                stopTimers()
                                mBinding.noInternetChat.llNointernet.gone()
                                mBinding.nodataChat.visible()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSend -> {
                CallSendMessageAPI()
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
            mBinding.nodataChat.gone()
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
            mBinding.nodataChat.gone()
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