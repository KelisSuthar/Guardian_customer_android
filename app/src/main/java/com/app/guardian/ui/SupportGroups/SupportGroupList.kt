package com.app.guardian.ui.SupportGroups

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.changeDateFormat
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentSupportGroupListBinding
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.SupportGroup.MultipleCallResp
import com.app.guardian.model.SupportGroup.SupportGroupResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.SupportGroups.adapter.SupportGroupListAdapter
import com.app.guardian.ui.virtualWitness.VirtualWitnessFragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class SupportGroupList(
    val header: String?,
    val isMultipleCall: Boolean? = false,
    val schedule_call: Boolean? = false
) : BaseFragment(),
    View.OnClickListener {
    lateinit var mBinding: FragmentSupportGroupListBinding
    private val mViewModel: UserViewModel by viewModel()
    private val commonScreensViewModel: CommonScreensViewModel by viewModel()
    var multipleCalls = ArrayList<MultipleCallResp>()
    var support_id = ""
    var supportGroupListAdapter: SupportGroupListAdapter? = null
    var array = ArrayList<SupportGroupResp>()

    override fun getInflateResource(): Int {
        return R.layout.fragment_support_group_list
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        if (header != null) {
            (activity as HomeActivity).headerTextVisible(
                header!!,
                true,
                true
            )
        } else {
            (activity as HomeActivity).headerTextVisible(
                resources.getString(R.string.support_groups),
                true,
                true
            )
        }

    }

    override fun onResume() {
        super.onResume()
        mBinding.rvSupportGroupList.visible()
        mBinding.noDataSupportGroup.gone()
        mBinding.noInternetSupportGroup.llNointernet.gone()
        multipleCalls.clear()
        callAPI()
        setAdapter()
    }

    private fun setAdapter() {
        mBinding.rvSupportGroupList.adapter = null
        supportGroupListAdapter = SupportGroupListAdapter(
            requireActivity(),
            array,
            object : SupportGroupListAdapter.onItemClicklisteners {
                override fun onClick(position: Int) {
                    support_id = array[position].id.toString()
                    if (isMultipleCall == true) {
                        callScedualDialog()
                    } else {
                        ReplaceFragment.replaceFragment(
                            requireActivity(),
                            VirtualWitnessFragment(array[position]),
                            true,
                            SupportGroupList::class.java.name,
                            SupportGroupList::class.java.name
                        );
                        SharedPreferenceManager.putInt(
                            AppConstants.EXTRA_SH_SUPPORT_GROUP_LIST,
                            array[position].id!!
                        )
                    }
                }

            })
        mBinding.rvSupportGroupList.adapter = supportGroupListAdapter
    }

    private fun callAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.getSupportGroup(true, context as BaseActivity)
        } else {
            mBinding.rvSupportGroupList.gone()
            mBinding.noInternetSupportGroup.llNointernet.visible()
            mBinding.noDataSupportGroup.gone()
        }
    }

    private fun callVirtualWitnessReqAPI(is_immediate_joining: Int? = 0, schedule_time: String) {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            commonScreensViewModel.SendRequestVirtualWitness(
                true, context as BaseActivity, support_id,
                is_immediate_joining!!, schedule_time
            )
        } else {
            ReusedMethod.displayMessage(
                requireActivity(),
                resources.getString(R.string.text_error_network)
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun postInit() {
        mViewModel.getSupportGroupResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            array.clear()
                            array.addAll(data)
                            supportGroupListAdapter?.notifyDataSetChanged()

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
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {
                                        ReusedMethod.displayMessage(requireActivity(), it)
                                        (activity as HomeActivity).unAuthorizedNavigation()
                                    } else {
                                        ReusedMethod.displayMessage(context as Activity, it)
                                    }
                                }
                    }
                }
            }
        }
        commonScreensViewModel.getSendRequestVirtualWitnessResp().observe(this) { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            if (schedule_call == false) {
                                displayMultipleCallConfirmationDialog(requireContext())
                            } else {
                                requireActivity().onBackPressed()
                            }

                            multipleCalls.add(
                                MultipleCallResp(
                                    data.schedule_datetime,
                                    data.support_group_id
                                )
                            )
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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

    override fun handleListener() {
        mBinding.noInternetSupportGroup.btnTryAgain.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }

    fun callScedualDialog() {
        val dialog = ReusedMethod.setUpDialog(
            requireContext(),
            R.layout.virtual_witness_request_dialog,
            true
        )
        val cvScheduleDate: MaterialCardView = dialog.findViewById(R.id.cvScheduleDate)
        val cvScheduleTime: MaterialCardView = dialog.findViewById(R.id.cvScheduleTime)
        val txtDate: TextView = dialog.findViewById(R.id.txtDate)
        val txtTime: TextView = dialog.findViewById(R.id.txtTime)
        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)
        val btnImmediateJoin: Button = dialog.findViewById(R.id.btnImmediateJoin)
        val btnRequestSend: Button = dialog.findViewById(R.id.btnRequestSend)
        txtDate.text = SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().time)
        txtTime.text = SimpleDateFormat("hh:mm a").format(Calendar.getInstance().time)
        cvScheduleDate.setOnClickListener {
            ReusedMethod.selectDate(requireActivity(), txtDate)
        }

        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        cvScheduleTime.setOnClickListener {
            ReusedMethod.selectTime(requireActivity(), txtTime)
        }
        btnImmediateJoin.setOnClickListener {
            dialog.dismiss()
            callVirtualWitnessReqAPI(1, ReusedMethod.getCurrentDate())
//            ReusedMethod.displayMessage(requireActivity(), resources.getString(R.string.come_soon))

        }
        btnRequestSend.setOnClickListener {
            dialog.dismiss()
            callConformationDialog(
                requireActivity(),
                txtDate.text.toString(),
                txtTime.text.toString(),
                resources.getString(R.string.virtual_witness)
            )

        }
        dialog.show()
    }

    fun callConformationDialog(context: Context, date: String, time: String, headder: String?) {
        val dialog = ReusedMethod.setUpDialog(
            requireContext(),
            R.layout.virtual_witness_request_confirmation_dialog,
            true
        )
        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)
        val txtDate: TextView = dialog.findViewById(R.id.txtDate)
        val txtTime: TextView = dialog.findViewById(R.id.txtTime)
        val txtDesc: TextView = dialog.findViewById(R.id.txtDesc)
        val txtTitle: TextView = dialog.findViewById(R.id.txtTitle)
        val sub: AppCompatButton = dialog.findViewById(R.id.btnRequestSend)

        txtDate.text = date
        txtTime.text = time
        txtTitle.text = headder
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        val time = changeDateFormat(
            "dd-MM-yyyy hh:mm a",
            "yyyy-MM-dd HH:mm:ss",
            (txtDate.text.toString() + " " + txtTime.text.toString())
        )
        sub.setOnClickListener {
            callVirtualWitnessReqAPI(schedule_time = time)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun displayMultipleCallConfirmationDialog(context: Context) {
        val dialog = ReusedMethod.setUpDialog(requireContext(), R.layout.dialog_layout, false)
        val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
        val HEADDER = dialog.findViewById<MaterialTextView>(R.id.txtAppname)
        val TITLE = dialog.findViewById<TextView>(R.id.tvTitle)
        val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
        val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)
        TITLE.gone()
        MESSAGE.gone()
        HEADDER.text = resources.getString(R.string.want_to_add_call)
        CANCEL.setOnClickListener {

            val gson = Gson()
            val json = gson.toJson(multipleCalls)
            SharedPreferenceManager.putString(
                AppConstants.MULTIPLE_CALLS,
                json
            )
            dialog.dismiss()
        }
        OK.setOnClickListener {
            dialog.dismiss()
            callScedualDialog()
        }
        dialog.show()
    }

//    companion object {
//
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            SupportGroupList().apply {
//
//            }
//    }
}