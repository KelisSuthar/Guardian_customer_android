package com.app.guardian.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.guardian.model.CheckSub.CheckSubscriptionResp
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.KnowYourRights.KnowYourRightsResp
import com.app.guardian.model.ListFilter.FilterResp
import com.app.guardian.model.RequestState
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.model.cms.CMSResp
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.repo.UserRepo
import com.app.guardian.utils.ApiConstant
import com.google.gson.JsonObject

class CommonScreensViewModel(private val mUserRepository: UserRepo) : ViewModel() {

    //For Common Resp
    private val commonResp = MutableLiveData<RequestState<CommonResponse>>()
    fun getCommonResp(): LiveData<RequestState<CommonResponse>> =
        commonResp

    //USER HOME BANNERS
    private val userBannerResp = MutableLiveData<RequestState<MutableList<UserHomeBannerResp>>>()
    fun getuserHomeBannerResp(): LiveData<RequestState<MutableList<UserHomeBannerResp>>> =
        userBannerResp

    fun getuserHomeBanners(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ) {


        mUserRepository.getuserHomeBanners(
            isInternetConnected,
            baseView,
            userBannerResp
        )
    }

    private val userDetailsResp = MutableLiveData<RequestState<UserDetailsResp>>()
    fun getuserDetailsResp(): LiveData<RequestState<UserDetailsResp>> = userDetailsResp

    fun getUserDetials(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ) {


        mUserRepository.getUserDetails(
            isInternetConnected,
            baseView,
            userDetailsResp
        )
    }

    private val knowYourRightsResp =
        MutableLiveData<RequestState<MutableList<KnowYourRightsResp>>>()

    fun getKnowYourRightsResp(): LiveData<RequestState<MutableList<KnowYourRightsResp>>> =
        knowYourRightsResp

    fun getKnowRights(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        city: String, state: String
    ) {

        val body = JsonObject()
        body.addProperty(ApiConstant.EXTRAS_CITY, city)
        body.addProperty(ApiConstant.EXTRAS_STATE, state)


        mUserRepository.getKnowYourRights(
            body,
            isInternetConnected,
            baseView,
            knowYourRightsResp
        )
    }

    ///Connected History API CALLING
    private val connectedHistoryResp =
        MutableLiveData<RequestState<MutableList<ConnectedHistoryResp>>>()

    fun getConenctedHistoryResp(): LiveData<RequestState<MutableList<ConnectedHistoryResp>>> =
        connectedHistoryResp

    fun getUserConnectedHistory(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        ser: String, type: String
    ) {

        val body = JsonObject()
        body.addProperty(ApiConstant.EXTRAS_SERCH, ser)
        body.addProperty(ApiConstant.EXTRAS_TYPE, type)


        mUserRepository.getUserConnectedHistory(
            body,
            isInternetConnected,
            baseView,
            connectedHistoryResp
        )
    }

    fun getLawyerConnectedHistory(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        ser: String,
        type: String,
    ) {

        val body = JsonObject()
        body.addProperty(ApiConstant.EXTRAS_SERCH, ser)
        body.addProperty(ApiConstant.EXTRAS_TYPE, type)

        mUserRepository.getLawyerConnectedHistory(
            body,
            isInternetConnected,
            baseView,
            connectedHistoryResp
        )
    }

    fun getMediatorConnectedHistory(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        ser: String,
    ) {

        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_SERCH, ser)



        mUserRepository.getMediatorConnectedHistory(
            body,
            isInternetConnected,
            baseView,
            connectedHistoryResp
        )
    }

    private val checkSubscriptionResp = MutableLiveData<RequestState<CheckSubscriptionResp>>()
    fun getcheckSubResp(): LiveData<RequestState<CheckSubscriptionResp>> =
        checkSubscriptionResp

    fun checkSubscritpion(
        isInternetConnected: Boolean,
        baseView: BaseActivity,

        ) {
        mUserRepository.checkSubscription(
            isInternetConnected,
            baseView,
            checkSubscriptionResp
        )
    }


    //GET FILTER DATA RESP
    private val filterResp = MutableLiveData<RequestState<FilterResp>>()
    fun getFilterResp(): LiveData<RequestState<FilterResp>> = filterResp

    fun getFilterData(
        isInternetConnected: Boolean,
        baseView: BaseActivity,

        ) {
        mUserRepository.getFilterData(
            isInternetConnected,
            baseView,
            filterResp
        )
    }

    //GET CMS DATA
    private val cmsResp = MutableLiveData<RequestState<MutableList<CMSResp>>>()
    fun getCMSResp(): LiveData<RequestState<MutableList<CMSResp>>> = cmsResp

    fun getCMSData(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ) {
        mUserRepository.cmsData(
            isInternetConnected,
            baseView,
            cmsResp
        )
    }
}