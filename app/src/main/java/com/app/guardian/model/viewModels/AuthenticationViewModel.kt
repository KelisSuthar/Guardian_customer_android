package com.app.guardian.model.viewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.guardian.model.CommonResponse
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.ForgotPass.ForgotPassResp
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.Login.User
import com.app.guardian.model.RequestState
import com.app.guardian.model.SignUp.SignupResp
import com.app.guardian.model.SubscriptionPlan.SubscriptionPlanResp
import com.app.guardian.model.specializationList.SpecializationListResp
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.repo.UserRepo
import com.app.guardian.utils.ApiConstant
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject


class AuthenticationViewModel(private val mUserRepository: UserRepo) : ViewModel() {

    //For Common Resp
    private val CommonResponse = MutableLiveData<RequestState<CommonResponse>>()
    fun getCommonResp(): LiveData<RequestState<CommonResponse>> = CommonResponse

    //For Login Resp
    private val LoginResp = MutableLiveData<RequestState<LoginResp>>()


    //Login API CALLING
    fun getLoginResp(): LiveData<RequestState<LoginResp>> = LoginResp

    fun Login(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        isEmail: Boolean,
        dial_code: String,
        email_phone: String,
        pass: String,
        device_token: String,

        ) {
        val signInJson = JsonObject()
        if (isEmail) {
            signInJson.addProperty(ApiConstant.EXTRAS_EMAIL, email_phone)
        } else {
            signInJson.addProperty(ApiConstant.EXTRAS_PHONE, email_phone)
            signInJson.addProperty(ApiConstant.EXTRAS_DIAL_CODE, dial_code)
        }
        signInJson.addProperty(ApiConstant.EXTRAS_PASSWORD, pass)
        signInJson.addProperty(ApiConstant.EXTRAS_DEVICETOKEN, device_token)




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
        is_lawyer: Boolean,
        is_mediator: Boolean,
        full_name: String,
        email: String,
        specialization: String,
        years_of_experience: String,
        office_dial_code: String,
        office_phone: String,
        password: String,
        confirm_password: String,
        dial_code: String,
        phone: String,
        state: String,
        postal_code: String,
        licence_no: String,
        profile_avatar: String,
        user_doc: ArrayList<String>,
        device_token: String,
        firebase_uid: String,


        ) {
        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_FULL_NAME, full_name)
        body.addProperty(ApiConstant.EXTRAS_EMAIL, email)
        if (is_lawyer) {
            body.addProperty(ApiConstant.EXTRAS_SPECIALIZATION, specialization)
            body.addProperty(ApiConstant.EXTRAS_YEARS_OF_EXP, years_of_experience)
            body.addProperty(ApiConstant.EXTRAS_OFFICE_PHONE, office_phone)
            body.addProperty(ApiConstant.EXTRAS_OFFICE_DIAL_CODE, office_dial_code)
            body.addProperty(ApiConstant.EXTRAS_PHONE, phone)
            body.addProperty(ApiConstant.EXTRAS_DIAL_CODE, dial_code)
            body.addProperty(ApiConstant.EXTRAS_LICENCE_NO, licence_no)
        } else if (is_mediator) {
            body.addProperty(ApiConstant.EXTRAS_SPECIALIZATION, specialization)
            body.addProperty(ApiConstant.EXTRAS_YEARS_OF_EXP, years_of_experience)
            body.addProperty(ApiConstant.EXTRAS_PHONE, phone)
            body.addProperty(ApiConstant.EXTRAS_DIAL_CODE, dial_code)

        } else {
            body.addProperty(ApiConstant.EXTRAS_PHONE, phone)
            body.addProperty(ApiConstant.EXTRAS_DIAL_CODE, dial_code)
//            body.addProperty(ApiConstant.EXTRAS_LICENCE_NO, licence_no)
        }

        body.addProperty(ApiConstant.EXTRAS_PASSWORD, password)
        body.addProperty(ApiConstant.EXTRAS_CONFIRM_PASS, confirm_password)
        body.addProperty(ApiConstant.EXTRAS_STATE, state)
        body.addProperty(ApiConstant.EXTRAS_POSTAL_CODE, postal_code)
        body.addProperty(ApiConstant.EXTRAS_FIREBASE_UUID, firebase_uid)
        body.addProperty(ApiConstant.EXTRAS_DEVICETOKEN, device_token)

