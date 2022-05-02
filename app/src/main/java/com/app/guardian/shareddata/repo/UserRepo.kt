package com.studelicious_user.shareddata.repo

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.studelicious_user.model.Property.Aminities.AminitiesResp
import com.studelicious_user.ReservedOrders.ReservedOrdersResp
import com.studelicious_user.model.CommonResponse
import com.studelicious_user.model.Home.Filter.HomeFilterResp
import com.studelicious_user.model.Home.HomeResp
import com.studelicious_user.model.ItemLIstingResp.RestItemListingResp
import com.studelicious_user.model.ListingResp.ListingResp
import com.studelicious_user.model.Login.LoginResp
import com.studelicious_user.model.MemberShip.MemberShipResp
import com.studelicious_user.model.Payment.PaymentCardResp
import com.studelicious_user.model.Property.PropertyResp
import com.studelicious_user.model.RequestState
import com.studelicious_user.model.Cart.CartItemResp
import com.studelicious_user.model.CategoriesResp
import com.studelicious_user.model.ItemLIstingResp.BusinessItemDetails.BusinessItemResp
import com.studelicious_user.model.ItemLIstingResp.DrinkItemListResp
import com.studelicious_user.model.ItemLIstingResp.GroceryItemListResp
import com.studelicious_user.model.apiKey.KeyData
import com.studelicious_user.model.settings.HelpResp

import com.studelicious_user.model.settings.TermsResp
import com.studelicious_user.model.settings.earn.EarnResp
import com.studelicious_user.model.settings.favlist.FavouriteResp
import com.studelicious_user.model.settings.notification.NotifiactionResp
import com.studelicious_user.model.settings.orders.MyOrderResp
import com.studelicious_user.model.settings.property.PropertyListingResp
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


    fun getHomeList(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callback: MutableLiveData<RequestState<HomeResp>>
    )

    fun getPaymentCardList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        callback: MutableLiveData<RequestState<PaymentCardResp>>
    )

    fun addPaymentCard(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callback: MutableLiveData<RequestState<CommonResponse>>
    )

    fun editPaymentCard(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callback: MutableLiveData<RequestState<CommonResponse>>
    )

    fun deletePaymentCard(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callback: MutableLiveData<RequestState<CommonResponse>>
    )

    fun sendForgotPassData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDForgotPassRequest: MutableLiveData<RequestState<CommonResponse>>
    )

    fun sendOTPData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDVerifyOTPRequest: MutableLiveData<RequestState<CommonResponse>>
    )

    fun sendNewPass(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDNewPassRequest: MutableLiveData<RequestState<CommonResponse>>
    )

    fun getMemberShipList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDMemberShipListRequest: MutableLiveData<RequestState<MemberShipResp>>
    )

    fun socialLogin(
        signInJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDSignInRequest: MutableLiveData<RequestState<LoginResp>>
    )

    fun sendResendOTPData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDVerifyOTPRequest: MutableLiveData<RequestState<CommonResponse>>
    )

    fun sendMobileOTPData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDVerifyOTPRequest: MutableLiveData<RequestState<CommonResponse>>
    )

    fun getHomeFilterList(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDHomeDataRequest: MutableLiveData<RequestState<HomeFilterResp>>
    )

    fun getFreeTrialData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDCommonResponse: MutableLiveData<RequestState<LoginResp>>
    )

    fun getListingData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        listingResp: MutableLiveData<RequestState<ListingResp>>
    )

    fun addProperty(
        multipartBody: MultipartBody,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponce: MutableLiveData<RequestState<CommonResponse>>
    )

    fun getAminitiesList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        aminiteisResponse: MutableLiveData<RequestState<AminitiesResp>>
    )

    fun getAccomodationByID(
        jsonObject: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        aminiteisResponse: MutableLiveData<RequestState<PropertyResp>>
    )

    fun editProperty(
        build: MultipartBody,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponce: MutableLiveData<RequestState<CommonResponse>>
    )

    fun bookPropertyData(
        jsonObject: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        aminiteisResponse: MutableLiveData<RequestState<CommonResponse>>
    )

    fun applyJob(
        jsonObject: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponce: MutableLiveData<RequestState<CommonResponse>>
    )



    fun getRestItemListData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        restItemListingResp: MutableLiveData<RequestState<RestItemListingResp>>
    )

    fun getFavData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<CommonResponse>>
    )

    fun addCartItemData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<CartItemResp>>
    )

    fun getCartData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<CartItemResp>>
    )

    fun getReserveOrderData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<ReservedOrdersResp>>
    )

    fun removeCartItemData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<CartItemResp>>
    )

    fun placeOrder(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<CommonResponse>>
    )

    fun getItemDetailsData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        BusinessItemResp: MutableLiveData<RequestState<BusinessItemResp>>
    )

    fun getDrinkItemListData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        drinkResponse: MutableLiveData<RequestState<DrinkItemListResp>>
    )

    fun getGroceryItemListData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        groceryItemListingResp: MutableLiveData<RequestState<GroceryItemListResp>>
    )



    fun getJobListWithFilter(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        jobItemListingResp: MutableLiveData<RequestState<ListingResp>>
    )



    fun buyPlanData(
        jsonObject: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDCommonResponse: MutableLiveData<RequestState<CommonResponse>>
    )

    fun placeMealplanDrinks(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<CommonResponse>>
    )

    fun updateProfile(
        json: MultipartBody,
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDSignInRequest: MutableLiveData<RequestState<LoginResp>>
    )

    fun profileDetails(
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDSignInRequest: MutableLiveData<RequestState<LoginResp>>
    )

    fun getTermsData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        aboutusResponse: MutableLiveData<RequestState<TermsResp>>
    )

    fun getHelpData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        helpResponse: MutableLiveData<RequestState<HelpResp>>
    )

    fun getAboutUsData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        aboutusResponse: MutableLiveData<RequestState<TermsResp>>
    )

    fun getFavouriteList(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        favouriteDataRequest: MutableLiveData<RequestState<FavouriteResp>>
    )

    fun getNotificationList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        notificationDataRequest: MutableLiveData<RequestState<NotifiactionResp>>
    )

    fun logout(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun changePass(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun getOrderList(
        Json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        orderResp: MutableLiveData<RequestState<MyOrderResp>>
    )

    fun getProperties(
        internetConnected: Boolean,
        baseView: BaseActivity,
        propertyListsResp: MutableLiveData<RequestState<PropertyListingResp>>
    )

    fun getEarnData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        earnDataResp: MutableLiveData<RequestState<EarnResp>>
    )

    fun addReviewsData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        earnDataResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun getCategoriesList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        categoryResp: MutableLiveData<RequestState<CategoriesResp>>
    )

    fun acceptRejectAccomodationApplication(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun property_id(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

    fun addAppReview(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    )

}

