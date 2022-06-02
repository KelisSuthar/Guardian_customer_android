package com.app.guardian.ui.LawyerList


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.app.guardian.R
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerListBinding
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.model.ListFilter.FilterResp
import com.app.guardian.model.ListFilter.Specialization
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Lawyer.adapter.LawyerListAdapter
import com.app.guardian.ui.LawyerProfile.LawyerProfileFragment
import com.app.guardian.ui.chatting.ChattingFragment
import com.app.guardian.utils.Config
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import org.koin.android.viewmodel.ext.android.viewModel


class LawyerListFragment(isDialLawyer: Boolean) : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentLawyerListBinding
    private val mViewModel: UserViewModel by viewModel()
    private val commonViewModel: CommonScreensViewModel by viewModel()
    var lawyerListAdapter: LawyerListAdapter? = null
    var array = ArrayList<LawyerListResp>()
    var isDialLawyerOpen = isDialLawyer
    var specialization = ""
    var years_of_exp = ""
    private var rootView: View? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_lawyer_list
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(true)
        if (isDialLawyerOpen) {
            (activity as HomeActivity).headerTextVisible(
                requireActivity().resources.getString(R.string.dial_lawyer_list),
                true,
                true
            )
        } else {
            (activity as HomeActivity).headerTextVisible(
                requireActivity().resources.getString(R.string.lawyer_list),
                true,
                false
            )
        }
    }

    private fun setAdapter() {
        lawyerListAdapter = LawyerListAdapter(
            context as Activity,
            this,
            isDialLawyerOpen,
            array,
            object : LawyerListAdapter.onItemClicklisteners {
                override fun onSubclick(selectedLawyerListId: Int?) {
                    ReplaceFragment.replaceFragment(
                        requireActivity(),
                        LawyerProfileFragment.newInstance(selectedLawyerListId!!, isDialLawyerOpen),
                        true,
                        LawyerListFragment::class.java.name,
                        LawyerListFragment::class.java.name
                    );
                    //  callLawyerProfileDetails(array[position].id)
                }

            }
        )
        mBinding.rvLawyerList.adapter = lawyerListAdapter
    }

    override fun postInit() {
    }


    override fun initObserver() {
        mViewModel.getLawyerList().observe(this, Observer { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->

                        array.clear()
                        array.addAll(data)
                        if (array.size != 0) {
                            lawyerListAdapter?.notifyDataSetChanged()
                        } else {
                            mBinding.rvLawyerList.gone()
                            mBinding.noLawyer.visible()
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
                                ?.let { ReusedMethod.displayMessage(context as Activity, it) }
                    }
                }
            }

        })

        //GET FILTER RESP
        commonViewModel.getFilterResp().observe(this, Observer { response ->
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

        })
    }

    override fun onResume() {
        super.onResume()
        callAPI("", "", "")
        setAdapter()
        mBinding.rvLawyerList.visible()
        mBinding.lyLawyerListFilter.lySearch.visible()
        mBinding.lyLawyerListFilter.lySearchFilter.visible()
        //  mBinding.lyLawyerListFilter.edtLoginEmail.gone()
        mBinding.noLawyer.gone()
        mBinding.noInternetLawyer.llNointernet.gone()
    }


    fun callShowLawyerContactDetails(lawyerName : String?,lawyerEmail: String?,lawyerPhone : String?, lawyerProfilePicture : String?) {
        lawyerName?.let {
            ReusedMethod.displayLawyerContactDetails(requireActivity(),
                it, lawyerEmail,lawyerPhone,lawyerProfilePicture)
        }
    }

    fun callChatPageOpe(selectUserId: Int, selectUserFullName: String, profilePicUrl: String) {
        ReplaceFragment.replaceFragment(
            requireActivity(),
            ChattingFragment(selectUserId,selectUserFullName,profilePicUrl),
            true,
            LawyerListFragment::class.java.name,
            LawyerListFragment::class.java.name
        );
    }

    private fun callAPI(search: String, years_of_exp: String, specialization: String) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.lawyerList(
                true,
                context as BaseActivity,
                search,
                years_of_exp,
                specialization
            )
        } else {
            mBinding.rvLawyerList.gone()
            mBinding.noLawyer.gone()
            mBinding.noInternetLawyer.llNointernet.visible()
        }
    }

    private fun callFilterDataAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            commonViewModel.getFilterData(true, context as BaseActivity)
        } else {
            mBinding.rvLawyerList.gone()
            mBinding.noLawyer.gone()
            mBinding.noInternetLawyer.llNointernet.visible()
        }
    }

    override fun handleListener() {
        mBinding.noInternetLawyer.btnTryAgain.setOnClickListener(this)
        mBinding.lyLawyerListFilter.lySearchFilter.setOnClickListener(this)
        mBinding.lyLawyerListFilter.llsearch.setOnClickListener(this)
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
                if (!TextUtils.isEmpty(mBinding.lyLawyerListFilter.edtLoginEmail.text.toString())) {
                    callAPI(mBinding.lyLawyerListFilter.edtLoginEmail.text.toString(), "", "")
                }
            }
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
        btnDone.setOnClickListener {
            dialog.dismiss()
                callAPI("",years_of_exp,specialization)
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
        chip.setTextColor(ContextCompat.getColor(context, R.color.black))
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
                chip.setTextColor(ContextCompat.getColor(context, R.color.black))
                chip.isChecked = false

            }
        }
        return chip
    }
}