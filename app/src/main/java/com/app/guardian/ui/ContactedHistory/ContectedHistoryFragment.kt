package com.app.guardian.ui.ContactedHistory

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.changeDateFormat
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentContectedHistoryBinding
import com.app.guardian.model.ListFilter.FilterResp
import com.app.guardian.model.ListFilter.Specialization
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.model.specializationList.SpecializationListResp
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.ContactedHistory.adapter.ConnectedHistoryAdapter
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.LawyerProfile.LawyerProfileFragment
import com.app.guardian.ui.chatting.ChattingFragment
import com.app.guardian.ui.signup.adapter.SpecializationAdapter
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ContectedHistoryFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    var specialization = ""
    var selected_laywer_id = -1
    var isSearchVisisble = false

    lateinit var mBinding: FragmentContectedHistoryBinding
    var connectedHistoryAdapter: ConnectedHistoryAdapter? = null
    var array = ArrayList<ConnectedHistoryResp>()
    var type = ""
    override fun getInflateResource(): Int {
        return R.layout.fragment_contected_history
    }

    override fun initView() {
        mBinding = getBinding()


        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb1) {
                isSearchVisisble = false
                callApi("")
            } else {
                isSearchVisisble = false
                callApi("")
            }

        }
        mBinding.searchConnectedHistory.edtLoginEmail.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                isSearchVisisble = true
                callApi(
                    mBinding.searchConnectedHistory.edtLoginEmail.text.toString(),
                )
                return@OnEditorActionListener true
            }
            false
        })
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.contacted_history),
            true,
            false
        )

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun postInit() {

    }

    override fun onResume() {
        super.onResume()
        callApi("")

    }

    private fun setAdapter() {
        mBinding.rvContectedHistory.adapter = null
        connectedHistoryAdapter = ConnectedHistoryAdapter(
            requireActivity(),
            type,
            array,
            object : ConnectedHistoryAdapter.onItemClicklisteners {
                override fun onCallClick(position: Int) {
                    val i = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + "+" + array[position].dialing_code + " " + array[position].phone)
                    )
                    try {
                        context?.startActivity(i)
                    } catch (s: SecurityException) {
                        ReusedMethod.displayMessage(context as Activity, "An error occurred")
                    }
                }

                override fun onChatClick(position: Int?) {
//                    if (array[position!!].user_role == AppConstants.APP_ROLE_LAWYER) {
                    ReplaceFragment.replaceFragment(
                        requireActivity(),
                        ChattingFragment(
                            array[position!!].id,
                        ),
                        true,
                        ContectedHistoryFragment::class.java.name,
                        ContectedHistoryFragment::class.java.name
                    )
//                    }
                }

                override fun onNotesClick(position: Int?) {
                    ReusedMethod.displayMessage(
                        requireActivity(),
                        resources.getString(R.string.come_soon)
                    )
                }

                override fun onItemClick(position: Int?) {
                    when (type) {

                        AppConstants.APP_ROLE_LAWYER -> {
                            ReplaceFragment.replaceFragment(
                                requireActivity(),
                                LawyerProfileFragment.newInstance(array[position!!].id!!, false),
                                true,
                                LawyerListFragment::class.java.name,
                                LawyerListFragment::class.java.name
                            );
                        }

                    }
                }

                override fun onVideCallClick(position: Int) {
                    if (SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_USER) {
                        displayVideoCallDialog(array[position].id)
                    } else {
                        callVideoCallRequestAPI(
                            array[position].id!!,
                            AppConstants.APP_ROLE_LAWYER,
                            0,
                            ""
                        )
                    }

                }

            })
        mBinding.rvContectedHistory.adapter = connectedHistoryAdapter
    }

    fun displayVideoCallDialog(id: Int?) {
        selected_laywer_id = id!!
        val dialog = Dialog(
            requireContext(),
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_do_you_need_mediator)
        dialog.setCancelable(true)

        val YES = dialog.findViewById<MaterialTextView>(R.id.txtDialogYes)
        val NO = dialog.findViewById<MaterialTextView>(R.id.txtDialogNo)

        YES.setOnClickListener {
            dialog.dismiss()
//            callRequestrMediatorApi()
            callScedualDialog()

//            startActivity(Intent(context,CreateOrJoinActivity::class.java))
        }

        NO.setOnClickListener {
            if (SharedPreferenceManager.getString(
                    AppConstants.USER_ROLE,
                    ""
                ) == AppConstants.APP_ROLE_USER
            ) {
                callVideoCallRequestAPI(
                    selected_laywer_id,
                    AppConstants.APP_ROLE_LAWYER,
                    0,
                    "",
                )
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun callScedualDialog() {
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
        val current_date = SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().time)

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
            callRequestrMediatorApi(1, txtDate.text.toString() + " " + txtTime.text.toString())
        }
        btnRequestSend.setOnClickListener {

            if (current_date != txtDate.text.toString()) {
                dialog.dismiss()
                callRequestrMediatorApi(
                    0,
                    txtDate.text.toString() + " " + txtTime.text.toString()
                )
            } else {
                if (isValidTime(txtTime.text.toString())) {
                    dialog.dismiss()
                    callRequestrMediatorApi(
                        0,
                        txtDate.text.toString() + " " + txtTime.text.toString()
                    )
                } else {
                    ReusedMethod.displayMessage(
                        requireActivity(),
                        "Please select proper date and time"
                    )
                }
            }
        }

        dialog.show()
    }

    private fun isValidTime(time: String): Boolean {
        var days = 0
        var hrs = 0
        var min = 0
        val currentDate = ReusedMethod.getCurrentDate()
        val currentDateTime =
            changeDateFormat("yyyy-MM-dd HH:mm:ss", "hh:mm a", currentDate)

        val date2: Date =
            SimpleDateFormat("hh:mm a").parse("$time")
        val date1: Date =
            SimpleDateFormat("hh:mm a").parse(currentDateTime)
        val difference: Long = date2.time - date1.time

        days = ((difference / (1000 * 60 * 60 * 24)).toInt())
        hrs = (((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60)).toInt())
        min =
            ((difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hrs)) / (1000 * 60)).toInt();

        return hrs > 0 || min > 30
//        return hrs > 0 && min > 30
    }


    private fun callRequestrMediatorApi(is_imediate_join: Int, schedual_datetime: String) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            userViewModel.sendCallingReqtoMediator(
                true, requireActivity() as BaseActivity,
                is_imediate_join, schedual_datetime
            )
        } else {
            ReusedMethod.displayMessage(
                requireActivity(),
                resources.getString(R.string.text_error_network)
            )
        }
    }

    private fun callVideoCallRequestAPI(
        selected_laywer_id: Int,
        role: String,
        isImmediateJoining: Int,
        requestDatetime: String
    ) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.sendVideoCallReq(
                true,
                requireActivity() as BaseActivity,
                selected_laywer_id,
                role,
                isImmediateJoining,
                requestDatetime
            )
        }
    }


    private fun callApi(search: String) {
        mBinding.noDataConnectedHistory.gone()
        mBinding.noInternetConnectedhistory.llNointernet.gone()
        mBinding.cl1.visible()
        mBinding.searchConnectedHistory.lySearch.gone()
        mBinding.radioGroup.visible()
        mBinding.rvContectedHistory.gone()
        val getCurrentRole =
            SharedPreferenceManager.getString(AppConstants.USER_ROLE, AppConstants.USER_ROLE)
        if (ReusedMethod.isNetworkConnected(requireContext())) {


            when (getCurrentRole) {
                AppConstants.APP_ROLE_USER -> {
                    type = if (mBinding.rb1.isChecked) {

                        AppConstants.APP_ROLE_LAWYER
                    } else {

                        AppConstants.APP_ROLE_MEDIATOR
                    }
                    setAdapter()
                    mViewModel.getUserConnectedHistory(true, context as BaseActivity, search, type)
                }
                AppConstants.APP_ROLE_LAWYER -> {
                    type = if (mBinding.rb1.isChecked) {
                        mBinding.searchConnectedHistory.lySearchFilter.visible()
                        AppConstants.APP_ROLE_MEDIATOR
                    } else {
                        mBinding.searchConnectedHistory.lySearchFilter.gone()
                        AppConstants.APP_ROLE_USER
                    }
                    mBinding.rb1.text = AppConstants.MEDIATOR
                    mBinding.rb2.text = AppConstants.USER
                    setAdapter()
                    mViewModel.getLawyerConnectedHistory(
                        true,
                        context as BaseActivity,
                        search,
                        type
                    )
                }
                AppConstants.APP_ROLE_MEDIATOR -> {
                    mBinding.radioGroup.gone()
                    type = AppConstants.APP_ROLE_MEDIATOR
                    setAdapter()
                    mViewModel.getMediatorConnectedHistory(true, context as BaseActivity, search)
                }
            }

        } else {
            mBinding.noDataConnectedHistory.gone()
            mBinding.noInternetConnectedhistory.llNointernet.visible()
            mBinding.cl1.gone()
            mBinding.radioGroup.visible()
        }
    }

    private fun callFilterDataAPI() {

        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.getFilterData(true, context as BaseActivity)
        } else {
            mBinding.noDataConnectedHistory.gone()
            mBinding.noInternetConnectedhistory.llNointernet.visible()
            mBinding.cl1.gone()
            mBinding.radioGroup.visible()
        }
    }

    override fun handleListener() {

        mBinding.noInternetConnectedhistory.btnTryAgain.setOnClickListener(this)
        mBinding.searchConnectedHistory.llsearch.setOnClickListener(this)
        mBinding.searchConnectedHistory.lySearchFilter.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getConenctedHistoryResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {

                    it.data?.let { data ->
                        if (it.status) {
                            if (!data.isNullOrEmpty()) {
                                mBinding.searchConnectedHistory.lySearch.visible()
                                mBinding.rvContectedHistory.visible()
                                array.clear()
                                array.addAll(data)
                                connectedHistoryAdapter?.notifyDataSetChanged()
                                if (array.isNullOrEmpty()) {
                                    mBinding.noDataConnectedHistory.visible()
                                    mBinding.noInternetConnectedhistory.llNointernet.gone()
                                    if (isSearchVisisble) {
                                        mBinding.searchConnectedHistory.lySearch.visible()
                                    } else {
                                        mBinding.searchConnectedHistory.lySearch.gone()
                                    }
                                    mBinding.radioGroup.visible()
                                }
                            } else {
                                mBinding.noDataConnectedHistory.visible()
                                mBinding.noInternetConnectedhistory.llNointernet.gone()
                                if (isSearchVisisble) {
                                    mBinding.searchConnectedHistory.lySearch.visible()
                                } else {
                                    mBinding.searchConnectedHistory.lySearch.gone()
                                }
                                mBinding.radioGroup.visible()
                            }

                        } else {
                            mBinding.noDataConnectedHistory.visible()
                            mBinding.noInternetConnectedhistory.llNointernet.gone()
                            mBinding.cl1.gone()
                            mBinding.radioGroup.visible()
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->

                    if (errorObj.customMessage.equals("No record.")) {
                        mBinding.noDataConnectedHistory.visible()
                        mBinding.noInternetConnectedhistory.llNointernet.gone()
                        mBinding.cl1.gone()
                        mBinding.radioGroup.visible()
                    } else {
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
        }
        //GET FILTER RESP
        mViewModel.getFilterResp().observe(this) { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        showFilterDialog(data)

                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                context as Activity,
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
        //SEND MEDIATOR CALLING REEQUEST RESP
        userViewModel.getCallMediatorReqResp().observe(this) { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        if (SharedPreferenceManager.getString(
                                AppConstants.USER_ROLE,
                                ""
                            ) == AppConstants.APP_ROLE_USER
                        ) {
                            callVideoCallRequestAPI(
                                selected_laywer_id,
                                AppConstants.APP_ROLE_LAWYER,
                                data.is_immediate_joining,
                                data.request_datetime
                            )
                        }

                    }
                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                context as Activity,
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

        //SEND Video CallReq
        mViewModel.getSendVideoCallRequestResp().observe(this) { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                    }
                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                context as Activity,
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

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnTryAgain -> {
                onResume()
            }
            R.id.lySearchFilter -> {
                callFilterDataAPI()
            }
            R.id.llsearch -> {
                isSearchVisisble = true
                if (TextUtils.isEmpty(mBinding.searchConnectedHistory.edtLoginEmail.text.toString())) {
                    callApi(mBinding.searchConnectedHistory.edtLoginEmail.text.toString())
                }
            }
        }
    }


    //    private fun changeLayout() {
//        mBinding.rb2.isChecked = mBinding.rb1.isChecked
//    }
    private fun showFilterDialog(data: FilterResp) {

        val dialog = Dialog(
            requireActivity(),
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_filter)
        dialog.setCancelable(false)
        val chipGroup1: ChipGroup = dialog.findViewById(R.id.chip_group1)
        val scrollView: ScrollView = dialog.findViewById(R.id.sv1)
        val btnDone: Button = dialog.findViewById(R.id.btnDone)
        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)
        val txtYearsExpTitle: TextView = dialog.findViewById(R.id.txtYearsExpTitle)

        txtYearsExpTitle.gone()
        scrollView.gone()

        AddItemsInChipGroup(requireContext(), chipGroup1, data.specialization)

        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        btnDone.setOnClickListener {
            mBinding.rvContectedHistory.visible()
            mBinding.searchConnectedHistory.lySearch.visible()
            mBinding.searchConnectedHistory.lySearchFilter.visible()
            mBinding.noDataConnectedHistory.gone()
            mBinding.noInternetConnectedhistory.llNointernet.gone()
            dialog.dismiss()
            isSearchVisisble = true
            callApi(specialization)

        }
        dialog.show()
    }


    fun AddItemsInChipGroup(
        context: Context,
        chipGroup: ChipGroup,
        arrayList: List<Specialization>
    ) {
        for (i in arrayList.indices) {
            val entryChip2: Chip = getChip(arrayList[i].title, context)
            entryChip2.id = i
            chipGroup.addView(entryChip2)
        }
    }

    private fun getChip(text: String, context: Context): Chip {
        val chip = Chip(context)
        chip.setChipDrawable(ChipDrawable.createFromResource(context, R.xml.filter_chips))
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 50f,
            context.resources.displayMetrics
        ).toInt()
        chip.setChipBackgroundColorResource(R.color.chip_unselector)
        chip.typeface = resources.getFont(R.font.lora_regular)
        chip.setTextColor(ContextCompat.getColor(context, R.color.txt_dark))
        chip.isCloseIconVisible = false
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        chip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                specialization = text

                chip.setChipBackgroundColorResource(R.color.chip_selector)
                chip.setTextColor(ContextCompat.getColor(context, R.color.white))
                chip.isChecked = true

            } else {
                chip.setChipBackgroundColorResource(R.color.chip_unselector)
                chip.setTextColor(ContextCompat.getColor(context, R.color.txt_dark))
                chip.isChecked = false

            }
        }
        return chip
    }

}