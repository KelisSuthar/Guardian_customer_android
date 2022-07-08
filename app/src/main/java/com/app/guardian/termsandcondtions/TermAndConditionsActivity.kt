package com.app.guardian.termsandcondtions

import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.loadWebViewData
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityTermAndConditionsBinding
import com.app.guardian.shareddata.base.BaseActivity


class TermAndConditionsActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityTermAndConditionsBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_term_and_conditions
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.headderTerms.tvHeaderText.text = resources.getString(R.string.terms_amp_conditions)
    }

    override fun initObserver() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    override fun onResume() {
        super.onResume()
        mBinding.cl.gone()
        mBinding.noInternetTerms.llNointernet.gone()
        if (ReusedMethod.isNetworkConnected(this)) {
            showLoadingIndicator(true)
            Handler().postDelayed({
                showLoadingIndicator(false)
            }, 2000)

            mBinding.webview.loadWebViewData(SharedPreferenceManager.getCMS()!!.terms_conditions)
            mBinding.cl.visible()
        } else {
            mBinding.noInternetTerms.llNointernet.visible()
            mBinding.cl.gone()
        }
    }

    override fun handleListener() {
        mBinding.headderTerms.ivBack.setOnClickListener(this)
        mBinding.noInternetTerms.btnTryAgain.setOnClickListener(this)
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
        }
    }
}