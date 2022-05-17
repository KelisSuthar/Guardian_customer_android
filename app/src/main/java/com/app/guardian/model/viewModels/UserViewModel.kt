package com.app.guardian.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.model.LawyerProfileDetails.LawyerProfileDetailsResp
import com.app.guardian.model.RequestState
import com.app.guardian.model.SeekLegalAdviceResp.SeekLegalAdviceResp
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.repo.UserRepo

class UserViewModel(private val mUserRepository: UserRepo) : ViewModel(){

    //For Common Resp
    private val commonResponse = MutableLiveData<RequestState<CommonResponse>>()

    //LawyerList
    private  val lawyerListResp = MutableLiveData<RequestState<MutableList<LawyerListResp>>>()

    fun getLawyerList(): LiveData<RequestState<MutableList<LawyerListResp>>> =
        lawyerListResp

    fun lawyerList(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ){
        mUserRepository.getLawyerList(
            isInternetConnected,
            baseView,
            lawyerListResp
        )
    }

    //LawyerProfileDetails
    private val lawyerProfileDetails = MutableLiveData<RequestState<LawyerProfileDetailsResp>>()

    fun lawyerProfileDetails(): LiveData<RequestState<LawyerProfileDetailsResp>> = lawyerProfileDetails

    fun getLawyerProfileDetails(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        id: Int
    ){

        mUserRepository.getLawyerProfileDetails(
            isInternetConnected,
            baseView,
            lawyerProfileDetails,
            id
        )
    }

    //SeekLegalAdvice

    private  val seekLegalAdvice = MutableLiveData<RequestState<MutableList<SeekLegalAdviceResp>>>()

    fun seekLegalAdvice(): LiveData<RequestState<MutableList<SeekLegalAdviceResp>>> = seekLegalAdvice

    fun getSeekLegalAdvice(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        id: Int
    )
    {
        mUserRepository.getSeekLegalAdviceList(
            isInternetConnected,
            baseView,
            seekLegalAdvice,
            id
        )
    }
}