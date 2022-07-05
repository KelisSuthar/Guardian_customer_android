package com.app.guardian.ui.SeekLegalAdvice.AddEditSeekLegalAdv

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.app.guardian.R
import com.app.guardian.common.IntegratorImpl
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ValidationView
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentAddSeekLegalAdviceBinding
import com.app.guardian.model.viewModels.LawyerViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import org.koin.android.viewmodel.ext.android.viewModel

class AddSeekLegalAdvice(isEdit: Boolean, id: Int, title: String, desc: String) : BaseFragment(),
    View.OnClickListener {
    private val mViewModel: LawyerViewModel by viewModel()
    lateinit var mBinding: FragmentAddSeekLegalAdviceBinding
    var is_edit = isEdit
    var seek_legal_adv_id = id
    var imm: InputMethodManager? = null
    var edit_title = title
    var edit_desc = desc
    override fun getInflateResource(): Int {
        return R.layout.fragment_add_seek_legal_advice
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.seek_legal_advice),
            true,
            true
        )
        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (is_edit) {
            mBinding.edtTitle.setText(edit_title)
            mBinding.edtDesc.setText(edit_desc)

        }
        mBinding.edtDesc.filters = arrayOf(InputFilter.LengthFilter(700))
        mBinding.edtTitle.filters = arrayOf(InputFilter.LengthFilter(60))


        setFocus(mBinding.edtTitle)
        setFocus(mBinding.edtDesc)
        mBinding.edtDesc.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mBinding.edtDesc.clearFocus()

                imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setFocus(edtText: TextInputEditText) {
        edtText.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                val value = edtText.text?.trim().toString()
                if (!hasFocus) {

                    if (!TextUtils.isEmpty(value) && value.length > 5) {
                        ReusedMethod.ShowNoBorders(requireContext(), edtText)
                    }
                }
            }
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.btnSubmit.setOnClickListener(this)
        mBinding.noInternetAddSeekLegal.btnTryAgain.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getCommonResp().observe(this, Observer { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse.let {
                    it?.data?.let { data ->

                        if (it.status) {

                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                            if (!is_edit) {
                                displayDeleteConfirmDialog()

                            } else {
                                Handler().postDelayed({
                                    requireActivity().onBackPressed()
                                }, 500)

                            }
                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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
        })
    }

    fun displayDeleteConfirmDialog(

    ) {
        val dialog = Dialog(
            requireContext(),
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_seek_legal_advice_add_more)
        dialog.setCancelable(true)

        val Yes = dialog.findViewById<MaterialTextView>(R.id.txtDialogYes)
        val NO = dialog.findViewById<MaterialTextView>(R.id.txtDeleteNo)

        Yes.setOnClickListener {
            dialog.dismiss()
            mBinding.edtDesc.text!!.clear()
            mBinding.edtTitle.text!!.clear()
            mBinding.edtTitle.requestFocus()
            imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            if (is_edit) {
                seek_legal_adv_id = 0
                is_edit = false
            }
        }

        NO.setOnClickListener {
            dialog.dismiss()
            requireActivity().onBackPressed()
        }
        dialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {

            }
            R.id.btnTryAgain -> {

                callAddEditAdviceApi()
            }
            R.id.btnSubmit -> {
                validation()
            }
        }
    }

    private fun validation() {
        IntegratorImpl.isValidAddSeekAdv(
            mBinding.edtTitle.text?.trim().toString(),
            mBinding.edtDesc.text?.trim().toString(),
            object : ValidationView.AddSeekLegalAdvice {
                override fun emptyTitle() {
                    ReusedMethod.displayMessageDialog(
                        requireActivity(),
                        "",
                        resources.getString(R.string.empty_title),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(requireContext(), mBinding.edtTitle)
                }

                override fun length_Title() {
                    ReusedMethod.displayMessageDialog(
                        requireActivity(),
                        "",
                        resources.getString(R.string.error_title),
                        false,
                        "Cancel",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(requireContext(), mBinding.edtTitle)
                }


                override fun emptyTDesc() {
                    ReusedMethod.displayMessageDialog(
                        requireActivity(),
                        "",
                        resources.getString(R.string.empty_desc),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(requireContext(), mBinding.edtTitle)
                    ReusedMethod.ShowRedBorders(requireContext(), mBinding.edtDesc)
                }

                override fun length_desc() {
                    ReusedMethod.displayMessageDialog(
                        requireActivity(),
                        "",
                        resources.getString(R.string.error_description),
                        false,
                        "Cancel",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(requireContext(), mBinding.edtTitle)
                    ReusedMethod.ShowRedBorders(requireContext(), mBinding.edtDesc)
                }

                override fun success() {
                    callAddEditAdviceApi()
                    ReusedMethod.ShowNoBorders(requireContext(), mBinding.edtDesc)
                    ReusedMethod.ShowNoBorders(requireContext(), mBinding.edtTitle)
                }

            })
    }

    private fun callAddEditAdviceApi() {
        mBinding.noInternetAddSeekLegal.llNointernet.gone()
        mBinding.noAddSeekLegal.gone()
        mBinding.ns.visible()
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            if (is_edit) {
                mViewModel.updateSeekLegalAdvice(
                    true,
                    context as BaseActivity,
                    mBinding.edtTitle.text?.trim().toString(),
                    mBinding.edtDesc.text?.trim().toString(),
                    seek_legal_adv_id
                )
            } else {
                mViewModel.addSeekLegalAdvice(
                    true,
                    context as BaseActivity,
                    mBinding.edtTitle.text?.trim().toString(),
                    mBinding.edtDesc.text?.trim().toString(),
                )
            }


        } else {
            mBinding.noInternetAddSeekLegal.llNointernet.visible()
            mBinding.noAddSeekLegal.gone()
            mBinding.ns.gone()
        }
    }

}