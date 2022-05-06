package com.app.guardian.shareddata.endpoint

import com.app.guardian.model.CommonResponse
import com.app.guardian.model.CommonResponseModel
import com.app.guardian.model.ForgotPass.ForgotPassResp
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.SignUp.SignupResp
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiEndPoint {
//    @GET("apikey")
//    fun getKey(): Call<CommonResponseModel<KeyData>>

    @POST("createAccount")
    fun doSignUp(@Body jsonObject: JsonObject): Call<CommonResponseModel<SignupResp>>

    @POST("signIn")
    fun doLogIn(@Body jsonObject: JsonObject): Call<CommonResponseModel<LoginResp>>

    @POST("forgotPassword")
    fun forGotPass(@Body jsonObject: JsonObject): Call<CommonResponseModel<ForgotPassResp>>

    @POST("verifyFPOTP")
    fun verifyOTP(@Body jsonObject: JsonObject): Call<CommonResponseModel<CommonResponse>>

    @POST("resetPassword")
    fun resetPass(@Body jsonObject: JsonObject): Call<CommonResponseModel<CommonResponse>>

    @POST("signOut")
    fun signOut(): Call<CommonResponseModel<LoginResp>>

    @GET("getPricing")
    fun getSubscriptionPlan(): Call<CommonResponseModel<MutableList<SubscriptionPlanResp>>>

    @POST("addSubscription")
    fun buysubscribePlan(@Body jsonObject: JsonObject): Call<CommonResponseModel<CommonResponse>>


}