package com.app.guardian.shareddata.repo

import androidx.lifecycle.MutableLiveData
import com.app.guardian.model.AcceptRejectCallByMediator.AcceptRejectCallByMediatorResp
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

    fun getLawyerList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        lawyerResp: MutableLiveData<RequestState<MutableList<LawyerListResp>>>
    )

    fun getuserHomeBanners(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<UserHomeBannerResp>>
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
        commonResp: MutableLiveData<RequestState<CMSResp>>
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

    fun getNotification(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<NotificationResp>>>
    )

    fun deleteNotification(
        id: Int,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun sendRequestVirtualWitness(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<SendRequestVirtualWitnessResp>>
    )

    fun getLawyerBySpecialization(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<LawyerBySpecializationResp>>>
    )

    fun addRadarMapPoint(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<RadarListResp>>
    )

    fun deleteRadarMapPoint(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<RadarListResp>>
    )

    fun getRadarMapList(
        LAT: String,
        LONG: String,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<MutableList<RadarListResp>>>
    )


    fun getChatList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<ChatListResp>>
    )

    fun sendMessage(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        sendMessageResp: MutableLiveData<RequestState<SendMessageResp>>
    )

    fun setUserStatus(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun sendCallingRequestToMediator(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<MediatorCallReqResp>>
    )

    fun scheduleRequestedVideoCall(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<ScheduleRequestedVideoCallResp>>
    )

    fun editprofile(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        loginResp: MutableLiveData<RequestState<UserDetailsResp>>
    )

    fun getVideoCallRequestUserList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        videoCallRequestListResp: MutableLiveData<RequestState<MutableList<VideoCallRequestListResp>>>
    )

    fun getVideoCallRequestLawyerList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        videoCallRequestListResp: MutableLiveData<RequestState<MutableList<VideoCallRequestListResp>>>
    )

    fun getVideoCallRequestMediatorList(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        videoCallRequestListResp: MutableLiveData<RequestState<MutableList<VideoCallRequestListResp>>>
    )

    fun askModeQuestion(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        askModeQResp: MutableLiveData<RequestState<AskModeQResp>>
    )

    fun sendVideoCallRequest(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        sendVideocallReqresp: MutableLiveData<RequestState<SendVideoCallReqResp>>
    )

    fun uploadOfflineVideo(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        uploadOfflineVideoResp: MutableLiveData<RequestState<UploadOfflineVideoResp>>
    )

    fun getOfflineVideoList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        offlineUploadedVideoResp: MutableLiveData<RequestState<MutableList<OfflineUploadedVideoResp>>>
    )

    fun sendEndCallReq(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        sendEndcallReqresp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun sendAcceptReqMed(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        acceptRejectCallByMediatorResp: MutableLiveData<RequestState<AcceptRejectCallByMediatorResp>>
    )

    fun deleteUploadOfflineVideo(
        id: Int,
        internetConnected: Boolean,
        baseView: BaseActivity,
        deleteUploadedOfflineVideoResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun chnageUploadOfflineVideoStatus(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        chnageUploadedOfflineVideostatusResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun drivingOffenceList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        drivingOffenceListResp: MutableLiveData<RequestState<MutableList<DrivingOffenceListResp>>>
    )

    fun sendRejectReqMed(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        acceptRejectCallByMediatorResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun sendVideoCallRequestFromLawyerToUser(
        body: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        scheduleRequestedVideoCallFromLawyerToUserResp: MutableLiveData<RequestState<SendVideoCallReqResp>>
    )


}

