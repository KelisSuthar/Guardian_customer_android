package com.app.guardian.shareddata.repo

import androidx.lifecycle.MutableLiveData
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.ForgotPass.ForgotPassResp
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.Login.User
import com.app.guardian.model.RequestState
import com.app.guardian.model.SignUp.SignupResp
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.shareddata.BaseView
import com.app.guardian.shareddata.base.BaseActivity
import com.google.gson.JsonObject

interface UserRepo {
//    fun getKey(
//        body: JsonObject,
//        isInternetConnected: Boolean,
//        baseView: BaseView,
//        callback: MutableLiveData<RequestState<KeyData>>
//    )

    fun doSignIn(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseView,
        callback: MutableLiveData<RequestState<LoginResp>>
    )

    fun doSignUp(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseView,
        callback: MutableLiveData<RequestState<SignupResp>>
    )

    fun getSubscriptionPlanList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        subscriptionResp: MutableLiveData<RequestState<MutableList<SubscriptionPlanResp>>>
    )

    fun buySubscriptionPlan(
        buyPlanJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        subscriptionResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun verifyOTP(
        verifyOTPJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<User>>
    )

    fun forgotPass(
        forgotPassJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        fotgotPassResp: MutableLiveData<RequestState<ForgotPassResp>>
    )

    fun signout(
        internetConnected: Boolean,
        baseView: BaseActivity,
        loginResp: MutableLiveData<RequestState<LoginResp>>
    )

    fun resetPassword(
        restePassJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<MutableList<CommonResponse>>>
    )

    fun getuserHomeBanners(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<UserHomeBannerResp>>>
    )

    fun getUserDetails(
        internetConnected: Boolean,
        baseView: BaseActivity,
        userDetailsResp: MutableLiveData<RequestState<UserDetailsResp>>
    )

}

