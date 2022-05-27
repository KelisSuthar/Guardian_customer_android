package com.app.guardian.shareddata.repo

import androidx.lifecycle.MutableLiveData
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
import com.app.guardian.model.SupportGroup.SupportGroupResp
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.model.cms.CMSResp
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.model.specializationList.SpecializationListResp
import com.app.guardian.shareddata.BaseView
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.google.gson.JsonObject
import java.net.IDN

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

    fun getLawyerList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        lawyerResp: MutableLiveData<RequestState<MutableList<LawyerListResp>>>
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

    fun getLawyerProfileDetails(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        lawyerProfileDetails: MutableLiveData<RequestState<LawyerProfileDetailsResp>>,
        idn: Int
    )

    fun getSeekLegalAdviceList(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        seekLegalAdvice: MutableLiveData<RequestState<MutableList<SeekLegalAdviceResp>>>,
        idn: Int
    )

    fun deleteSeekLegalAdvice(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>,
        idn: Int
    )

    fun addSeekLegalAdvice(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun updateSeekLegalAdvice(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun getKnowYourRights(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<KnowYourRightsResp>>>
    )

    fun getUserConnectedHistory(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<ConnectedHistoryResp>>>
    )

    fun getLawyerConnectedHistory(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<ConnectedHistoryResp>>>
    )

    fun getMediatorConnectedHistory(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<ConnectedHistoryResp>>>
    )

    fun checkSubscription(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CheckSubscriptionResp>>
    )

    fun getLawyerSubscriptionPlanList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<SubscriptionPlanResp>>>
    )

    fun addBannerSubscription(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun addBanner(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun changePass(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<CommonResponse>>>
    )

    fun getFilterData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<FilterResp>>
    )

    fun getSpecializationList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<SpecializationListResp>>>
    )

    fun cmsData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<CMSResp>>>
    )

    fun updatePhoneOtpVerify(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<CommonResponse>>>
    )

    fun supportGroup(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<SupportGroupResp>>>
    )

}

