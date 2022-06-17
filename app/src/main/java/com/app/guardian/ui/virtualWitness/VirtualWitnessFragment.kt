package com.app.guardian.ui.virtualWitness

import android.app.Dialog
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.callConformationDialog
import com.app.guardian.common.ReusedMethod.Companion.selectDate
import com.app.guardian.common.ReusedMethod.Companion.selectTime
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentVirtualWitnessBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.google.android.material.card.MaterialCardView
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [VirtualWitnessFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VirtualWitnessFragment(val headder: String? = "") : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentVirtualWitnessBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_virtual_witness
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        if (headder.isNullOrEmpty()) {
            (activity as HomeActivity).headerTextVisible(
                resources.getString(R.string.virtual_witness),
                true,
                true
            )
        } else {
            (activity as HomeActivity).headerTextVisible(
                headder!!,
                true,
                true
            )
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.noDataVWitness.gone()
        mBinding.noInternetVWitness.llNointernet.gone()
        mBinding.cl1.visible()
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.btnRequest.setOnClickListener(this)
        mBinding.noInternetVWitness.btnTryAgain.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRequest -> {
                callScedualDialog()
            }
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }

    private fun callScedualDialog() {
        val dialog = Dialog(
            requireActivity(),
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.virtual_witness_request_dialog)
        dialog.setCancelable(true)

        val cvScheduleDate: MaterialCardView = dialog.findViewById(R.id.cvScheduleDate)
        val cvScheduleTime: MaterialCardView = dialog.findViewById(R.id.cvScheduleTime)
        val txtDate: TextView = dialog.findViewById(R.id.txtDate)
        val txtTime: TextView = dialog.findViewById(R.id.txtTime)
        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)
        val btnImmediateJoin: Button = dialog.findViewById(R.id.btnImmediateJoin)
        val btnRequestSend: Button = dialog.findViewById(R.id.btnRequestSend)

        cvScheduleDate.setOnClickListener {
            selectDate(requireActivity(),txtDate)
        }

        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        cvScheduleTime.setOnClickListener {
            selectTime(requireActivity(), txtTime)
        }
        btnImmediateJoin.setOnClickListener {
            dialog.dismiss()
            ReusedMethod.displayMessage(requireActivity(), resources.getString(R.string.come_soon))
        }
        btnRequestSend.setOnClickListener {
            dialog.dismiss()
            if (headder.isNullOrEmpty()) {
                callConformationDialog(
                    requireActivity(),
                    txtDate.text.toString(),
                    txtTime.text.toString(),
                    resources.getString(R.string.virtual_witness)
                )
            } else {
                callConformationDialog(requireActivity(),txtDate.text.toString(), txtTime.text.toString(), headder)
            }

        }

        dialog.show()
    }



//
}