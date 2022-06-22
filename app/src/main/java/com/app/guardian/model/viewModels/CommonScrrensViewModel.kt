package com.app.guardian.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.guardian.common.ReusedMethod.Companion.getCurrentDate
import com.app.guardian.model.Chat.ChatListResp
import com.app.guardian.model.Chat.SendMessageResp
import com.app.guardian.model.CheckSub.CheckSubscriptionResp
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.HomeBanners.UserHomeBannerResp
import com.app.guardian.model.KnowYourRights.KnowYourRightsResp
import com.app.guardian.model.LawyerBySpecialization.LawyerBySpecializationResp
import com.app.guardian.model.ListFilter.FilterResp
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.RequestState
import com.app.guardian.model.cms.CMSResp
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.model.scheduleRequestedVideoCall.ScheduleRequestedVideoCallResp
import com.app.guardian.model.sendRequestVirtualWitness.SendRequestVirtualWitnessResp
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.repo.UserRepo
import com.app.guardian.utils.ApiConstant
import com.google.gson.JsonObject

class CommonScreensViewModel(private val mUserRepository: UserRepo) : ViewModel() {

    //For Common Resp
    private val commonResp = MutableLiveData<RequestState<CommonResponse>>()
    fun getCommonResp(): LiveData<RequestState<CommonResponse>> =
        commonResp

    //USER HOME BANNERS
    private val userBannerResp = MutableLiveData<RequestState<UserHomeBannerResp>>()
    fun getuserHomeBannerResp(): LiveData<RequestState<UserHomeBannerResp>> =
        userBannerResp

    fun getuserHomeBanners(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ) {


        mUserRepository.getuserHomeBanners(
            isInternetConnected,
            baseView,
            userBannerResp
        )
    }

    private val userDetailsResp = MutableLiveData<RequestState<UserDetailsResp>>()
    fun getuserDetailsResp(): LiveData<RequestState<UserDetailsResp>> = userDetailsResp

    fun getUserDetials(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ) {


        mUserRepository.getUserDetails(
            isInternetConnected,
            baseView,
            userDetailsResp
        )
    }

    private val knowYourRightsResp =
        MutableLiveData<RequestState<MutableList<KnowYourRightsResp>>>()

    fun getKnowYourRightsResp(): LiveData<RequestState<MutableList<KnowYourRightsResp>>> =
        knowYourRightsResp

    fun getKnowRights(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        city: String, state: String
    ) {

        val body = JsonObject()
        body.addProperty(ApiConstant.EXTRAS_CITY, city)
        body.addProperty(ApiConstant.EXTRAS_STATE, state)


        mUserRepository.getKnowYourRights(
            body,
            isInternetConnected,
            baseView,
            knowYourRightsResp
        )
    }

    ///Connected History API CALLING
    private val connectedHistoryResp =
        MutableLiveData<RequestState<MutableList<ConnectedHistoryResp>>>()

    fun getConenctedHistoryResp(): LiveData<RequestState<MutableList<ConnectedHistoryResp>>> =
        connectedHistoryResp

    fun getUserConnectedHistory(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        ser: String, type: String
    ) {

        val body = JsonObject()
        body.addProperty(ApiConstant.EXTRAS_SERCH, ser)
        body.addProperty(ApiConstant.EXTRAS_TYPE, type)


        mUserRepository.getUserConnectedHistory(
            body,
            isInternetConnected,
            baseView,
            connectedHistoryResp
        )
    }

    fun getLawyerConnectedHistory(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        ser: String,
        type: String,
    ) {

        val body = JsonObject()
        body.addProperty(ApiConstant.EXTRAS_SERCH, ser)
        body.addProperty(ApiConstant.EXTRAS_TYPE, type)

        mUserRepository.getLawyerConnectedHistory(
            body,
            isInternetConnected,
            baseView,
            connectedHistoryResp
        )
    }

    fun getMediatorConnectedHistory(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        ser: String,
    ) {

        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_SERCH, ser)



