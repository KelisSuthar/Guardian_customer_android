package com.app.guardian.shareddata.endpoint

import com.app.guardian.model.CommonResponse
import com.app.guardian.model.CommonResponseModel
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.ForgotPass.ForgotPassResp
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.model.LawyerProfileDetails.LawyerProfileDetailsResp
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.Login.User
import com.app.guardian.model.SeekLegalAdviceResp.SeekLegalAdviceResp
import com.app.guardian.model.SignUp.SignupResp
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @POST("signOut")
    fun signOut(): Call<CommonResponseModel<LoginResp>>

    @GET("getPricing")
    fun getSubscriptionPlan(): Call<CommonResponseModel<MutableList<SubscriptionPlanResp>>>

    @POST("addSubscription")
    fun buysubscribePlan(@Body jsonObject: JsonObject): Call<CommonResponseModel<CommonResponse>>

    @GET("getUserHomeBanner")
    fun getUserHomeBanners(): Call<CommonResponseModel<MutableList<UserHomeBannerResp>>>

    @GET("getUserDetail")
    fun getUserDetails(): Call<CommonResponseModel<UserDetailsResp>>

    @PUT("updateUserProfile")
    fun updateUserProfile(): Call<CommonResponseModel<CommonResponse>>

    @GET("getLawyerList")
    fun getLawyerList(): Call<CommonResponseModel<MutableList<LawyerListResp>>>

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


}