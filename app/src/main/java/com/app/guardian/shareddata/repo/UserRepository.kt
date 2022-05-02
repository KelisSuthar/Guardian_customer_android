package com.studelicious_user.shareddata.repo

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.studelicious_user.model.Property.Aminities.AminitiesResp
import com.studelicious_user.ReservedOrders.ReservedOrdersResp
import com.studelicious_user.model.ApiError
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
import com.app.guardian.shareddata.endpoint.ApiEndPoint
import com.studelicious_user.shareddata.networkmanager.NetworkManager
import com.studelicious_user.utils.Config
import okhttp3.MultipartBody


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

    override fun sendForgotPassData(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!isInternetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.forgotPassword(body), baseView, callbackKey)
        }
    }

    override fun sendOTPData(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!isInternetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.verifyForgotPassOtp(body),
                baseView,
                callbackKey
            )
        }
    }

    override fun sendNewPass(
        body: JsonObject,
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!isInternetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.changeForgotPass(body), baseView, callbackKey)
        }
    }

    override fun getMemberShipList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<MemberShipResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getMemberShipPlans(), baseView, callbackKey)
        }
    }

    override fun socialLogin(
        signInJson: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<LoginResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.doLogIn(signInJson), baseView, callbackKey)
        }
    }

    override fun sendResendOTPData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.resend_Mobile_OTP(json), baseView, callbackKey)
        }
    }

    override fun sendMobileOTPData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.verifyMobileOtp(json), baseView, callbackKey)
        }
    }

    override fun getHomeFilterList(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<HomeFilterResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getFilterHomeData(json), baseView, callbackKey)
        }
    }

    override fun getFreeTrialData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<LoginResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.buyFreeTrail(), baseView, callbackKey)
        }
    }

    override fun getListingData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<ListingResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getAllBusiness(json), baseView, callbackKey)
        }
    }

    override fun getRestItemListData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<RestItemListingResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getBusinessDetails(json), baseView, callbackKey)
        }
    }

    override fun getFavData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getAddRemoveFavBusiness(json),
                baseView,
                callbackKey
            )
        }
    }

    override fun addCartItemData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CartItemResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.addCartDetails(json), baseView, callbackKey)
        }
    }

    override fun removeCartItemData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CartItemResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.removeCartDetails(json), baseView, callbackKey)
        }
    }

    override fun placeOrder(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.placeOrder(json), baseView, callbackKey)
        }
    }

    override fun updateProfile(
        json: MultipartBody,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<LoginResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.editProfile(json), baseView, callbackKey)
        }
    }

    override fun profileDetails(
        internetConnected: Boolean,
        baseView: BaseActivity,
        mLDSignInRequest: MutableLiveData<RequestState<LoginResp>>
    ) {
        if (!internetConnected) {
            mLDSignInRequest.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            mLDSignInRequest.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getUserProfileDetails(),
                baseView,
                mLDSignInRequest
            )
        }
    }

    override fun getTermsData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        aboutusResponse: MutableLiveData<RequestState<TermsResp>>
    ) {
        if (!internetConnected) {
            aboutusResponse.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            aboutusResponse.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getTermsAndConditions(),
                baseView,
                aboutusResponse
            )
        }
    }

    override fun getHelpData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        helpResponse: MutableLiveData<RequestState<HelpResp>>
    ) {
        if (!internetConnected) {
            helpResponse.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            helpResponse.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getHelpData(), baseView, helpResponse)
        }
    }

    override fun getAboutUsData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        aboutusResponse: MutableLiveData<RequestState<TermsResp>>
    ) {
        if (!internetConnected) {
            aboutusResponse.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            aboutusResponse.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getAboutUs(), baseView, aboutusResponse)
        }
    }

    override fun getFavouriteList(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        favouriteDataRequest: MutableLiveData<RequestState<FavouriteResp>>
    ) {
        if (!internetConnected) {
            favouriteDataRequest.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            favouriteDataRequest.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getFavList(json),
                baseView,
                favouriteDataRequest
            )
        }
    }

    override fun getNotificationList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        notificationDataRequest: MutableLiveData<RequestState<NotifiactionResp>>
    ) {
        if (!internetConnected) {
            notificationDataRequest.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            notificationDataRequest.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getNotificationList(),
                baseView,
                notificationDataRequest
            )
        }
    }


    override fun changePass(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.changePassword(json), baseView, commonResp)
        }
    }

    override fun getOrderList(
        Json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        orderResp: MutableLiveData<RequestState<MyOrderResp>>
    ) {
        if (!internetConnected) {
            orderResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            orderResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getOrdersList(Json), baseView, orderResp)
        }
    }

    override fun getProperties(
        internetConnected: Boolean,
        baseView: BaseActivity,
        propertyListsResp: MutableLiveData<RequestState<PropertyListingResp>>
    ) {
        if (!internetConnected) {
            propertyListsResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            propertyListsResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getPropertiesList(),
                baseView,
                propertyListsResp
            )
        }
    }

    override fun getEarnData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        earnDataResp: MutableLiveData<RequestState<EarnResp>>
    ) {
        if (!internetConnected) {
            earnDataResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            earnDataResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getEarnData(), baseView, earnDataResp)
        }
    }

    override fun addReviewsData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.addReviews(json), baseView, commonResp)
        }
    }

    override fun getItemDetailsData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<BusinessItemResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getBusinessItemDetails(json),
                baseView,
                callbackKey
            )
        }
    }

    override fun getDrinkItemListData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        drinkResponse: MutableLiveData<RequestState<DrinkItemListResp>>
    ) {
        if (!internetConnected) {
            drinkResponse.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            drinkResponse.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getDrinkDetails(json), baseView, drinkResponse)
        }
    }

    override fun getGroceryItemListData(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        groceryItemListingResp: MutableLiveData<RequestState<GroceryItemListResp>>
    ) {
        if (!internetConnected) {
            groceryItemListingResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            groceryItemListingResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getGroceryDetails(json),
                baseView,
                groceryItemListingResp
            )
        }
    }

    override fun getJobListWithFilter(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        jobItemListingResp: MutableLiveData<RequestState<ListingResp>>
    ) {
        if (!internetConnected) {
            jobItemListingResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            jobItemListingResp.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getJobListWithFilter(json),
                baseView,
                jobItemListingResp
            )
        }
    }

    override fun getCartData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CartItemResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.geCartDetails(), baseView, callbackKey)
        }
    }

    override fun getReserveOrderData(
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<ReservedOrdersResp>>
    ) {
        if (!internetConnected) {
            callbackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callbackKey.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getReservedOrder(), baseView, callbackKey)
        }
    }

    override fun addProperty(
        multipartBody: MultipartBody,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponce: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResponce.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResponce.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.addProperty(multipartBody),
                baseView,
                commonResponce
            )
        }
    }

    override fun getAminitiesList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        aminiteisResponse: MutableLiveData<RequestState<AminitiesResp>>
    ) {
        if (!internetConnected) {
            aminiteisResponse.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            aminiteisResponse.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.getAminitesList(), baseView, aminiteisResponse)
        }
    }

    override fun getAccomodationByID(
        jsonObject: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callBackKey: MutableLiveData<RequestState<PropertyResp>>
    ) {
        if (!internetConnected) {
            callBackKey.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            callBackKey.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.getPropertyDetailsById(jsonObject),
                baseView,
                callBackKey
            )
        }
    }

    override fun editProperty(
        build: MultipartBody,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponce: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResponce.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResponce.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.editProperty(build), baseView, commonResponce)
        }
    }

    override fun bookPropertyData(
        jsonObject: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponce: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResponce.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResponce.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.bookProperty(jsonObject),
                baseView,
                commonResponce
            )
        }
    }

    override fun applyJob(
        jsonObject: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponce: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResponce.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResponce.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.applyForJob(jsonObject),
                baseView,
                commonResponce
            )
        }
    }


    override fun getHomeList(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<HomeResp>>
    ) {
        callbackKey.value = RequestState(progress = true)
        NetworkManager.requestData(mApiEndPoint.getHomedata(json), baseView, callbackKey)
    }

    override fun getPaymentCardList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<PaymentCardResp>>
    ) {
        callbackKey.value = RequestState(progress = true)
        NetworkManager.requestData(mApiEndPoint.getPaymentCardList(), baseView, callbackKey)
    }

    override fun addPaymentCard(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        callbackKey.value = RequestState(progress = true)
        NetworkManager.requestData(mApiEndPoint.addPaymentCard(json), baseView, callbackKey)
    }

    override fun editPaymentCard(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        callbackKey.value = RequestState(progress = true)
        NetworkManager.requestData(mApiEndPoint.editPaymentCardList(json), baseView, callbackKey)
    }

    override fun deletePaymentCard(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        callbackKey: MutableLiveData<RequestState<CommonResponse>>
    ) {
        callbackKey.value = RequestState(progress = true)
        NetworkManager.requestData(mApiEndPoint.deletePaymentCardList(json), baseView, callbackKey)
    }

    override fun logout(
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.logout(), baseView, commonResp)
        }
    }

    override fun buyPlanData(
        jsonObject: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {

        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.buyPlan(jsonObject), baseView, commonResp)
        }
    }

    override fun placeMealplanDrinks(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResponse: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResponse.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResponse.value = RequestState(progress = true)
            NetworkManager.requestData(
                mApiEndPoint.meaplanDrinksOrderPlace(json),
                baseView,
                commonResponse
            )
        }
    }

    override fun getCategoriesList(
        internetConnected: Boolean,
        baseView: BaseActivity,
        categoryResp: MutableLiveData<RequestState<CategoriesResp>>
    ) {
        if (!internetConnected) {
            categoryResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            categoryResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.jobCategories(), baseView, categoryResp)
        }
    }

    override fun acceptRejectAccomodationApplication(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.acceptRejectRenter(json), baseView, commonResp)
        }
    }

    override fun property_id(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.deleteProperty(json), baseView, commonResp)
        }
    }

    override fun addAppReview(
        json: JsonObject,
        internetConnected: Boolean,
        baseView: BaseActivity,
        commonResp: MutableLiveData<RequestState<CommonResponse>>
    ) {
        if (!internetConnected) {
            commonResp.value =
                RequestState(progress = false, error = ApiError(Config.NETWORK_ERROR, null))
        } else {
            commonResp.value = RequestState(progress = true)
            NetworkManager.requestData(mApiEndPoint.addAppReview(json), baseView, commonResp)
        }
    }



}
