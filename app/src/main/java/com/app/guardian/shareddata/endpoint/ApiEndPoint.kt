package com.app.guardian.shareddata.endpoint

import com.app.guardian.model.CommonResponseModel
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.apiKey.KeyData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiEndPoint {
    @GET("apikey")
    fun getKey(): Call<CommonResponseModel<KeyData>>

    @POST("createAccount")
    fun doSignIn(@Body jsonObject: JsonObject): Call<CommonResponseModel<LoginResp>>

    @POST("signIn")
    fun doLogIn(@Body jsonObject: JsonObject): Call<CommonResponseModel<LoginResp>>

    @POST("forgotPassword")
    fun forGotPass(@Body jsonObject: JsonObject): Call<CommonResponseModel<LoginResp>>

    @POST("verifyFPOTP")
    fun verifyOTP(@Body jsonObject: JsonObject): Call<CommonResponseModel<LoginResp>>

    @POST("resetPassword")
    fun resetPass(@Body jsonObject: JsonObject): Call<CommonResponseModel<LoginResp>>

    @POST("signOut")
    fun signOut(@Body jsonObject: JsonObject): Call<CommonResponseModel<LoginResp>>



}