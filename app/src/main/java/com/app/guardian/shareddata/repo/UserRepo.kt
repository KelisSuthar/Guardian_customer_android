package com.app.guardian.shareddata.repo

import androidx.lifecycle.MutableLiveData
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.RequestState
import com.app.guardian.model.apiKey.KeyData
import com.google.gson.JsonObject
import com.app.guardian.shareddata.BaseView
import com.app.guardian.shareddata.base.BaseActivity
import okhttp3.MultipartBody

interface UserRepo {
    fun getKey(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseView,
        callback: MutableLiveData<RequestState<KeyData>>
    )

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
        callback: MutableLiveData<RequestState<LoginResp>>
    )

}