        val img_array:JsonArray = Gson().toJsonTree(user_doc).asJsonArray
        body.addProperty(ApiConstant.EXTRAS_PROFILE_AVATAR, profile_avatar)
        body.add(ApiConstant.EXTRAS_USER_DOC, img_array)


//        if (profile_avatar != "") {
//            val file = File(profile_avatar)
//            file.let {
//                body.addFormDataPart(
//                    ApiConstant.EXTRAS_PROFILE_AVATAR,
//                    it.name,
//                    it.asRequestBody("image/*".toMediaTypeOrNull())
//                )
//            }
//        }
//        user_doc.forEach {
//            if (it != "") {
//                val file = File(it)
//                file.let {
//                    body.addFormDataPart(
//                        ApiConstant.EXTRAS_USER_DOC,
//                        it.name,
//                        it.asRequestBody("image/*".toMediaTypeOrNull())
//                    )
//                }
//            }
//        }
        mUserRepository.doSignUp(
            body,
            isInternetConnected,
            baseView,
            signupResp
        )
    }

    //RESET PASS API CALLING
    private val CommonRestPassResponse =
        MutableLiveData<RequestState<MutableList<CommonResponse>>>()

    fun getResetPAssResp(): LiveData<RequestState<MutableList<CommonResponse>>> =
        CommonRestPassResponse

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
            CommonRestPassResponse
        )
    }

    //VERIFY OTP API CALLING
    private val UserResp = MutableLiveData<RequestState<User>>()
    fun getVerifyOTPResp(): LiveData<RequestState<User>> = UserResp

    fun verifyOTP(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        isEmail: Boolean,

        email_phone: String,

        ccp: String,
        OTP: String,

        ) {
        val verifyOTPJson = JsonObject()
        if (isEmail) {
            verifyOTPJson.addProperty(ApiConstant.EXTRAS_EMAIL, email_phone)
        } else {
            verifyOTPJson.addProperty(ApiConstant.EXTRAS_PHONE, email_phone)
            verifyOTPJson.addProperty(ApiConstant.EXTRAS_DIAL_CODE, ccp)
        }
        verifyOTPJson.addProperty(ApiConstant.EXTRAS_OTP, OTP)

        mUserRepository.verifyOTP(
            verifyOTPJson,
            isInternetConnected,
            baseView,
            UserResp
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
//        start_date: String,
//        end_date: String,
    ) {
        val buyPlanJson = JsonObject()
        buyPlanJson.addProperty(ApiConstant.EXTRAS_PRICE_ID, price_id)
        buyPlanJson.addProperty(ApiConstant.EXTRAS_PRICE, price)
        buyPlanJson.addProperty(ApiConstant.EXTRAS_APPLE_RECEIPT, "fddgfgfgfg@hardik15")
        buyPlanJson.addProperty(ApiConstant.EXTRAS_SHARED_SECRET, shared_secret)
//        buyPlanJson.addProperty(ApiConstant.EXTRAS_START_DATE, start_date)
//        buyPlanJson.addProperty(ApiConstant.EXTRAS_END_DATE, end_date)

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
        dial_code: String,
        email_phone: String,


        ) {
        val forgotPassJson = JsonObject()
        if (isEmail) {
            forgotPassJson.addProperty(ApiConstant.EXTRAS_EMAIL, email_phone)
        } else {
            forgotPassJson.addProperty(ApiConstant.EXTRAS_PHONE, email_phone)
            forgotPassJson.addProperty(ApiConstant.EXTRAS_DIAL_CODE, dial_code)
        }


        mUserRepository.forgotPass(
            forgotPassJson,
            isInternetConnected,
            baseView,
            fotgotPassResp
        )
    }

    //SIGN OUT API CALLING
    fun getSignOutResp(): LiveData<RequestState<LoginResp>> = LoginResp

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

    private val chnagePass = MutableLiveData<RequestState<MutableList<CommonResponse>>>()
    fun getchangePassResp(): LiveData<RequestState<MutableList<CommonResponse>>> = chnagePass
    fun changePassword(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        old_password: String,
        new_password: String,
        confirm_new_password: String,
    ) {
        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_OLD_PASS, old_password)
        body.addProperty(ApiConstant.EXTRAS_NEW_PASS, new_password)
        body.addProperty(ApiConstant.EXTRAS_CON_NEW_PASS, confirm_new_password)

        mUserRepository.changePass(
            body,
            isInternetConnected,
            baseView,
            chnagePass
        )
    }


    //CALL SPECIALIZATION LIST
    private val specializationListResp =
        MutableLiveData<RequestState<MutableList<SpecializationListResp>>>()

    fun getSpecializationListResp(): LiveData<RequestState<MutableList<SpecializationListResp>>> =
        specializationListResp

    fun getSpecializationList(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
    ) {
        mUserRepository.getSpecializationList(
            isInternetConnected,
            baseView,
            specializationListResp
        )
    }

    //CALL VERIFY PHONE OTP API AFTER USER UPDATE MOBILE NUMBER IN EDIT PROFILE
    private val verifyPhoneOTPResp =
        MutableLiveData<RequestState<MutableList<CommonResponse>>>()

    fun getPhoneOtpVerifyResp(): LiveData<RequestState<MutableList<CommonResponse>>> =
        verifyPhoneOTPResp

    fun updatePhoneOtpVerify(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        new_phone: String,
        otp: String,
    ) {

        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_OLD_PASS, new_phone)
        body.addProperty(ApiConstant.EXTRAS_NEW_PASS, otp)

        mUserRepository.updatePhoneOtpVerify(
            body,
            isInternetConnected,
            baseView,
            verifyPhoneOTPResp
        )
    }

    //EDIT PROFILE API
    private val editprofileResp =
        MutableLiveData<RequestState<UserDetailsResp>>()

    fun getEditProfileResp(): LiveData<RequestState<UserDetailsResp>> =
        editprofileResp

    fun EditProfile(
        isInternetConnected: Boolean,
        baseView: BaseActivity,
        is_lawyer: Boolean,
        is_mediator: Boolean,
        full_name: String,
        email: String,
        specialization: String,
        years_of_experience: String,
        office_dial_code: String,
        office_phone: String,
        dial_code: String,
        phone: String,
        state: String,
        postal_code: String,
        licence_no: String,
        profile_avatar: String,
        user_doc: ArrayList<String>,


        ) {
        val body = JsonObject()

        body.addProperty(ApiConstant.EXTRAS_FULL_NAME, full_name)
        body.addProperty(ApiConstant.EXTRAS_EMAIL, email)
        if (is_lawyer) {
            body.addProperty(ApiConstant.EXTRAS_SPECIALIZATION, specialization)
            body.addProperty(ApiConstant.EXTRAS_YEARS_OF_EXP, years_of_experience)
            body.addProperty(ApiConstant.EXTRAS_OFFICE_PHONE, office_phone)
            body.addProperty(ApiConstant.EXTRAS_OFFICE_DIAL_CODE, office_dial_code)
            body.addProperty(ApiConstant.EXTRAS_PHONE, phone)
            body.addProperty(ApiConstant.EXTRAS_DIAL_CODE, dial_code)
        } else if (is_mediator) {
            body.addProperty(ApiConstant.EXTRAS_SPECIALIZATION, specialization)
            body.addProperty(ApiConstant.EXTRAS_YEARS_OF_EXP, years_of_experience)
            body.addProperty(ApiConstant.EXTRAS_PHONE, phone)
            body.addProperty(ApiConstant.EXTRAS_DIAL_CODE, dial_code)

        } else {
            body.addProperty(ApiConstant.EXTRAS_PHONE, phone)
            body.addProperty(ApiConstant.EXTRAS_DIAL_CODE, dial_code)
        }
        body.addProperty(ApiConstant.EXTRAS_STATE, state)
        body.addProperty(ApiConstant.EXTRAS_POSTAL_CODE, postal_code)

        val img_array:JsonArray = Gson().toJsonTree(user_doc).asJsonArray
        body.addProperty(ApiConstant.EXTRAS_PROFILE_AVATAR, profile_avatar)
        body.add(ApiConstant.EXTRAS_USER_DOC, img_array)


//        if (profile_avatar != "") {
//            val file = File(profile_avatar)
//            file.let {
//                body.addFormDataPart(
//                    ApiConstant.EXTRAS_PROFILE_AVATAR,
//                    it.name,
//                    it.asRequestBody("image/*".toMediaTypeOrNull())
//                )
//            }
//        }
//        user_doc.forEach {
//            if (it != "") {
//                val file = File(it)
//                file.let {
//                    body.addFormDataPart(
//                        ApiConstant.EXTRAS_USER_DOC,
//                        it.name,
//                        it.asRequestBody("image/*".toMediaTypeOrNull())
//                    )
//                }
//            }
//        }


        mUserRepository.editprofile(
            body,
            isInternetConnected,
            baseView,
            editprofileResp
        )
    }

}