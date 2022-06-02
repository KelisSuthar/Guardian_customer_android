package com.app.guardian.ui.Lawyer.AddBaner

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentAddBannerBinding
import com.app.guardian.model.viewModels.LawyerViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.utils.Config
import com.github.dhaval2404.imagepicker.ImagePicker
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddBannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddBannerFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentAddBannerBinding
    private val mViewModel: LawyerViewModel by viewModel()
    var IMAGE_CODE = 1002
    var bannerImage = ""
    override fun getInflateResource(): Int {
        return R.layout.fragment_add_banner
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.banner_ads),
            true,
            true
        )
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvAddImage.setOnClickListener(this)
        mBinding.lladdImg.setOnClickListener(this)
        mBinding.btnSubmit.setOnClickListener(this)
        mBinding.noInternetAddBanner.btnTryAgain.setOnClickListener(this)


    }

    override fun initObserver() {
        mViewModel.getCommonResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            requireActivity().onBackPressed()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.lladdImg -> {
                ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(
                        1080,
                        1080
                    )
                    .start(IMAGE_CODE)
            }
            R.id.cvAddImage -> {
                ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(
                        1080,
                        1080
                    )
                    .start(IMAGE_CODE)
            }
            R.id.btnSubmit -> {
                validations()
            }
            R.id.btnTryAgain -> {
                validations()
            }
        }

    }

    private fun validations() {

        if (TextUtils.isEmpty(bannerImage)) {
            ReusedMethod.displayMessageDialog(
                requireActivity(),
                "",
                "Please upload banner image.",
                false,
                "OK",
                ""
            )
        } else if (TextUtils.isEmpty(mBinding.edtShareLink.text?.trim().toString())) {
            ReusedMethod.displayMessageDialog(
                requireActivity(),
                "",
                "Please add share link for banner.",
                false,
                "OK",
                ""
            )
            ReusedMethod.ShowRedBorders(requireActivity(), mBinding.edtShareLink)
        } else if (!isValidUrl(mBinding.edtShareLink.text?.trim().toString())) {
            ReusedMethod.displayMessageDialog(
                requireActivity(),
                "",
                "Please add valid link",
                false,
                "OK",
                ""
            )
            ReusedMethod.ShowRedBorders(requireActivity(), mBinding.edtShareLink)
        } else {
                            callAddBannerApi()
            ReusedMethod.ShowNoBorders(requireActivity(), mBinding.edtShareLink)
        }
    }

    fun isValidUrl(linkToCheck: String?): Boolean {
        if (linkToCheck == null) {
            return false;
        }
        return Pattern.matches(
            "^(https?://)?([a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+)+(/*[A-Za-z0-9/\\-_&:?\\+=//.%]*)*",
            linkToCheck
        );
    }

    private fun callAddBannerApi() {
        mBinding.noInternetAddBanner.llNointernet.gone()
        mBinding.cl1.visible()
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.addBanners(
                true,
                context as BaseActivity,
                bannerImage,
                mBinding.edtShareLink.text?.trim().toString(),
                "2022-05-11 23:00:00",
                "2022-07-11 23:00:00"
            )
        } else {
            mBinding.noInternetAddBanner.llNointernet.visible()
            mBinding.cl1.gone()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {

                IMAGE_CODE -> {
                    val uri: Uri = data?.data!!
                    mBinding.ivBannerImg.visible()
                    mBinding.lladdImg.gone()
                    mBinding.ivBannerImg.setImageURI(uri)
                    bannerImage = ImagePicker.getFilePath(data).toString()
                }
            }

        }
    }

}