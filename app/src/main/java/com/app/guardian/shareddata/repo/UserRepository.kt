package com.studelicious_user.shareddata.repo

import androidx.lifecycle.MutableLiveData
import com.app.guardian.model.ApiError
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.RequestState
import com.app.guardian.model.apiKey.KeyData
import com.app.guardian.shareddata.BaseView
import com.app.guardian.shareddata.endpoint.ApiEndPoint
import com.app.guardian.shareddata.networkmanager.NetworkManager
import com.app.guardian.shareddata.repo.UserRepo
import com.app.guardian.utils.Config
import com.google.gson.JsonObject


class UserRepository(private val mApiEndPoint: ApiEndPoint) : UserRepo {

    override fun getKey(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseView,
        callbackKey: MutableLiveData<RequestState<KeyData>>
    ) {
        if (!isInternetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getKey(),
                baseView, callbackKey
            )
        }
    }

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
        callbackKey: MutableLiveData<RequestState<LoginResp>>
    ) {
        if (!isInternetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.doSignIn(body), baseView, callbackKey)
        }
    }

}