        mUserRepository.getMediatorConnectedHistory(
            body,
            isInternetConnected,
            baseView,
            connectedHistoryResp
        )
    }

    private val checkSubscriptionResp = MutableLiveData<RequestState<CheckSubscriptionResp>>()
    fun getcheckSubResp(): LiveData<RequestState<CheckSubscriptionResp>> =
        checkSubscriptionResp

    fun checkSubscritpion(
        isInternetConnected: Boolean,
        baseView: BaseActivity,

        ) {
        mUserRepository.checkSubscription(
            isInternetConnected,
            baseView,
            checkSubscriptionResp
        )
    }


    //GET FILTER DATA RESP
    private val filterResp = MutableLiveData<RequestState<FilterResp>>()
    fun getFilterResp(): LiveData<RequestState<FilterResp>> = filterResp

    fun getFilterData(
        isInternetConnected: Boolean,
        baseView: BaseActivity,

        ) {
        mUserRepository.getFilterData(
            isInternetConnected,
            baseView,
            filterResp
        )
    }

    //GET CMS DATA
    private val cmsResp = MutableLiveData<RequestState<CMSResp>>()
    fun getCMSResp(): LiveData<RequestState<CMSResp>> = cmsResp

    fun getCMSData(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ) {
        mUserRepository.cmsData(
            isInternetConnected,
            baseView,
            cmsResp
        )
    }

    //GET CHAT LIST
    private val chatListResp = MutableLiveData<RequestState<ChatListResp>>()
    fun getChatListResp(): LiveData<RequestState<ChatListResp>> = chatListResp

    fun getChatData(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        to_id: String
    ) {
        val body = JsonObject()
        body.addProperty(ApiConstant.EXTRAS_TO_ID, to_id)
        mUserRepository.getChatList(
            body,
            isInternetConnected,
            baseView,
            chatListResp
        )
    }

    //SEND CHAT MESSAGE
    private val sendMessageResp = MutableLiveData<RequestState<SendMessageResp>>()
    fun getSendChatResp(): LiveData<RequestState<SendMessageResp>> = sendMessageResp

    fun sendChatMessage(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        to_id: String,
        message: String,
        message_time: String,
        to_role: String,
    ) {
        val body = JsonObject()
        body.addProperty(ApiConstant.EXTRAS_TO_ID, to_id)
        body.addProperty(ApiConstant.EXTRAS_MESSAGE, message)
        body.addProperty(ApiConstant.EXTRAS_MESSAGE_TIME, message_time)
        body.addProperty(ApiConstant.EXTRAS_TO_ROLE, to_role)
        mUserRepository.sendMessage(
            body,
            isInternetConnected,
            baseView,
            sendMessageResp
        )
    }

    //GET LAWYER BY SPECIALIZATION LIST API
    private val lawyerBySpecializationResp =
        MutableLiveData<RequestState<MutableList<LawyerBySpecializationResp>>>()

    fun getLawyerBySpecializationListResp(): LiveData<RequestState<MutableList<LawyerBySpecializationResp>>> =
        lawyerBySpecializationResp

    fun getLawyerbySpecializationlist(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        ser: String,
        years_of_experience: String,
        specialization: String,
    ) {
        val body = JsonObject()
        body.addProperty(ApiConstant.EXTRAS_SERCH, ser)
        body.addProperty(ApiConstant.EXTRAS_YEARS_OF_EXP, years_of_experience)
        body.addProperty(ApiConstant.EXTRAS_SPECIALIZATION, specialization)
        mUserRepository.getLawyerBySpecialization(
            body,
            isInternetConnected,
            baseView,
            lawyerBySpecializationResp
        )
    }


    //SET USER STATUS API
    fun setAppUserStatus(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        is_online: String,
    ) {
        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_IS_ONLINE, is_online)
        body.addProperty(ApiConstant.EXTRAS_LAST_SEEN, getCurrentDate())

        mUserRepository.setUserStatus(
            body,
            isInternetConnected,
            baseView,
            commonResp
        )
    }

    //CALL SEND REQUEST VIRTUAL WITNESS
    private val sendRequestVirtualWitnessResp =
        MutableLiveData<RequestState<SendRequestVirtualWitnessResp>>()

    fun getSendRequestVirtualWitnessResp(): LiveData<RequestState<SendRequestVirtualWitnessResp>> =
        sendRequestVirtualWitnessResp

    fun SendRequestVirtualWitness(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        support_group_id: String,
        is_immediate_joining: Int,
        schedule_datetime: String,
    ) {
        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_SUPPORT_GROUP_ID, support_group_id)
        body.addProperty(ApiConstant.EXTRAS_IS_IMMEDIATE_ONLINE, is_immediate_joining)
        body.addProperty(ApiConstant.EXTRAS_SCHEDUAL_DATE_TIME, schedule_datetime)

        mUserRepository.sendRequestVirtualWitness(
            body,
            isInternetConnected,
            baseView,
            sendRequestVirtualWitnessResp
        )
    }

    //CALL SEND REQUEST VIRTUAL WITNESS
    private val scheduleRequestedVideoCallResp =
        MutableLiveData<RequestState<ScheduleRequestedVideoCallResp>>()

    fun getscheduleRequestedVideoCallResp(): LiveData<RequestState<ScheduleRequestedVideoCallResp>> =
        scheduleRequestedVideoCallResp

    fun ScheduleRequestedVideoCall(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        calling_history_id: Int,
        to_id: Int,
        to_role: String,
        url: String,
        schedule_datetime: String,
        room_id: String,
    ) {
        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_CALLING_HISTORY_ID, calling_history_id)
        body.addProperty(ApiConstant.EXTRAS_TO_ID, to_id)
        body.addProperty(ApiConstant.EXTRAS_TO_ROLE, to_role)
        body.addProperty(ApiConstant.EXTRAS_URL, url)
        body.addProperty(ApiConstant.EXTRAS_SCHEDUAL_DATE_TIME, schedule_datetime)
        body.addProperty(ApiConstant.EXTRAS_ROOM_ID, room_id)

        mUserRepository.scheduleRequestedVideoCall(
            body,
            isInternetConnected,
            baseView,
            scheduleRequestedVideoCallResp
        )
    }


}