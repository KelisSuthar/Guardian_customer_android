package com.app.guardian.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.KnowYourRights.KnowYourRightsResp
import com.app.guardian.model.RequestState
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.repo.UserRepo
import com.app.guardian.utils.ApiConstant
import com.google.gson.JsonObject

class CommonScreensViewModel(private val mUserRepository: UserRepo) : ViewModel() {

    //For Common Resp
    private val commonResp = MutableLiveData<RequestState<CommonResponse>>()

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
}