package com.app.guardian.ui.SubscriptionPlan

import android.content.Intent
import android.util.Log
import android.view.View
import com.android.billingclient.api.*
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivitySubScriptionPlanScreenBinding
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.model.viewModels.LawyerViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.SubscriptionPlan.Adapter.SubscriptionPlanAdapter
import com.app.guardian.utils.Config
import com.google.android.gms.common.util.CollectionUtils
import org.koin.android.viewmodel.ext.android.viewModel


class SubScriptionPlanScreen : BaseActivity(), View.OnClickListener, PurchasesUpdatedListener {
    lateinit var mBinding: ActivitySubScriptionPlanScreenBinding
    private val mViewModel: AuthenticationViewModel by viewModel()
    private val lawyerViewModel: LawyerViewModel by viewModel()
    var subscriptionPlanAdapter: SubscriptionPlanAdapter? = null
    var shared_secret = "ifjsjfkjs;p;sjflk;jmsw;lfalsw"
    var start_date = ""
    var end_date = ""
    var array = ArrayList<SubscriptionPlanResp>()

    private lateinit var billingClient: BillingClient//inApp Purchase
    private val skuList = CollectionUtils.listOf("android.test.purchased")//inApp Purchase
    var checkUpdate: Boolean = false//inApp Purchase
    private var isSkuIdGated = false//inApp Purchase
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_sub_scription_plan_screen
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.headderSubscritpion.tvHeaderText.text = resources.getString(R.string.sub_plan)

    }

    override fun onResume() {
        super.onResume()
        setupBillingClient()
//        if (intent.extras != null || intent != null) {
//            if (intent.getBooleanExtra(AppConstants.EXTRA_IS_LAWYER, false)) {
        if (SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_LAWYER
        ) {
            callLawyerBannersubScriptionPlan()
        } else {
            callAPI()
        }


        setAdapter()
        mBinding.recyclerView.visible()
        mBinding.noDataSubscritpion.gone()
        mBinding.noInternetSubscritpion.llNointernet.gone()
    }


    private fun setAdapter() {
        mBinding.recyclerView.adapter = null
        subscriptionPlanAdapter = SubscriptionPlanAdapter(
            this,
            array,
            object : SubscriptionPlanAdapter.onItemClicklisteners {
                override fun onSubclick(position: Int) {
//                    loadAllSKUs()
                    if (SharedPreferenceManager.getString(
                            AppConstants.USER_ROLE,
                            AppConstants.APP_ROLE_USER
                        ) == AppConstants.APP_ROLE_LAWYER
                    ) {
                        callLawyerBannerplanAPI(array[position].id, array[position].pricing)
                    } else {
                        callBuyPlanAPI(array[position].id, array[position].pricing)
                    }

                }

            })
        mBinding.recyclerView.adapter = subscriptionPlanAdapter
    }


    override fun initObserver() {
        //SUBSCRIPTION PLAN RESP
        mViewModel.getSubcriptionPlanResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            array.clear()
                            array.addAll(data)
                            subscriptionPlanAdapter?.notifyDataSetChanged()
                        } else {
                            mBinding.recyclerView.gone()
                            mBinding.noDataSubscritpion.visible()
                            mBinding.noInternetSubscritpion.llNointernet.gone()
                        }

                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                this,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let { ReusedMethod.displayMessage(this, it) }
                    }
                }
            }
        }
        //BUY SUBSCRIPTION PLAN RESP
        mViewModel.getBuySubcriptionPlanResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {

                            SharedPreferenceManager.putBoolean(AppConstants.IS_SUBSCRIBE, true)
                            SharedPreferenceManager.putBoolean(AppConstants.IS_LOGIN, true)
                            startActivity(
                                Intent(
                                    this@SubScriptionPlanScreen,
                                    HomeActivity::class.java
                                )
                            )
                            finish()
                            overridePendingTransition(R.anim.rightto, R.anim.left)


                            ReusedMethod.displayMessage(this, it.message.toString())

                        } else {
                            ReusedMethod.displayMessage(this, it.message.toString())
                        }

                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                this,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let { ReusedMethod.displayMessage(this, it) }
                    }
                }
            }
        }
