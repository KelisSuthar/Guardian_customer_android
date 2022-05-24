package com.app.guardian.ui.User.LiveVirtualVitness

import android.view.View
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.databinding.FragmentLiveVirtualVitnessUserBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.Mediator.MediatorHome.MediatorHomeFragment


class LiveVirtualVitnessUserFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentLiveVirtualVitnessUserBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_live_virtual_vitness_user
    }

    override fun onResume() {
        super.onResume()
        changeLayout(0)
    }
    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.live_virtual_witness_mediator),
            true,
            true
        )
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvAccessYourRecording.setOnClickListener(this)
        mBinding.cvDrivingOffenceList.setOnClickListener(this)
        mBinding.cvDialLawyer.setOnClickListener(this)
        mBinding.rbDialLawyer.setOnClickListener(this)
        mBinding.rbDrivingOffenceList.setOnClickListener(this)
        mBinding.rbAccessYourRecording.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvAccessYourRecording -> {
                 mBinding.rlDialLawyer.performClick()
            }
            R.id.cvDrivingOffenceList -> {
                mBinding.rlDrivingOffenceList.performClick()
            }
            R.id.cvDialLawyer -> {
                mBinding.rlDialLawyer.performClick()
            }
            R.id.rbAccessYourRecording->{
                changeLayout(1)
            }
            R.id.rbDrivingOffenceList->{
                changeLayout(2)
            }
            R.id.rbDialLawyer->{
                changeLayout(3)
                ReplaceFragment.replaceFragment(requireActivity(),
                    LawyerListFragment(true),true,"", LiveVirtualVitnessUserFragment::class.java.name)

            }
        }
    }

    private fun changeLayout(i: Int) {
        when (i) {
            0 -> {
                mBinding.rlAccessYourRecording.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDrivingOffenceList.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDialLawyer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbAccessYourRecording.isChecked = false
                mBinding.rbDrivingOffenceList.isChecked = false
                mBinding.rbDialLawyer.isChecked = false

            }
            1 -> {
                mBinding.rlAccessYourRecording.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlDrivingOffenceList.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDialLawyer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )

                mBinding.rbAccessYourRecording.isChecked = true
                mBinding.rbDrivingOffenceList.isChecked = false
                mBinding.rbDialLawyer.isChecked = false
            }
            2 -> {
                    mBinding.rlAccessYourRecording.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.lightBlue_2
                        )
                    )
                            mBinding.rlDrivingOffenceList.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.blue
                            )
                            )
                            mBinding.rlDialLawyer.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.lightBlue_2
                            )
                            )

                            mBinding.rbAccessYourRecording.isChecked = false
                            mBinding.rbDrivingOffenceList.isChecked = true
                            mBinding.rbDialLawyer.isChecked = false
            }
            3->{
                mBinding.rlAccessYourRecording.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDrivingOffenceList.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDialLawyer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )

                mBinding.rbAccessYourRecording.isChecked = false
                mBinding.rbDrivingOffenceList.isChecked = false
                mBinding.rbDialLawyer.isChecked = true
            }

        }
    }


}