package com.app.guardian.model.viewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.ForgotPass.ForgotPassResp
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.RequestState
import com.app.guardian.model.SignUp.SignupResp
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.repo.UserRepo
import com.app.guardian.utils.ApiConstant
import com.google.gson.JsonArray
import com.google.gson.JsonObject

//TODO-AuthenticationViewModel(Signup,login,forgotpass,resetpass,logut,subscriptions)
class AuthenticationViewModel(private val mUserRepository: UserRepo) : ViewModel() {

    //For Common Resp
    private val CommonResponse = MutableLiveData<RequestState<CommonResponse>>()

    //For Login Resp
    private val LoginResp = MutableLiveData<RequestState<LoginResp>>()


    //Login API CALLING
    fun getLoginResp(): LiveData<RequestState<LoginResp>> = LoginResp

    fun Login(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        isEmail: Boolean,
        email_phone: String,
        pass: String,
    ) {
        val signInJson = JsonObject()
        if (isEmail) {
            signInJson.addProperty(ApiConstant.EXTRAS_EMAIL, email_phone)
        } else {
            signInJson.addProperty(ApiConstant.EXTRAS_PHONE, email_phone)
        }
        signInJson.addProperty(ApiConstant.EXTRAS_PASSWORD, pass)



        mUserRepository.doSignIn(
            signInJson,
            isInternetConnected,
            baseView,
            LoginResp
        )
    }


    //FORGOT PASS API CALLING
    private val signupResp = MutableLiveData<RequestState<SignupResp>>()
    fun getSignupResp(): LiveData<RequestState<SignupResp>> = signupResp

    fun signUp(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        full_name: String,
        email: String,
        password: String,
        confirm_password: String,
        phone: String,
        state: String,
        postal_code: String,
        profile_avatar: String,
        user_doc: ArrayList<String>,
        device_token: String,


        ) {
        val signUpJson = JsonObject()
        val array = JsonArray()
        signUpJson.addProperty(ApiConstant.EXTRAS_FULL_NAME, full_name)
        signUpJson.addProperty(ApiConstant.EXTRAS_EMAIL, email)
        signUpJson.addProperty(ApiConstant.EXTRAS_PASSWORD, password)
        signUpJson.addProperty(ApiConstant.EXTRAS_CONFIRM_PASS, confirm_password)
        signUpJson.addProperty(ApiConstant.EXTRAS_PHONE, phone)
        signUpJson.addProperty(ApiConstant.EXTRAS_STATE, state)
        signUpJson.addProperty(ApiConstant.EXTRAS_POSTAL_CODE, postal_code)
        signUpJson.addProperty(ApiConstant.EXTRAS_PROFILE_AVATAR, profile_avatar)

        for (i in user_doc.indices) {
            array.add(user_doc[i])
        }
        signUpJson.add(ApiConstant.EXTRAS_USER_DOC, array)
        signUpJson.addProperty(ApiConstant.EXTRAS_DEVICETOKEN, device_token)


        mUserRepository.doSignUp(
            signUpJson,
            isInternetConnected,
            baseView,
            signupResp
        )
    }
    //RESET PASS API CALLING

    fun getResetPAssResp(): LiveData<RequestState<CommonResponse>> = CommonResponse

    fun resetPassword(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        password: String,
        confirm_password: String,
        user_id: String,

        ) {
        val restePassJson = JsonObject()

        restePassJson.addProperty(ApiConstant.EXTRAS_PASSWORD, password)
        restePassJson.addProperty(ApiConstant.EXTRAS_CONFIRM_PASS, confirm_password)
        restePassJson.addProperty(ApiConstant.EXTRAS_USER_ID, user_id)

        mUserRepository.resetPassword(
            restePassJson,
            isInternetConnected,
            baseView,
            CommonResponse
        )
    }
    //VERIFY OTP API CALLING

    fun getVerifyOTPResp(): LiveData<RequestState<CommonResponse>> = CommonResponse

    fun verifyOTP(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        isEmail: Boolean,
        email_phone: String,
        OTP: String,

        ) {
        val verifyOTPJson = JsonObject()
        if (isEmail) {
            verifyOTPJson.addProperty(ApiConstant.EXTRAS_EMAIL, email_phone)
        } else {
            verifyOTPJson.addProperty(ApiConstant.EXTRAS_PHONE, email_phone)
        }
        verifyOTPJson.addProperty(ApiConstant.EXTRAS_OTP, OTP)

        mUserRepository.verifyOTP(
            verifyOTPJson,
            isInternetConnected,
            baseView,
            CommonResponse
        )
    }

    //Subscripton API CALLING
    private val SubscriptionResp =
        MutableLiveData<RequestState<MutableList<SubscriptionPlanResp>>>()

    fun getSubcriptionPlanResp(): LiveData<RequestState<MutableList<SubscriptionPlanResp>>> =
        SubscriptionResp

    fun SubscriptionPlanList(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ) {
        mUserRepository.getSubscriptionPlanList(
            isInternetConnected,
            baseView,
            SubscriptionResp
        )
    }


    //Buy Subscription Plan API CALLING

    fun getBuySubcriptionPlanResp(): LiveData<RequestState<CommonResponse>> = CommonResponse

    fun BuySubscriptionPlanList(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        price_id: String,
        price: String,
        shared_secret: String,
        start_date: String,
        end_date: String,
    ) {
        val buyPlanJson = JsonObject()
        buyPlanJson.addProperty(ApiConstant.EXTRAS_PRICE_ID, price_id)
        buyPlanJson.addProperty(ApiConstant.EXTRAS_PRICE, price)
        buyPlanJson.addProperty(ApiConstant.EXTRAS_APPLE_RECEIPT, "")
        buyPlanJson.addProperty(ApiConstant.EXTRAS_SHARED_SECRET, shared_secret)
        buyPlanJson.addProperty(ApiConstant.EXTRAS_START_DATE, start_date)
        buyPlanJson.addProperty(ApiConstant.EXTRAS_END_DATE, end_date)

        mUserRepository.buySubscriptionPlan(
            buyPlanJson,
            isInternetConnected,
            baseView,
            CommonResponse
        )
    }


    //FORGOT PASS API CALLING
    private val fotgotPassResp = MutableLiveData<RequestState<ForgotPassResp>>()
    fun getForgotPassResp(): LiveData<RequestState<ForgotPassResp>> = fotgotPassResp

    fun forgotPass(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        isEmail: Boolean,
        email_phone: String,


        ) {
        val forgotPassJson = JsonObject()
        if (isEmail) {
            forgotPassJson.addProperty(ApiConstant.EXTRAS_EMAIL, email_phone)
        } else {
            forgotPassJson.addProperty(ApiConstant.EXTRAS_PHONE, email_phone)
        }


        mUserRepository.forgotPass(
            forgotPassJson,
            isInternetConnected,
            baseView,
            fotgotPassResp
        )
    }

    //SIGN OUT API CALLING
    fun getSignOTPResp(): LiveData<RequestState<LoginResp>> = LoginResp

    fun signOUT(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ) {
        mUserRepository.signout(

            isInternetConnected,
            baseView,
            LoginResp
        )
    }
}