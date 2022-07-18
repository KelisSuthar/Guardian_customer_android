package com.app.guardian.termsandcondtions

import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.app.guardian.R
import com.app.guardian.common.AppConstants
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
            val i = intent
            val extras = i.extras
            if (extras != null) {
                for (key in extras.keySet()) {
                    val value = extras[key]
                    Log.d(
                        "TC",
                        "Extras received at initView splash:  Key: $key Value: $value"
                    )
                }
                val notification_type =
                    intent.extras!!.getString("type")
                val notification_id = intent.extras!!.getString("sender_id")
                val notification_URL =
                    intent.extras!!.getString("url")
                val notification_meeting_id = intent.extras!!.getString("room_id")

                if (notification_type != AppConstants.EXTRA_VIDEOCALLREQ_PAYLOAD) {
                    if (!notification_id.toString().isNullOrEmpty() || !notification_id.toString()
                            .isNullOrEmpty()
                    ) {
                        Log.e("THIS_APP_GUAR_TC", notification_type.toString())
                        Log.e("THIS_APP_GUAR_TC", notification_id.toString())

                    }
                } else {
                    if (!notification_URL.isNullOrEmpty() || !notification_meeting_id.isNullOrEmpty()) {

                        when (SharedPreferenceManager.getLoginUserRole()) {
                            AppConstants.APP_ROLE_LAWYER -> {

                            }
                            AppConstants.APP_ROLE_USER -> {

                            }
                            AppConstants.APP_ROLE_MEDIATOR -> {

                            }

                        }
                    }
                }


            } else {

            }
//            mBinding.webview.loadWebViewData(SharedPreferenceManager.getCMS()!!.terms_conditions)
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