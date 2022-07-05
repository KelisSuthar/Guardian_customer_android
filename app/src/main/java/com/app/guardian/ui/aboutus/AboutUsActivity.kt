package com.app.guardian.ui.aboutus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.core.app.ActivityCompat
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.checkPermissions
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.loadWebViewData
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityAboutUsBinding
import com.app.guardian.shareddata.base.BaseActivity


class AboutUsActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityAboutUsBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_about_us
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.headderAboutUs.tvHeaderText.text = resources.getString(R.string.about_us)

//        checkPermissions(
//            this,
//            AppConstants.EXTRA_CALL_PERMISSION,
//            Manifest.permission.CALL_PHONE
//        )
    }

    override fun onResume() {
        super.onResume()
        mBinding.noInternetoginAboutUs.llNointernet.gone()
        mBinding.cl.gone()
        if (ReusedMethod.isNetworkConnected(this)) {
            mBinding.webview.loadWebViewData(SharedPreferenceManager.getCMS()?.about_us.toString())
            for (i in SharedPreferenceManager.getCMS()!!.contact.indices) {
                if (SharedPreferenceManager.getCMS()!!.contact[i].title == AppConstants.EXTRA_PHONE) {
                    mBinding.btnContact.text = SharedPreferenceManager.getCMS()!!.contact[i].value
                }
                if (SharedPreferenceManager.getCMS()!!.contact[i].title == AppConstants.EXTRA_EMAIL) {
                    mBinding.btnEmail.text = SharedPreferenceManager.getCMS()!!.contact[i].value
                }
            }

            mBinding.cl.visible()
        } else {
            mBinding.noInternetoginAboutUs.llNointernet.visible()
            mBinding.cl.gone()
        }
    }

    override fun initObserver() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    override fun handleListener() {
        mBinding.headderAboutUs.ivBack.setOnClickListener(this)
        mBinding.noInternetoginAboutUs.btnTryAgain.setOnClickListener(this)
        mBinding.btnEmail.setOnClickListener(this)
        mBinding.btnContact.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
            R.id.btnTryAgain -> {
                onResume()
            }
            R.id.btnEmail -> {
                val mIntent = Intent(Intent.ACTION_SEND)
                mIntent.data = Uri.parse("mailto:")
                mIntent.type = "text/plain"
                mIntent.putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(mBinding.btnEmail.text.trim().toString())
                )
                mIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
                try {
                    startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
                } catch (e: Exception) {
                }
            }
            R.id.btnContact -> {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    displayMessage(this, "Please Accept Permission to Continue")
                    checkPermissions(
                        this,
                        AppConstants.EXTRA_CALL_PERMISSION,
                        Manifest.permission.CALL_PHONE
                    )
                } else {
                    startActivity(
                        Intent(
                            Intent.ACTION_CALL,
                            Uri.parse("tel:" + mBinding.btnContact.text.trim().toString())
                        )
                    )
                }

            }
        }
    }

}