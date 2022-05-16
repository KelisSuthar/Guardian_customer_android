package com.app.guardian.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.RequestState
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.repo.UserRepo

class CommonScreensViewModel(private val mUserRepository: UserRepo) : ViewModel() {

    //For Common Resp
    private val commonResp = MutableLiveData<RequestState<CommonResponse>>()


    private val userBannerResp = MutableLiveData<RequestState<MutableList<UserHomeBannerResp>>>()
    fun getuserHomeBannerResp(): LiveData<RequestState<MutableList<UserHomeBannerResp>>> = userBannerResp

    fun getuserHomeBanners(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ) {


        mUserRepository. getuserHomeBanners(
            isInternetConnected,
            baseView,
            userBannerResp
        )
    }
}