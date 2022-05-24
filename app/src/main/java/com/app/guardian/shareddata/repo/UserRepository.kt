package com.studelicious_user.shareddata.repo

import androidx.lifecycle.MutableLiveData
import com.app.guardian.model.ApiError
import com.app.guardian.model.CheckSub.CheckSubscriptionResp
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.ForgotPass.ForgotPassResp
import com.app.guardian.model.KnowYourRights.KnowYourRightsResp
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.model.LawyerProfileDetails.LawyerProfileDetailsResp
import com.app.guardian.model.ListFilter.FilterResp
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.Login.User
import com.app.guardian.model.RequestState
import com.app.guardian.model.SeekLegalAdviceResp.SeekLegalAdviceResp
import com.app.guardian.model.SignUp.SignupResp
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.model.specializationList.SpecializationListResp
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
        UserResp: MutableLiveData<RequestState<User>>
    ) {
        if (!internetConnected) {
            UserResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            UserResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.verifyOTP(verifyOTPJson),
                baseView,
                UserResp
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
        commonResponse: MutableLiveData<RequestState<MutableList<CommonResponse>>>
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

    override fun getuserHomeBanners(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<UserHomeBannerResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getUserHomeBanners(),
                baseView,
                commonResp
            )
        }
    }

    override fun getUserDetails(
        internetConnected: Boolean,
        baseView: BaseActivity,
        userDetailsResp: MutableLiveData<RequestState<UserDetailsResp>>
    ) {
        if (!internetConnected) {
            userDetailsResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            userDetailsResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getUserDetails(),
                baseView,
                userDetailsResp
            )
        }
    }

    override fun getLawyerList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        lawyerResp: MutableLiveData<RequestState<MutableList<LawyerListResp>>>
    ) {
        if (!internetConnected) {
            lawyerResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            lawyerResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getLawyerList(body),
                baseView,
                lawyerResp
            )
        }
    }

    override fun getLawyerProfileDetails(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        lawyerProfileDetails: MutableLiveData<RequestState<LawyerProfileDetailsResp>>,
        idn: Int
    ) {
        if (!isInternetConnected) {
            lawyerProfileDetails.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            lawyerProfileDetails.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getLawyerProfileDetails(idn),
                baseView,
                lawyerProfileDetails
            )
        }
    }

    override fun getSeekLegalAdviceList(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        seekLegalAdvice: MutableLiveData<RequestState<MutableList<SeekLegalAdviceResp>>>,
        idn: Int
    ) {
        if (!isInternetConnected) {
            seekLegalAdvice.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            seekLegalAdvice.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getSeekLegalAdvice(idn),
                baseView,
                seekLegalAdvice
            )
        }
    }

    override fun deleteSeekLegalAdvice(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>,
        idn: Int
    ) {
        if (!isInternetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.deleteSeekLegalAdvice(idn),
                baseView,
                commonResp
            )
        }
    }

    override fun addSeekLegalAdvice(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.addSeekLegalAdvice(body),
                baseView,
                commonResp
            )
        }
    }

    override fun updateSeekLegalAdvice(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.updateSeekLegalAdvice(body),
                baseView,
                commonResp
            )
        }
    }

    override fun getKnowYourRights(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<KnowYourRightsResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getKnowYourRights(body),
                baseView,
                commonResp
            )
        }
    }

    override fun getUserConnectedHistory(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<ConnectedHistoryResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getuserContactHistory(body),
                baseView,
                commonResp
            )
        }
    }

    override fun getLawyerConnectedHistory(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<ConnectedHistoryResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getlawyerContactHistory(body),
                baseView,
                commonResp
            )
        }
    }

    override fun getMediatorConnectedHistory(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<ConnectedHistoryResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getmediatorContactHistory(body),
                baseView,
                commonResp
            )
        }
    }

    override fun checkSubscription(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CheckSubscriptionResp>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.checkSubscriptions(),
                baseView,
                commonResp
            )
        }
    }

    override fun getLawyerSubscriptionPlanList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<SubscriptionPlanResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getLawyerSubscriptionPlan(),
                baseView,
                commonResp
            )
        }
    }

    override fun addBannerSubscription(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.addBannerSubscription(body),
                baseView,
                commonResp
            )
        }
    }

    override fun addBanner(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.addBanner(body),
                baseView,
                commonResp
            )
        }
    }

    override fun changePass(
        body: JsonObject,
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
                mApiEndPoint.changePass(body),
                baseView,
                commonResponse
            )
        }
    }

    override fun getFilterData(

        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<FilterResp>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getFilterListData(),
                baseView,
                commonResp
            )
        }
    }

    override fun getSpecializationList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<SpecializationListResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getSpecializationList(),
                baseView,
                commonResp
            )
        }
    }
}
