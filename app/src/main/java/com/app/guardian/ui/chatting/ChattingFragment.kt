package com.app.guardian.ui.chatting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentChattingBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.chatting.adapter.ChatMessageAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChattingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ChattingFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentChattingBinding
    var chatMessageAdapter: ChatMessageAdapter? = null
    var chatArray = ArrayList<String>()
    override fun getInflateResource(): Int {
        return R.layout.fragment_chatting
    }

    override fun initView() {
        mBinding = getBinding()
        chatArray.add("Lorem Ipsum is simply dummy the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s")
        chatArray.add("Lorem Ipsum is simply dummy ")
        chatArray.add("Lorem Ipsum is simply dummy the printing and typesetting industry.")
        chatArray.add("Lorem Ipsum is simply printing and typesetting")
        chatArray.add("Lorem Ipsum is simply printing and typesetting")
        chatArray.add("Lorem Ipsum is simply dummy ")
        chatArray.add("Lorem Ipsum is simply dummy the printing and typesetting industry.")
        chatArray.add("Lorem Ipsum is simply dummy the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s")
        chatMessageAdapter?.notifyDataSetChanged()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onResume() {
        super.onResume()
        setAdapter()
        callApi()
        mBinding.nodataChat.gone()
        mBinding.noInternetChat.llNointernet.gone()
        mBinding.rvChat.visible()
    }

    private fun callApi() {
//        if (ReusedMethod.isNetworkConnected(requireActivity())) {
//
//        } else {
//            mBinding.noInternetChat.llNointernet.visible()
//            mBinding.nodataChat.gone()
//            mBinding.rvChat.gone()
//        }
    }

    private fun setAdapter() {
        mBinding.rvChat.adapter = null
        chatMessageAdapter = ChatMessageAdapter(requireActivity(), chatArray)
        mBinding.rvChat.adapter = chatMessageAdapter
    }

    override fun postInit() {

    }

    override fun handleListener() {

    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
    }


}