package com.app.guardian.shareddata.endpoint

import com.app.guardian.model.Chat.ChatListResp
import com.app.guardian.model.Chat.SendMessageResp
import com.app.guardian.model.CheckSub.CheckSubscriptionResp
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.CommonResponseModel
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.ForgotPass.ForgotPassResp
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
import com.app.guardian.model.Radar.RadarListResp
import com.app.guardian.model.SeekLegalAdviceResp.SeekLegalAdviceResp
import com.app.guardian.model.SignUp.SignupResp
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.app.guardian.model.SupportGroup.SupportGroupResp
import com.app.guardian.model.cms.CMSResp
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.model.sendRequestVirtualWitness.SendRequestVirtualWitnessResp
import com.app.guardian.model.specializationList.SpecializationListResp
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*


interface ApiEndPoint {
//    @GET("apikey")
//    fun getKey(): Call<CommonResponseModel<KeyData>>

    @POST("signUp")
    fun doSignUp(@Body jsonObject: JsonObject): Call<CommonResponseModel<SignupResp>>

    @POST("signIn")
    fun doLogIn(@Body jsonObject: JsonObject): Call<CommonResponseModel<LoginResp>>

    @POST("forgotPassword")
    fun forGotPass(@Body jsonObject: JsonObject): Call<CommonResponseModel<ForgotPassResp>>

    @POST("verifyFPOTP")
    fun verifyOTP(@Body jsonObject: JsonObject): Call<CommonResponseModel<User>>

    @POST("resetPassword")
    fun resetPass(@Body jsonObject: JsonObject): Call<CommonResponseModel<MutableList<CommonResponse>>>

    @PUT("changePassword")
    fun changePass(@Body jsonObject: JsonObject): Call<CommonResponseModel<MutableList<CommonResponse>>>

    @POST("signOut")
    fun signOut(): Call<CommonResponseModel<LoginResp>>

    @GET("getPricing")
    fun getSubscriptionPlan(): Call<CommonResponseModel<MutableList<SubscriptionPlanResp>>>

    @POST("addSubscription")
    fun buysubscribePlan(@Body jsonObject: JsonObject): Call<CommonResponseModel<CommonResponse>>

    @GET("getUserHomeBanner")
    fun getUserHomeBanners(): Call<CommonResponseModel<UserHomeBannerResp>>

    @GET("getUserDetail")
    fun getUserDetails(): Call<CommonResponseModel<UserDetailsResp>>

    @PUT("updateUserProfile")
    fun updateUserProfile(): Call<CommonResponseModel<CommonResponse>>

    @POST("getLawyerList")
    fun getLawyerList(@Body jsonObject: JsonObject): Call<CommonResponseModel<MutableList<LawyerListResp>>>

    @GET("lawyerDetail/{id}")
    fun getLawyerProfileDetails(@Path("id") id: Int): Call<CommonResponseModel<LawyerProfileDetailsResp>>

    @GET("lawyerSeekLegalAdviceList/{id}")
    fun getSeekLegalAdvice(@Path("id") id: Int): Call<CommonResponseModel<MutableList<SeekLegalAdviceResp>>>

    @POST("addSeekLegalAdvice")
    fun addSeekLegalAdvice(@Body body: JsonObject): Call<CommonResponseModel<CommonResponse>>

    @PUT("updateSeekLegalAdvice")
    fun updateSeekLegalAdvice(@Body body: JsonObject): Call<CommonResponseModel<CommonResponse>>

    @DELETE("deleteSeekLegalAdvice/{id}")
    fun deleteSeekLegalAdvice(@Path("id") id: Int): Call<CommonResponseModel<CommonResponse>>

    @POST("getUserRights")
    fun getKnowYourRights(@Body body: JsonObject): Call<CommonResponseModel<MutableList<KnowYourRightsResp>>>

    @POST("userContactHistory")
    fun getuserContactHistory(@Body body: JsonObject): Call<CommonResponseModel<MutableList<ConnectedHistoryResp>>>

    @POST("lawyerContactHistory")
    fun getlawyerContactHistory(@Body body: JsonObject): Call<CommonResponseModel<MutableList<ConnectedHistoryResp>>>

    @POST("mediatorContactHistory")
    fun getmediatorContactHistory(@Body body: JsonObject): Call<CommonResponseModel<MutableList<ConnectedHistoryResp>>>

    @GET("checkUserSubscription")
    fun checkSubscriptions(): Call<CommonResponseModel<CheckSubscriptionResp>>

    @GET("getBannerSubscriptionPlan")
    fun getLawyerSubscriptionPlan(): Call<CommonResponseModel<MutableList<SubscriptionPlanResp>>>

    @POST("addBannerSubscription")
    fun addBannerSubscription(@Body body: JsonObject): Call<CommonResponseModel<CommonResponse>>

    @POST("addBanner")
    fun addBanner(@Body body: JsonObject): Call<CommonResponseModel<CommonResponse>>

    @GET("getFilterData")
    fun getFilterListData(): Call<CommonResponseModel<FilterResp>>

    @GET("getSpecializationList")
    fun getSpecializationList(): Call<CommonResponseModel<MutableList<SpecializationListResp>>>

    @GET("getCMS")
    fun getCMSData(): Call<CommonResponseModel<CMSResp>>

    @PUT("updatePhoneOtpVerify")
    fun updatePhoneOtpVerify(@Body body: JsonObject): Call<CommonResponseModel<MutableList<CommonResponse>>>//after user change the phone number in edit profile

    @GET("getSupportGroup")
    fun getSupportGroup(): Call<CommonResponseModel<MutableList<SupportGroupResp>>>

    @POST("sendRequestVirtualWitness")
    fun sendRequestVirtualWitness(@Body body: JsonObject): Call<CommonResponseModel<SendRequestVirtualWitnessResp>>

    @GET("getNotificationList")
    fun getNotifications(): Call<CommonResponseModel<MutableList<NotificationResp>>>

    @DELETE("deleteNotification/{id}")
    fun deleteNotification(@Path("id") id: Int): Call<CommonResponseModel<CommonResponse>>

    @POST("lawyerBySpecialization")
    fun getLawyerBySpecialization(@Body body: JsonObject): Call<CommonResponseModel<MutableList<LawyerBySpecializationResp>>>

    @GET("listRadarMap")
    fun getRadarMapList(
        @Query("lat") lat: String,
        @Query("lng") lng: String,
    ): Call<CommonResponseModel<MutableList<RadarListResp>>>

    @POST("deleteRadarMap")
    fun deleteRadarMapPoint(@Body body: JsonObject): Call<CommonResponseModel<RadarListResp>>

    @POST("addRadarMap")
    fun addRadarMapPoint(@Body body: JsonObject): Call<CommonResponseModel<RadarListResp>>

    @POST("listUserChat")
    fun getChatList(@Body body: JsonObject): Call<CommonResponseModel<ChatListResp>>

    @POST("addUserChat")
    fun sendMessageChat(@Body body: JsonObject): Call<CommonResponseModel<SendMessageResp>>

    @POST("updateOnlineStatus")
    fun setAppUserStatus(@Body body: JsonObject): Call<CommonResponseModel<CommonResponse>>

    @POST("sendCallingRequestToMediator")
    fun sendCallingRequestToMediator(@Body body: JsonObject): Call<CommonResponseModel<MediatorCallReqResp>>



}