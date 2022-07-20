package com.app.guardian.shareddata.repo

import androidx.lifecycle.MutableLiveData
import com.app.guardian.model.AcceptRejectCallByMediator.AcceptRejectCallByMediatorResp
import com.app.guardian.model.ApiError
import com.app.guardian.model.AskModeQ.AskModeQResp
import com.app.guardian.model.AskModeQResp.ChatListResp
import com.app.guardian.model.AskModeQResp.SendMessageResp
import com.app.guardian.model.CheckSub.CheckSubscriptionResp
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.DrivingOffenceList.DrivingOffenceListResp
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.ForgotPass.ForgotPassResp
import com.app.guardian.model.GetVideoCallRequestResp.VideoCallRequestListResp
import com.app.guardian.model.HomeBanners.UserHomeBannerResp
import com.app.guardian.model.KnowYourRights.KnowYourRightsResp
import com.app.guardian.model.LawyerBySpecialization.LawyerBySpecializationResp
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.model.LawyerProfileDetails.LawyerProfileDetailsResp
import com.app.guardian.model.ListFilter.FilterResp
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.Login.User
import com.app.guardian.model.MediatorCallReq.MediatorCallReqResp
import com.app.guardian.model.Notification.NotificationResp
import com.app.guardian.model.OfflineVideos.OfflineUploadedVideoResp
import com.app.guardian.model.OfflineVideos.UploadOfflineVideoResp
import com.app.guardian.model.Radar.RadarListResp
import com.app.guardian.model.RequestState
import com.app.guardian.model.SeekLegalAdviceResp.SeekLegalAdviceResp
import com.app.guardian.model.SendVideoCallReq.SendVideoCallReqResp
import com.app.guardian.model.SignUp.SignupResp
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.app.guardian.model.SupportGroup.SupportGroupResp
import com.app.guardian.model.cms.CMSResp
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.model.scheduleRequestedVideoCall.ScheduleRequestedVideoCallResp
import com.app.guardian.model.sendRequestVirtualWitness.SendRequestVirtualWitnessResp
import com.app.guardian.model.specializationList.SpecializationListResp
import com.app.guardian.shareddata.BaseView
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.endpoint.ApiEndPoint
import com.app.guardian.shareddata.networkmanager.NetworkManager
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
//                mApiEndPoint.getKey(),sendVideoCallRequest
//                baseView, callbackKey
//            )
//        }
//    }

    override fun doSignIn(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseView,
        callback: MutableLiveData<RequestState<LoginResp>>
    ) {
        if (!isInternetConnected) {
            callback.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callback.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.doLogIn(body), baseView, callback)
        }
    }

    override fun doSignUp(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseView,
        callback: MutableLiveData<RequestState<SignupResp>>
    ) {
        if (!isInternetConnected) {
            callback.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callback.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.doSignUp(body), baseView, callback)
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
        subscriptionResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            subscriptionResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
//            subscriptionResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.buysubscribePlan(buyPlanJson),
                baseView,
                subscriptionResp
            )
        }
    }

    override fun verifyOTP(
        verifyOTPJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<User>>
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
        commonResp: MutableLiveData<RequestState<UserHomeBannerResp>>
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
//            commonResp.value = RequestState(progress = true)
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
        commonResp: MutableLiveData<RequestState<MutableList<CommonResponse>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.changePass(body),
                baseView,
                commonResp
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

    override fun cmsData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CMSResp>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getCMSData(),
                baseView,
                commonResp
            )
        }
    }

    override fun updatePhoneOtpVerify(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<CommonResponse>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.updatePhoneOtpVerify(body),
                baseView,
                commonResp
            )
        }
    }

    override fun supportGroup(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<SupportGroupResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getSupportGroup(),
                baseView,
                commonResp
            )
        }
    }

    override fun getNotification(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<NotificationResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getNotifications(),
                baseView,
                commonResp
            )
        }
    }

    override fun deleteNotification(
        id: Int,
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
                mApiEndPoint.deleteNotification(id),
                baseView,
                commonResp
            )
        }
    }

    override fun sendRequestVirtualWitness(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<SendRequestVirtualWitnessResp>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.sendRequestVirtualWitness(body),
                baseView,
                commonResp
            )
        }
    }

    override fun getLawyerBySpecialization(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<LawyerBySpecializationResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getLawyerBySpecialization(body),
                baseView,
                commonResp
            )
        }
    }

    override fun addRadarMapPoint(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<RadarListResp>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.addRadarMapPoint(body),
                baseView,
                commonResp
            )
        }
    }

    override fun deleteRadarMapPoint(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<RadarListResp>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.deleteRadarMapPoint(body),
                baseView,
                commonResp
            )
        }
    }

    override fun getRadarMapList(
        LAT: String,
        LONG: String,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<RadarListResp>>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getRadarMapList(LAT, LONG),
                baseView,
                commonResp
            )
        }
    }

    override fun getChatList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<ChatListResp>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getChatList(body),
                baseView,
                commonResp
            )
        }
    }

    override fun sendMessage(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        sendMessageResp: MutableLiveData<RequestState<SendMessageResp>>
    ) {
        if (!internetConnected) {
            sendMessageResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            sendMessageResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.sendMessageChat(body),
                baseView,
                sendMessageResp
            )
        }
    }

    override fun setUserStatus(
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
                mApiEndPoint.setAppUserStatus(body),
                baseView,
                commonResp
            )
        }
    }

    override fun sendCallingRequestToMediator(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<MediatorCallReqResp>>
    ) {
        if (!internetConnected) {
            commonResponse.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResponse.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.sendCallingRequestToMediator(body),
                baseView,
                commonResponse
            )
        }
    }

    override fun scheduleRequestedVideoCall(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<ScheduleRequestedVideoCallResp>>
    ) {
        if (!internetConnected) {
            commonResponse.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResponse.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.scheduleRequestedVideoCall(body),
                baseView,
                commonResponse
            )
        }
    }

    override fun editprofile(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        loginResp: MutableLiveData<RequestState<UserDetailsResp>>
    ) {
        if (!internetConnected) {
            loginResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            loginResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.updateUserProfile(body),
                baseView,
                loginResp
            )
        }
    }

    override fun getVideoCallRequestUserList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        videoCallRequestListResp: MutableLiveData<RequestState<MutableList<VideoCallRequestListResp>>>
    ) {
        if (!internetConnected) {
            videoCallRequestListResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            videoCallRequestListResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getVideoCallRequestUserList(body),
                baseView,
                videoCallRequestListResp
            )
        }
    }

    override fun getVideoCallRequestLawyerList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        videoCallRequestListResp: MutableLiveData<RequestState<MutableList<VideoCallRequestListResp>>>
    ) {
        if (!internetConnected) {
            videoCallRequestListResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            videoCallRequestListResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getVideoCallRequestLawyerList(body),
                baseView,
                videoCallRequestListResp
            )
        }
    }

    override fun getVideoCallRequestMediatorList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        videoCallRequestListResp: MutableLiveData<RequestState<MutableList<VideoCallRequestListResp>>>
    ) {
        if (!internetConnected) {
            videoCallRequestListResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            videoCallRequestListResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getVideoCallRequestMediatorList(body),
                baseView,
                videoCallRequestListResp
            )
        }
    }


    override fun askModeQuestion(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        askModeQResp: MutableLiveData<RequestState<AskModeQResp>>
    ) {
        if (!internetConnected) {
            askModeQResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            askModeQResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.askModratorAQuestion(body),
                baseView,
                askModeQResp
            )
        }
    }

    override fun sendVideoCallRequest(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        sendVideocallReqresp: MutableLiveData<RequestState<SendVideoCallReqResp>>
    ) {
        if (!internetConnected) {
            sendVideocallReqresp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            sendVideocallReqresp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.sendVideoCallRequest(body),
                baseView,
                sendVideocallReqresp
            )
        }
    }

    override fun uploadOfflineVideo(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        uploadOfflineVideoResp: MutableLiveData<RequestState<UploadOfflineVideoResp>>
    ) {
        if (!internetConnected) {
            uploadOfflineVideoResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            uploadOfflineVideoResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.uploadOfflineVideos(body),
                baseView,
                uploadOfflineVideoResp
            )
        }
    }

    override fun getOfflineVideoList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        offlineUploadedVideoResp: MutableLiveData<RequestState<MutableList<OfflineUploadedVideoResp>>>
    ) {
        if (!internetConnected) {
            offlineUploadedVideoResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            offlineUploadedVideoResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getOfflinUploadedVideoCallList(),
                baseView,
                offlineUploadedVideoResp
            )
        }
    }

    override fun sendEndCallReq(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        sendEndcallReqresp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            sendEndcallReqresp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            sendEndcallReqresp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.sendEndCall(body),
                baseView,
                sendEndcallReqresp
            )
        }
    }

    override fun sendAcceptReqMed(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        acceptRejectCallByMediatorResp: MutableLiveData<RequestState<AcceptRejectCallByMediatorResp>>
    ) {
        if (!internetConnected) {
            acceptRejectCallByMediatorResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            acceptRejectCallByMediatorResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.acceptCallByMeditor(body),
                baseView,
                acceptRejectCallByMediatorResp
            )
        }
    }

    override fun deleteUploadOfflineVideo(
        id: Int,
        internetConnected: Boolean,
        baseView: BaseActivity,
        deleteUploadedOfflineVideoResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            deleteUploadedOfflineVideoResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
//            deleteUploadedOfflineVideoResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.deleteUploadedVideos(id),
                baseView,
                deleteUploadedOfflineVideoResp
            )
        }
    }

    override fun chnageUploadOfflineVideoStatus(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        chnageUploadedOfflineVideostatusResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            chnageUploadedOfflineVideostatusResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            chnageUploadedOfflineVideostatusResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.changeUploadStatus(body),
                baseView,
                chnageUploadedOfflineVideostatusResp
            )
        }
    }

    override fun drivingOffenceList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        drivingOffenceListResp: MutableLiveData<RequestState<MutableList<DrivingOffenceListResp>>>
    ) {
        if (!internetConnected) {
            drivingOffenceListResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            drivingOffenceListResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getDrivingOffenceList(),
                baseView,
                drivingOffenceListResp
            )
        }
    }

    override fun sendRejectReqMed(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        acceptRejectCallByMediatorResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            acceptRejectCallByMediatorResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            acceptRejectCallByMediatorResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.rejectCallByMeditor(body),
                baseView,
                acceptRejectCallByMediatorResp
            )
        }
    }

    override fun sendVideoCallRequestFromLawyerToUser(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        scheduleRequestedVideoCallFromLawyerToUserResp: MutableLiveData<RequestState<SendVideoCallReqResp>>
    ) {
        if (!internetConnected) {
            scheduleRequestedVideoCallFromLawyerToUserResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            scheduleRequestedVideoCallFromLawyerToUserResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.sendVideoCallRequestFromLawyer(body),
                baseView,
                scheduleRequestedVideoCallFromLawyerToUserResp
            )
        }
    }
}
