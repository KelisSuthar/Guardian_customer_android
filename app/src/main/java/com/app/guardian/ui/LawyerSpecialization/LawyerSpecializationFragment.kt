package com.app.guardian.ui.LawyerSpecialization

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.changeDateFormat
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.inVisible
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerSpecializationBinding
import com.app.guardian.model.LawyerBySpecialization.LawyerBySpecializationResp
import com.app.guardian.model.ListFilter.FilterResp
import com.app.guardian.model.ListFilter.Specialization
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.LawyerProfile.LawyerProfileFragment
import com.app.guardian.ui.LawyerSpecialization.adapter.LawyerBySpecializationAdapter
import com.app.guardian.ui.chatting.ChattingFragment
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

class LawyerSpecializationFragment(isDialLawyer: Boolean? = false, specialization: String? = "") :
    BaseFragment(),
    View.OnClickListener {
    private val commonViewModel: CommonScreensViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private lateinit var mBinding: FragmentLawyerSpecializationBinding
    var lawyerBySpecializationAdapter: LawyerBySpecializationAdapter? = null
    var isDialLawyerOpen = isDialLawyer
    var specialization = ""
    var years_of_exp = ""
    var selected_laywer_id = -1
    var array = ArrayList<LawyerBySpecializationResp>()
    override fun getInflateResource(): Int {
        return R.layout.fragment_lawyer_specialization
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        if (isDialLawyerOpen == true) {
            (activity as HomeActivity).headerTextVisible(
                requireActivity().resources.getString(R.string.specialization_list),
                true,
                true
            )
        } else {
            (activity as HomeActivity).headerTextVisible(
                requireActivity().resources.getString(R.string.specialization_list),
                true,
                false
            )
        }
//        mBinding.lyLawyerSpListFilter.lySearchFilter.gone()
        mBinding.lyLawyerSpListFilter.edtLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    mBinding.lyLawyerSpListFilter.ivCancel.gone()
                } else {
                    mBinding.lyLawyerSpListFilter.ivCancel.visible()
                }
            }

            override fun afterTextChanged(p0: Editable?) {


            }

        })

        mBinding.lyLawyerSpListFilter.edtLoginEmail.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                callAPI(
//                    mBinding.lyLawyerSpListFilter.edtLoginEmail.text.toString(),
//                    years_of_exp, specialization!!
//                )
                val value = mBinding.lyLawyerSpListFilter.edtLoginEmail.text?.trim().toString()
                if (!TextUtils.isEmpty(value)) {
                    lawyerBySpecializationAdapter!!.filter.filter(value)
                }
                return@OnEditorActionListener true
            }
            false
        })

    }

    private fun setAdapter() {
        lawyerBySpecializationAdapter = LawyerBySpecializationAdapter(
            context as Activity,
            this,
            isDialLawyerOpen == true,
            array,
            object : LawyerBySpecializationAdapter.onItemClicklisteners {
                override fun onSubclick(selectedLawyerListId: Int?) {
                    ReplaceFragment.replaceFragment(
                        requireActivity(),
                        isDialLawyerOpen?.let {
                            LawyerProfileFragment.newInstance(
                                selectedLawyerListId!!,
                                it
                            )
                        },
                        true,
                        LawyerSpecializationFragment::class.java.name,
                        LawyerSpecializationFragment::class.java.name
                    );
                    //  callLawyerProfileDetails(array[position].id)
                }

            }
        )
        mBinding.rvLawyerSpList.adapter = lawyerBySpecializationAdapter
    }

    override fun onResume() {
        super.onResume()
        callAPI("", "", specialization.toString())
        setAdapter()
        mBinding.rvLawyerSpList.visible()
        mBinding.lyLawyerSpListFilter.lySearch.visible()
        mBinding.lyLawyerSpListFilter.lySearchFilter.visible()
        //  mBinding.lyLawyerListFilter.edtLoginEmail.gone()
        mBinding.noLawyerSp.gone()
        mBinding.noInternetLawyerSp.llNointernet.gone()
        mBinding.lyLawyerSpListFilter.edtLoginEmail.text?.clear()
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.noInternetLawyerSp.btnTryAgain.setOnClickListener(this)
        mBinding.lyLawyerSpListFilter.lySearchFilter.setOnClickListener(this)
        mBinding.lyLawyerSpListFilter.llsearch.setOnClickListener(this)
        mBinding.lyLawyerSpListFilter.ivCancel.setOnClickListener(this)
    }

    override fun initObserver() {
//        GET LIST RESP
        commonViewModel.getLawyerBySpecializationListResp().observe(this) { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->

                        array.clear()
                        array.addAll(data)
                        if (array.size != 0) {
                            lawyerBySpecializationAdapter?.notifyDataSetChanged()
                            mBinding.rvLawyerSpList.visible()
                            mBinding.noLawyerSp.gone()
                            mBinding.noInternetLawyerSp.llNointernet.gone()
                        } else {
                            mBinding.rvLawyerSpList.gone()
                            mBinding.noLawyerSp.visible()
                            mBinding.noInternetLawyerSp.llNointernet.gone()
                        }
                        lawyerBySpecializationAdapter?.notifyDataSetChanged()
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
                                        ReusedMethod.displayMessage(requireActivity(), it)
                                    }
                                }
                    }
                }
            }

        }
        //GET FILTER RESP
        commonViewModel.getFilterResp().observe(this) { response ->
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
                                        ReusedMethod.displayMessage(requireActivity(), it)
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
        commonViewModel.getSendVideoCallRequestResp().observe(this) { response ->
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
                if (!TextUtils.isEmpty(mBinding.lyLawyerSpListFilter.edtLoginEmail.text.toString())) {
//                    callAPI(
//                        mBinding.lyLawyerSpListFilter.edtLoginEmail.text.toString(), years_of_exp,
//                        specialization.toString()
//                    )
                    if (!TextUtils.isEmpty(
                            mBinding.lyLawyerSpListFilter.edtLoginEmail.text?.trim().toString()
                        )
                    ) {
                        lawyerBySpecializationAdapter!!.filter.filter(
                            mBinding.lyLawyerSpListFilter.edtLoginEmail.text?.trim().toString()
                        )
                    }
                }
            }
            R.id.ivCancel -> {
                lawyerBySpecializationAdapter!!.filter.filter("")
                mBinding.lyLawyerSpListFilter.edtLoginEmail.setText("")
            }
        }
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
                    ReusedMethod.getCurrentDate(),
                )
            }
            dialog.dismiss()
        }
        dialog.show()
    }


    fun callShowLawyerContactDetails(
        lawyerName: String?,
        lawyerEmail: String?,
        lawyerPhone: String?,
        lawyerProfilePicture: String?
    ) {
        lawyerName?.let {
            ReusedMethod.displayLawyerContactDetails(
                requireActivity(),
                it, lawyerEmail, lawyerPhone, lawyerProfilePicture
            )
        }
    }

    fun callChatPageOpe(
        selectUserId: Int,
        selectUserFullName: String,
        profilePicUrl: String,
        lastseen: String
    ) {
        Log.i("SEARCH_ISSUE", selectUserFullName)
        ReplaceFragment.replaceFragment(
            requireActivity(),
            ChattingFragment(
                selectUserId
            ),
            true,
            LawyerSpecializationFragment::class.java.name,
            LawyerSpecializationFragment::class.java.name
        );
    }

    private fun callAPI(search: String, years_of_exp: String, specialization: String) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            commonViewModel.getLawyerbySpecializationlist(
                true,
                context as BaseActivity,
                search,
                years_of_exp,
                specialization
            )
        } else {
            mBinding.rvLawyerSpList.gone()
            mBinding.noLawyerSp.gone()
            mBinding.noInternetLawyerSp.llNointernet.visible()
        }
    }

    private fun callFilterDataAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            commonViewModel.getFilterData(true, context as BaseActivity)
        } else {
            mBinding.rvLawyerSpList.gone()
            mBinding.noLawyerSp.gone()
            mBinding.noInternetLawyerSp.llNointernet.visible()
        }
    }

    private fun showFilterDialog(data: FilterResp) {
        val dialog = ReusedMethod.setUpDialog(requireContext(), R.layout.dialog_filter, false)
        val chipGroup1: ChipGroup = dialog.findViewById(R.id.chip_group1)
        val chipGroup2: ChipGroup = dialog.findViewById(R.id.chip_group2)
        val btnDone: Button = dialog.findViewById(R.id.btnDone)
        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)
        val txtClear1: AppCompatTextView = dialog.findViewById(R.id.txtClear1)
        val txtClear2: AppCompatTextView = dialog.findViewById(R.id.txtClear2)

        txtClear2.visible()
        txtClear1.visible()
        AddItemsInChipGroup(requireContext(), chipGroup1, data.specialization)


        val array = ArrayList<String>()
        array.add(data.age.`0-1`)
        array.add(data.age.`1-5`)
        array.add(data.age.`5-7`)
        array.add(data.age.`7-10`)
        array.add(data.age.`10-15`)
        array.add(data.age.`15-100`)
        for (i in array.indices) {
            val entryChip2: Chip = getChip(array[i], requireContext(), false)
            entryChip2.id = i
            chipGroup2.addView(entryChip2)
        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
//        if (specialization!!.isNotEmpty()) {
//            txtClear2.visible()
//        } else {
//            txtClear2.gone()
//        }
//        if (years_of_exp.isNotEmpty()) {
//            txtClear1.visible()
//        } else {
//            txtClear1.gone()
//        }
        btnDone.setOnClickListener {
            mBinding.rvLawyerSpList.visible()
            mBinding.lyLawyerSpListFilter.lySearch.visible()
            mBinding.lyLawyerSpListFilter.lySearchFilter.visible()
            //  mBinding.lyLawyerListFilter.edtLoginEmail.gone()
            mBinding.noLawyerSp.gone()
            mBinding.noInternetLawyerSp.llNointernet.gone()
            dialog.dismiss()
            if (years_of_exp == "Above 15") {
                callAPI("", "15-100", specialization.toString())

            } else {
                callAPI("", years_of_exp.replace(" to ", "-"), specialization.toString())


            }
        }
        txtClear1.setOnClickListener {
            years_of_exp = ""
            chipGroup2.removeAllViews()
            for (i in array.indices) {
                val entryChip2: Chip = getChip(array[i], requireContext(), false)
                entryChip2.id = i
                chipGroup2.addView(entryChip2)
            }
//            txtClear1.inVisible()
            if (years_of_exp == "Above 15") {
                callAPI("", "15-100", specialization.toString())
            } else {
                callAPI("", years_of_exp.replace(" to ", "-"), specialization.toString())
            }
        }
        txtClear2.setOnClickListener {

            specialization = ""
            chipGroup1.removeAllViews()
            AddItemsInChipGroup(requireContext(), chipGroup1, data.specialization)
//            txtClear2.inVisible()
            callAPI("", years_of_exp, specialization!!)
        }

        dialog.show()
    }

    fun AddItemsInChipGroup(
        context: Context,
        chipGroup: ChipGroup,
        arrayList: List<Specialization>
    ) {
        for (i in arrayList.indices) {
            val entryChip2: Chip = getChip(arrayList[i].title, context, true)
            entryChip2.id = i
            chipGroup.addView(entryChip2)
        }
    }

    private fun getChip(text: String, context: Context, b: Boolean): Chip {
        val chip = Chip(context)
        chip.setChipDrawable(ChipDrawable.createFromResource(context, R.xml.filter_chips))
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 50f,
            context.resources.displayMetrics
        ).toInt()
        if (specialization == text || years_of_exp == text) {
            chip.setChipBackgroundColorResource(R.color.chip_selector)
            chip.setTextColor(ContextCompat.getColor(context, R.color.white))
            chip.isChecked = true
        } else {
            chip.setChipBackgroundColorResource(R.color.chip_unselector)
            chip.setTextColor(ContextCompat.getColor(context, R.color.txt_dark))
            chip.isChecked = false
        }


        chip.typeface = resources.getFont(R.font.lora_regular)

        chip.isCloseIconVisible = false
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        chip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                if (b) {
                    specialization = text
                } else {
                    years_of_exp = text
                }
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
//            callRequestrMediatorApi(1, txtDate.text.toString() + " " + txtTime.text.toString())
            ReusedMethod.displayMessage(requireActivity(), resources.getString(R.string.come_soon))
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

    fun callVideoCallRequestAPI(
        selected_laywer_id: Int,
        role: String,
        isImmediateJoining: Int,
        requestDatetime: String
    ) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            commonViewModel.sendVideoCallReq(
                true,
                requireActivity() as BaseActivity,
                selected_laywer_id,
                role,
                isImmediateJoining,
                requestDatetime
            )
        }
    }
}