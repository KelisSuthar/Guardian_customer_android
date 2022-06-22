package com.app.guardian.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.RequestState
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.repo.UserRepo
import com.app.guardian.utils.ApiConstant
import com.google.gson.JsonObject

class LawyerViewModel(private val mUserRepository: UserRepo) : ViewModel() {

    //For Common Resp
    private val commonResponse = MutableLiveData<RequestState<CommonResponse>>()
    fun getCommonResp(): LiveData<RequestState<CommonResponse>> = commonResponse

    fun addSeekLegalAdvice(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        title: String,
        desc: String,
    ) {
        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_TITLE, title)
        body.addProperty(ApiConstant.EXTRAS_DESC, desc)


        mUserRepository.addSeekLegalAdvice(
            body,
            isInternetConnected,
            baseView,
            commonResponse
        )
    }

    fun updateSeekLegalAdvice(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        title: String,
        desc: String,
        seek_legal_advice_id: Int,
    ) {
        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_TITLE, title)
        body.addProperty(ApiConstant.EXTRAS_DESC, desc)
        body.addProperty(ApiConstant.EXTRAS_SEEK_LEGAL_ADV, seek_legal_advice_id)
        mUserRepository.updateSeekLegalAdvice(
            body,
            isInternetConnected,
            baseView,
            commonResponse
        )
    }

    fun deleteSeekLegalAdvice(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        id: Int?
    ) {
        mUserRepository.deleteSeekLegalAdvice(
            isInternetConnected,
            baseView,
            commonResponse,
            id!!
        )
    }

    private val SubscriptionResp =
        MutableLiveData<RequestState<MutableList<SubscriptionPlanResp>>>()

    fun getSubcriptionPlanResp(): LiveData<RequestState<MutableList<SubscriptionPlanResp>>> =
        SubscriptionResp

    fun getLawyerSubscriptionList(
        isInternetConnected: Boolean,
        baseView: BaseActivity,

        ) {
        mUserRepository.getLawyerSubscriptionPlanList(
            isInternetConnected,
            baseView,
            SubscriptionResp
        )
    }

    fun addBanners(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        banner_avatar: String,
        url: String,
        start_date: String,
        end_date: String,
    ) {

        val body = JsonObject()

//        if (banner_avatar != "") {
//            val file = File(banner_avatar)
//            file.let {
//                body.addFormDataPart(
//                    ApiConstant.EXTRAS_BANNER_AVATAR,
//                    it.name,
//                    it.asRequestBody("image/*".toMediaTypeOrNull())
//                )
//            }
//        }
        body.addProperty(ApiConstant.EXTRAS_BANNER_AVATAR, banner_avatar)
        body.addProperty(ApiConstant.EXTRAS_URL, url)
        body.addProperty(ApiConstant.EXTRAS_START_DATE, start_date)
        body.addProperty(ApiConstant.EXTRAS_END_DATE, end_date)
        mUserRepository.addBanner(
            body,
            isInternetConnected,
            baseView,
            commonResponse
        )
    }  fun addBannersSubscription(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        plan_id: String,
        price: String,
        shared_secret: String,
    ) {

        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_PLAN_ID, plan_id)
        body.addProperty(ApiConstant.EXTRAS_PRICE, price)
        body.addProperty(ApiConstant.EXTRAS_APPLE_RECEIPT, "fddgfgfgfg@hardik15")
        body.addProperty(ApiConstant.EXTRAS_SHARED_SECRET, shared_secret)
        mUserRepository.addBannerSubscription(
            body,
            isInternetConnected,
            baseView,
            commonResponse
        )
    }



}