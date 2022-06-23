package com.app.guardian.ui.ContactedHistory

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
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
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentContectedHistoryBinding
import com.app.guardian.model.ListFilter.FilterResp
import com.app.guardian.model.ListFilter.Specialization
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.model.specializationList.SpecializationListResp
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.ContactedHistory.adapter.ConnectedHistoryAdapter
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.chatting.ChattingFragment
import com.app.guardian.ui.signup.adapter.SpecializationAdapter
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import org.koin.android.viewmodel.ext.android.viewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContectedHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContectedHistoryFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    private val commonViewModel: CommonScreensViewModel by viewModel()
    var specialization = ""


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
                callApi("")

            } else {
                callApi("")
            }

        }
        mBinding.searchConnectedHistory.edtLoginEmail.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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

                }

            })
        mBinding.rvContectedHistory.adapter = connectedHistoryAdapter
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
            commonViewModel.getFilterData(true, context as BaseActivity)
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
                                    mBinding.cl1.gone()
                                    mBinding.radioGroup.visible()
                                }
                            } else {
                                mBinding.noDataConnectedHistory.visible()
                                mBinding.noInternetConnectedhistory.llNointernet.gone()
                                mBinding.cl1.gone()
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