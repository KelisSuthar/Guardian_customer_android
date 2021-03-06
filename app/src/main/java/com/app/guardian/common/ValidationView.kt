package com.app.guardian.common

interface ValidationView {
    interface LoginView {
        fun email()
        fun emailValidation()
        fun passwordValidation()
        fun passwordMinValidation()
        fun passwordSpecialValidation()
        fun success()
    }

    interface SignUp {

        fun email_empty()
        fun emailValidation()
        fun password_empty()
        fun passwordMinValidation()
        fun passwordSpecialValidation()
        fun success()
    }

    interface CreateProile {
        fun full_name()
        fun full_name_length()
        fun ph_number()
        fun ph_length_number()
        fun email()
        fun emailValidation()
        fun tandcCheck()

        //        fun family_full_name()
        fun family_full_name_length()

        //        fun family_email()
        fun family_emailValidation()
        fun family_member_fullname_Validation()
        fun family_member_email_Validation()
        fun success()
    }

    interface ForgotPass {
        fun email()
        fun emailValidation()
        fun success()
    }

    interface NewPass {
        fun passwordValidation()
        fun passwordMinValidation()
        fun passwordSpecialValidation()
        fun confirmPass()
        fun con_passwordMinValidation()
        fun con_passwordSpecialValidation()
        fun comparePass()
        fun success()
    }

    interface AddNewCard {
        fun cardNum()
        fun cardNum_length()
        fun date()
        fun CVV()
        fun CVV_length()
        fun full_name()
        fun full_name_length()
        fun save_card()
        fun success()
    }

    interface RenterApplication {
        fun full_name()
        fun full_name_length()
        fun ph_number()
        fun ph_length_number()
        fun email()
        fun emailValidation()
        fun address()
        fun address_length()
        fun city()
        fun city_length()
        fun country()
        fun country_length()
        fun postal_code()
        fun postal_code_length()
        fun dob()
        fun cultural_back()
        fun cultural_back_length()
        fun Material_Status()
        fun Material_Status_Length()
        fun Success()
    }

    interface AddProperty {
        fun name()
//        fun name_length()
        fun Address()
        fun flat_name()
        fun rent_per_month()
        fun rent_per_month_length()
        fun image_length()
        fun bedRoomprice_length()
        fun bedRoomprice()
        fun bedRoom_length()
        fun aboutRoom()
        fun aboutRoom_length()
        fun property_Type_id()
        fun aminities()
        fun availability()
        fun check_in()
        fun check_out()
        fun ph_number()
        fun ph_length_number()
        fun email()
        fun emailValidation()
        fun desc()
        fun desc_length()
        fun h_rules()
        fun h_rules_length()
        fun health_sefaty()
        fun health_sefaty_length()
//        fun cancel_policy()
//        fun cancel_policy_length()
        fun Success()

    }

    interface addCartMemebr {
        fun full_name()
        fun full_name_length()
        fun ph_number()
        fun ph_length_number()
        fun email()
        fun emailValidation()
        fun add_card()
        fun success()

    }

    interface UpdateProfile {
        fun first_name()
        fun first_name_length()
//        fun last_name()
//        fun last_name_length()
        fun email()
        fun emailValidation()
        fun family_member_fullname_Validation()
        fun family_member_email_Validation()
        fun family_full_name_length()
        fun family_emailValidation()
        fun success()
    }

    interface ChnagePass {
        fun oldpasswordValidation()
        fun OldpasswordMinValidation()
        fun oldpasswordSpecialValidation()
        fun newPass()
        fun newpasswordMinValidation()
        fun newpasswordSpecialValidation()
        fun conPass()
        fun conpasswordMinValidation()
        fun conpasswordSpecialValidation()
        fun comparePass()
        fun success()
    }

    interface AddReviews {
        fun item_rate()
        fun rest_rate()
        fun success()
    }

    interface AddAppReviews {
        fun rattings()
        fun reviews()
        fun success()
    }
}