//GET LAWYER PLan RESP
        lawyerViewModel.getSubcriptionPlanResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            array.clear()
                            array.addAll(data)
                            subscriptionPlanAdapter?.notifyDataSetChanged()
                        } else {
                            mBinding.recyclerView.gone()
                            mBinding.noDataSubscritpion.visible()
                            mBinding.noInternetSubscritpion.llNointernet.gone()
                        }

                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                this,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let { ReusedMethod.displayMessage(this, it) }
                    }
                }
            }
        }

        //BUY LAWYER PLan RESP
        lawyerViewModel.getCommonResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            ReusedMethod.displayMessage(
                                this,
                                it.message.toString()
                            )
                            onBackPressed()
                            finish()
                            overridePendingTransition(R.anim.leftto, R.anim.right)
                        } else {
                            ReusedMethod.displayMessage(
                                this,
                                it.message.toString()
                            )
                        }

                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                this,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let { ReusedMethod.displayMessage(this, it) }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    override fun handleListener() {
        mBinding.headderSubscritpion.ivBack.setOnClickListener(this)
        mBinding.noInternetSubscritpion.btnTryAgain.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }

    private fun callBuyPlanAPI(id: Int?, pricing: String?) {

        if (ReusedMethod.isNetworkConnected(this)) {
            mViewModel.BuySubscriptionPlanList(
                true,
                this,
                id.toString(),
                pricing.toString(),
                shared_secret,
//                start_date,
//                end_date
            )
        } else {
            mBinding.recyclerView.gone()
            mBinding.noDataSubscritpion.gone()
            mBinding.noInternetSubscritpion.llNointernet.gone()
        }

    }

    private fun callLawyerBannerplanAPI(id: Int?, pricing: String?) {
        if (ReusedMethod.isNetworkConnected(this)) {
            lawyerViewModel.addBannersSubscription(
                true,
                this,
                id.toString(),
                pricing.toString(),
                shared_secret,
//                start_date,
//                end_date
            )
        } else {
            mBinding.recyclerView.gone()
            mBinding.noDataSubscritpion.gone()
            mBinding.noInternetSubscritpion.llNointernet.gone()
        }

    }


    private fun callAPI() {
        if (ReusedMethod.isNetworkConnected(this)) {
            mViewModel.SubscriptionPlanList(true, this)
        } else {
            mBinding.recyclerView.gone()
            mBinding.noDataSubscritpion.gone()
            mBinding.noInternetSubscritpion.llNointernet.gone()
        }
    }

    private fun callLawyerBannersubScriptionPlan() {
        if (ReusedMethod.isNetworkConnected(this)) {
            lawyerViewModel.getLawyerSubscriptionList(true, this)
        } else {
            mBinding.recyclerView.gone()
            mBinding.noDataSubscritpion.gone()
            mBinding.noInternetSubscritpion.llNointernet.gone()
        }
    }


    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this@SubScriptionPlanScreen)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.v("###", "Setup Billing Done")

                    //loadAllSKUs()
                    getDetails()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.v("###", "Failed")

            }
        })
    }

    private fun getDetails() {
        billingClient.queryPurchaseHistoryAsync(
            BillingClient.SkuType.INAPP
        )
        { billingResult, purchasesList ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                for (purchase in purchasesList) {
                    checkUpdate = true
                    Log.e("getDetails", "=================")
                    acknowledgePurchase(
                        purchase.purchaseToken,
                        purchase.purchaseTime,
                        purchase.originalJson
                    )
                }
            } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
//                    mBinding.btnContinue.setText(getString(R.string.subscribed))
//                    mBinding.btnContinue.isEnabled = false
                // mBinding.btnContinue.backgroundTintList= ColorStateList.valueOf(Color.GREEN)
            } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
//                    mBinding.btnContinue.setText(getString(R.string.continue_label))
//                    mBinding.btnContinue.isEnabled = true
            } else {
//                    mBinding.btnContinue.setText(getString(R.string.continue_label))
//                    mBinding.btnContinue.isEnabled = true
            }
        }
    }

    private fun acknowledgePurchase(
        purchaseToken: String,
        purchaseTime: Long,
        originalJson: String
    ) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            /*val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage*/

            Log.e("responseCode", billingResult.responseCode.toString())
            if (billingResult.responseCode == 0) {
//                callBuyPlanAPI()
            }

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.e("ABCDEF", "------------------------------")

                if (!isSkuIdGated) {
                    isSkuIdGated = true
                }
            }

        }
    }

    private fun loadAllSKUs() = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (skuDetails in skuDetailsList!!) {
                    if (skuDetails.sku == "android.test.purchased") {
                        val billingFlowParams = BillingFlowParams
                            .newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()
                        billingClient.launchBillingFlow(this, billingFlowParams)
                    }
                }
            }
            // Log.v("###",skuDetailsList!!.get(0).description)

        }

    } else {
        println("Billing Client not ready")
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Log.e("onPurchasesUpdated", "------onPurchasesUpdated--------------")
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach {
                Log.v("acknowledgePurchase", purchases.toString())

                acknowledgePurchase(
                    it.purchaseToken,
                    it.purchaseTime,
                    it.originalJson
                )


            }
            /*for (purchase in purchases) {

            }*/
        } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.v("###", "User Cancelled")
            Log.v("###", billingResult?.debugMessage.toString())


        } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {

        } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {

        } else {
            Log.v("###", billingResult?.debugMessage.toString())

        }
    }
}