package com.studelicious_user.shareddata.repo

import androidx.lifecycle.MutableLiveData
import com.app.guardian.model.ApiError
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.ForgotPass.ForgotPassResp
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.RequestState
import com.app.guardian.model.SignUp.SignupResp
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.app.guardian.shareddata.BaseView
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.endpoint.ApiEndPoint
import com.app.guardian.shareddata.networkmanager.NetworkManager
import com.app.guardian.shareddata.repo.UserRepo
import com.app.guardian.utils.Config
import com.google.gson.JsonObject


class UserRepository(private val mApiEndPoint: ApiEndPoint) : UserRepo {

//    override fun getKey(
//        body: JsonObject,
//        isInternetConnected: Boolean,
//        baseView: BaseView,
//        callbackKey: MutableLiveData<RequestState<KeyData>>
//    ) {
//        if (!isInternetConnected) {
//            callbackKey.value =
//                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
//        } else {
//            callbackKey.value = RequestState(progress = true)
//            NetworkManager.requestData(
//                mApiEndPoint.getKey(),
//                baseView, callbackKey
//            )
//        }
//    }

    override fun doSignIn(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseView,
        callbackKey: MutableLiveData<RequestState<LoginResp>>
    ) {
        if (!isInternetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.doLogIn(body), baseView, callbackKey)
        }
    }

    override fun doSignUp(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseView,
        callbackKey: MutableLiveData<RequestState<SignupResp>>
    ) {
        if (!isInternetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.doSignUp(body), baseView, callbackKey)
        }
    }

    override fun getSubscriptionPlanList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        subscriptionResp: MutableLiveData<RequestState<MutableList<SubscriptionPlanResp>>>
    ) {
        if (!internetConnected) {
            subscriptionResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            subscriptionResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getSubscriptionPlan(),
                baseView,
                subscriptionResp
            )
        }
    }

    override fun buySubscriptionPlan(
        buyPlanJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.buysubscribePlan(buyPlanJson),
                baseView,
                callbackKey
            )
        }
    }

    override fun verifyOTP(
        verifyOTPJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResponse.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResponse.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.verifyOTP(verifyOTPJson),
                baseView,
                commonResponse
            )
        }
    }

    override fun forgotPass(
        forgotPassJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        fotgotPassResp: MutableLiveData<RequestState<ForgotPassResp>>
    ) {
        if (!internetConnected) {
            fotgotPassResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            fotgotPassResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.forGotPass(forgotPassJson),
                baseView,
                fotgotPassResp
            )
        }
    }

    override fun signout(
        internetConnected: Boolean,
        baseView: BaseActivity,
        loginResp: MutableLiveData<RequestState<LoginResp>>
    ) {
        if (!internetConnected) {
            loginResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            loginResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.signOut(), baseView, loginResp)
        }
    }

    override fun resetPassword(
        restePassJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResponse.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResponse.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.resetPass(restePassJson),
                baseView,
                commonResponse
            )
        }
    }
}
