package com.app.guardian.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.RequestState
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.repo.UserRepo
import com.app.guardian.utils.ApiConstant
import com.google.gson.JsonObject

class LawyerViewModel(private val mUserRepository: UserRepo) : ViewModel() {

    //For Common Resp
    private val commonResponse = MutableLiveData<RequestState<CommonResponse>>()
    fun getCommonResp(): LiveData<RequestState<CommonResponse>> = commonResponse

    fun addSeekLegalAdvice(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        title: String,
        desc: String,
    ) {
        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_TITLE, title)
        body.addProperty(ApiConstant.EXTRAS_DESC, desc)


        mUserRepository.addSeekLegalAdvice(
            body,
            isInternetConnected,
            baseView,
            commonResponse
        )
    }

    fun updateSeekLegalAdvice(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        title: String,
        desc: String,
        seek_legal_advice_id: Int,
    ) {
        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_TITLE, title)
        body.addProperty(ApiConstant.EXTRAS_DESC, desc)
        body.addProperty(ApiConstant.EXTRAS_SEEK_LEGAL_ADV, seek_legal_advice_id)
        mUserRepository.updateSeekLegalAdvice(
            body,
            isInternetConnected,
            baseView,
            commonResponse
        )
    }

    fun deleteSeekLegalAdvice(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        id: Int?
    ) {
        mUserRepository.deleteSeekLegalAdvice(
            isInternetConnected,
            baseView,
            commonResponse,
            id!!
        )
    }

}