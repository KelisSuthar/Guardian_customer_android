package com.app.guardian.ui.Lawyer.AddBaner

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.guardian.R
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentAddBannerBinding
import com.app.guardian.model.viewModels.LawyerViewModel
import com.app.guardian.shareddata.base.BaseFragment
import com.github.dhaval2404.imagepicker.ImagePicker
import org.koin.android.viewmodel.ext.android.viewModel

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

    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvAddImage.setOnClickListener(this)
        mBinding.lladdImg.setOnClickListener(this)
    }

    override fun initObserver() {

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