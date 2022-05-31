package com.app.guardian.ui.Notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.databinding.FragmentNotificationBinding
import com.app.guardian.shareddata.base.BaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : BaseFragment(),View.OnClickListener {
    lateinit var mBinding:FragmentNotificationBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_notification
    }

    override fun initView() {
        mBinding = getBinding()
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