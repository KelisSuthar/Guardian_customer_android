package com.app.guardian.ui.LawyerSpecialization

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerSpecializationBinding
import com.app.guardian.model.LawyerBySpecialization.LawyerBySpecializationResp
import com.app.guardian.model.ListFilter.FilterResp
import com.app.guardian.model.ListFilter.Specialization
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.LawyerProfile.LawyerProfileFragment
import com.app.guardian.ui.LawyerSpecialization.adapter.LawyerBySpecializationAdapter
import com.app.guardian.ui.chatting.ChattingFragment
import com.app.guardian.utils.Config
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import org.koin.android.viewmodel.ext.android.viewModel

class LawyerSpecializationFragment(isDialLawyer: Boolean? = false, specialization: String? = "") :
    BaseFragment(),
    View.OnClickListener {
    private val commonViewModel: CommonScreensViewModel by viewModel()
    private lateinit var mBinding: FragmentLawyerSpecializationBinding
    var lawyerBySpecializationAdapter: LawyerBySpecializationAdapter? = null
    var isDialLawyerOpen = isDialLawyer
    var specialization = specialization
    var years_of_exp = ""
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

        mBinding.lyLawyerSpListFilter.edtLoginEmail.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                callAPI(
                    mBinding.lyLawyerSpListFilter.edtLoginEmail.text.toString(),
                    years_of_exp, specialization!!
                )
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
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.noInternetLawyerSp.btnTryAgain.setOnClickListener(this)
        mBinding.lyLawyerSpListFilter.lySearchFilter.setOnClickListener(this)
        mBinding.lyLawyerSpListFilter.llsearch.setOnClickListener(this)
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
                                ?.let { ReusedMethod.displayMessage(context as Activity, it) }
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
                                ?.let { ReusedMethod.displayMessage(context as Activity, it) }
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
                    callAPI(
                        mBinding.lyLawyerSpListFilter.edtLoginEmail.text.toString(), years_of_exp,
                        specialization.toString()
                    )
                }
            }
        }
    }

    fun displayVideoCallDialog() {
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
            ReusedMethod.displayMessage(
                context as Activity,
                (context as Activity).resources.getString(R.string.come_soon)
            )
        }

        NO.setOnClickListener {
            dialog.dismiss()
            ReusedMethod.displayMessage(
                context as Activity,
                (context as Activity).resources.getString(R.string.come_soon)
            )
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

        val dialog = Dialog(
            requireActivity(),
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_filter)
        dialog.setCancelable(false)
        val chipGroup1: ChipGroup = dialog.findViewById(R.id.chip_group1)
        val chipGroup2: ChipGroup = dialog.findViewById(R.id.chip_group2)
        val btnDone: Button = dialog.findViewById(R.id.btnDone)
        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)

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
        btnDone.setOnClickListener {
            dialog.dismiss()
            if (years_of_exp == "Above 15") {
                callAPI("", "15-100", specialization.toString())

            } else {
                callAPI("", years_of_exp.replace(" to ", "-"), specialization.toString())

            }
            specialization = ""
            years_of_exp = ""
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
        chip.setChipBackgroundColorResource(R.color.chip_unselector)
        chip.typeface = resources.getFont(R.font.lora_regular)
        chip.setTextColor(ContextCompat.getColor(context, R.color.txt_dark))
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
}