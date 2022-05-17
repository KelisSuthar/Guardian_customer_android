package com.app.guardian.ui.User.RightWitnessRequest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.databinding.FragmentKnowYourBasicRightBinding
import com.app.guardian.databinding.FragmentUserHomeBinding
import com.app.guardian.shareddata.base.BaseFragment


class KnowYourBasicRightFragment : BaseFragment(),View.OnClickListener {
    lateinit var mBinding: FragmentKnowYourBasicRightBinding

    override fun getInflateResource(): Int {
        return R.layout.fragment_know_your_basic_right
    }

    override fun initView() {
        mBinding = getBinding()
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvKnowBasicRight.setOnClickListener(this)
        mBinding.cvLiveVirtualWitness.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
       when(v?.id){
           R.id.cvKnowBasicRight->{

           }R.id.cvLiveVirtualWitness->{

           }
       }
    }
}