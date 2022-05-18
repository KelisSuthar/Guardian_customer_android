package com.app.guardian.ui.KnowRight

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.app.guardian.R
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentKnowRightBinding
import com.app.guardian.databinding.FragmentKnowYourBasicRightBinding
import com.app.guardian.databinding.FragmentLawyerHomeBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.KnowRight.Adapter.KnowYourRightsAdapter
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList


class KnowRightFragment : BaseFragment(),View.OnClickListener {
    lateinit var mBinding: FragmentKnowRightBinding
    var knowYourRightsAdapter: KnowYourRightsAdapter? = null
    var array = ArrayList<String>()
    override fun getInflateResource(): Int {
        return R.layout.fragment_know_right
    }

    override fun initView() {
        mBinding = getBinding()
        setAdapter()
    }

    private fun setAdapter() {
        mBinding.rvLawyerList.adapter = null
        knowYourRightsAdapter = KnowYourRightsAdapter(requireActivity(),array,object :KnowYourRightsAdapter.onItemClicklisteners{
            override fun onItemClick(position: Int) {
                showDialog()
            }
        })
        mBinding.rvLawyerList.adapter = knowYourRightsAdapter
    }

    private fun showDialog() {
        val dialog = Dialog(
            requireActivity(),
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.know_right_dialog)
        dialog.setCancelable(false)

        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val btnClose = dialog.findViewById<Button>(R.id.btnClose)
        val txtRightName = dialog.findViewById<Button>(R.id.txtRightName)
        val txtRightSections = dialog.findViewById<Button>(R.id.txtRightSections)
        val txtLcation = dialog.findViewById<Button>(R.id.txtLcation)
        val txtDesc = dialog.findViewById<Button>(R.id.txtDesc)
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }



        dialog.show()
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