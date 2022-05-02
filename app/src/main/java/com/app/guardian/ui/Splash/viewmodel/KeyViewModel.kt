package com.app.guardian.ui.Splash.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.guardian.model.RequestState
import com.app.guardian.model.apiKey.KeyData
import com.app.guardian.shareddata.base.BaseActivity
import com.google.gson.JsonObject


import com.studelicious_user.shareddata.repo.UserRepo


class KeyViewModel(private val mUserRepository: UserRepo) : ViewModel() {
    private val mLDKeyRequest = MutableLiveData<RequestState<KeyData>>()
    fun getKeyRequest(): LiveData<RequestState<KeyData>> = mLDKeyRequest

    fun getKey(isInternetConnected: Boolean,
               baseView: BaseActivity
    ) {
        val key = JsonObject()
        mUserRepository.getKey(
                key,
                isInternetConnected,
                baseView,
                mLDKeyRequest
        )
    }
}