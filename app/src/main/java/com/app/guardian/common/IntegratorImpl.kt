package com.app.guardian.common

import android.text.TextUtils

object IntegratorImpl {

    fun isValidLogin(
        email: String,
        password: String,
        ValidationView: ValidationView.LoginView
    ) {
        if (TextUtils.isEmpty(email)) {
            ValidationView.email()
        } else if (!SmartUtils.emailValidator(email)) {
            ValidationView.emailValidation()
        } else if (TextUtils.isEmpty(password)) {
            ValidationView.passwordValidation()
        } else if (password.length < 8) {
            ValidationView.passwordMinValidation()
        } else if (!SmartUtils.checkSpecialPasswordValidation(password)) {
            ValidationView.passwordSpecialValidation()
        } else {
            ValidationView.success()
        }
    }

    fun isValidSignUp(


        email: String,

        password: String,

        ValidationView: ValidationView.SignUp
    ) {

       if (TextUtils.isEmpty(email)) {
            ValidationView.email_empty()
        } else if (!SmartUtils.emailValidator(email)) {
            ValidationView.emailValidation()
        } else if (TextUtils.isEmpty(password)) {
            ValidationView.password_empty()
        } else if (password.length < 8) {
            ValidationView.passwordMinValidation()
        } else if (!SmartUtils.checkSpecialPasswordValidation(password)) {
            ValidationView.passwordSpecialValidation()
        } else {
            ValidationView.success()
        }
    }

    fun isValidCreateProfile(
        fullName: String,
        phNumber: String,
        email: String,
        member_fullname: String,
        member_email: String,
        checked: Boolean,
        ValidationView: ValidationView.CreateProile
    ) {
        if (TextUtils.isEmpty(fullName)) {
            ValidationView.full_name()
        } else if (fullName.length > 35 || fullName.length < 3) {
            ValidationView.full_name_length()
        } else if (TextUtils.isEmpty(phNumber)) {
            ValidationView.ph_number()
        } else if (phNumber.length < 14) {
            ValidationView.ph_length_number()
        } else if (TextUtils.isEmpty(email)) {
            ValidationView.email()
        } else if (!SmartUtils.emailValidator(email)) {
            ValidationView.emailValidation()
        } else if (member_email.isNotEmpty() && member_fullname.isEmpty()) {
            ValidationView.family_member_fullname_Validation()
        } else if (member_fullname.isNotEmpty() && member_email.isEmpty()) {
            ValidationView.family_member_email_Validation()
        } else if (member_email.isNotEmpty() && member_fullname.isEmpty() && (member_fullname.length < 3 || member_fullname.length > 35)) {
            ValidationView.family_full_name_length()
        } else if (member_fullname.isNotEmpty() && member_email.isEmpty() && !SmartUtils.emailValidator(
                email
            )
        ) {
            ValidationView.family_emailValidation()
        } else if (!checked) {
            ValidationView.tandcCheck()
        } else {
            ValidationView.success()
        }
    }

    fun isValidForgotPass(email: String, ValidationView: ValidationView.ForgotPass) {

        if (TextUtils.isEmpty(email)) {
            ValidationView.email()
        } else if (!SmartUtils.emailValidator(email)) {
            ValidationView.emailValidation()
        } else {
            ValidationView.success()
        }
    }

    fun isValidNewPass(password: String, conPass: String, ValidationView: ValidationView.NewPass) {

        if (TextUtils.isEmpty(password)) {
            ValidationView.passwordValidation()
        } else if (password.length < 6) {
            ValidationView.passwordMinValidation()
        } else if (!SmartUtils.checkSpecialPasswordValidation(password)) {
            ValidationView.passwordSpecialValidation()
        } else if (TextUtils.isEmpty(conPass)) {
            ValidationView.confirmPass()
        } else if (conPass.length < 6) {
            ValidationView.con_passwordMinValidation()
        } else if (!SmartUtils.checkSpecialPasswordValidation(conPass)) {
            ValidationView.con_passwordSpecialValidation()
        } else if (password != conPass) {
            ValidationView.comparePass()
        } else {
            ValidationView.success()
        }

    }

    fun isValidAddCard(
        cardNum: String,
        date: String,
        cvv: String,
        name: String,
        checked: Boolean,
        ValidationView: ValidationView.AddNewCard
    ) {
        if (TextUtils.isEmpty(cardNum)) {
            ValidationView.cardNum()
        } else if (cardNum.length < 18) {
            ValidationView.cardNum_length()
        } else if (TextUtils.isEmpty(date)) {
            ValidationView.cardNum_length()
        } else if (TextUtils.isEmpty(cvv)) {
            ValidationView.CVV()
        } else if (cvv.length < 3 || cvv.length > 3) {
            ValidationView.CVV_length()
        } else if (cvv.length < 3 || cvv.length > 3) {
            ValidationView.CVV_length()
        } else if (TextUtils.isEmpty(name)) {
            ValidationView.full_name()
        } else if (name.length < 3 || name.length > 35) {
            ValidationView.full_name_length()
        } else if (!checked) {
            ValidationView.save_card()
        } else {
            ValidationView.success()
        }

    }

    fun isCartData(
        fullName: String,
        phNumber: String,
        email: String,
        cart_tv: String,
        ValidationView: ValidationView.addCartMemebr
    ) {
        if (TextUtils.isEmpty(fullName)) {
            ValidationView.full_name()
        } else if (fullName.length > 35 || fullName.length < 3) {
            ValidationView.full_name_length()
        } else if (TextUtils.isEmpty(phNumber)) {
            ValidationView.ph_number()
        } else if (phNumber.length < 10) {
            ValidationView.ph_length_number()
        } else if (TextUtils.isEmpty(email)) {
            ValidationView.email()
        } else if (!SmartUtils.emailValidator(email)) {
            ValidationView.emailValidation()
        } else if (cart_tv.equals("Please add payment details")) {
            ValidationView.add_card()
        } else {
            ValidationView.success()
        }

    }

    fun isRenterApp(
        name: String,
        phNumber: String,
        email: String,
        Address: String,
        City: String,
        country: String,
        postal_code: String,
        dob: String,
        cultural_back: String,
        Material_Status: String,
        ValidationView: ValidationView.RenterApplication
    ) {
        if (TextUtils.isEmpty(name)) {
            ValidationView.full_name()
        } else if (name.length < 3 || name.length > 35) {
            ValidationView.full_name_length()
        } else if (TextUtils.isEmpty(phNumber)) {
            ValidationView.ph_number()
        } else if (phNumber.length < 14) {
            ValidationView.ph_length_number()
        } else if (TextUtils.isEmpty(email)) {
            ValidationView.email()
        } else if (!SmartUtils.emailValidator(email)) {
            ValidationView.emailValidation()
        } else if (TextUtils.isEmpty(Address)) {
            ValidationView.address()
        } else if (Address.length < 3 || Address.length > 50) {
            ValidationView.address_length()
        } else if (TextUtils.isEmpty(City)) {
            ValidationView.city()
        } else if (City.length < 2 || City.length > 20) {
            ValidationView.city_length()
        }else if (TextUtils.isEmpty(country)) {
            ValidationView.country()
        } else if (country.length < 2 || country.length > 20) {
            ValidationView.country()
        }
        else if (TextUtils.isEmpty(postal_code)) {
            ValidationView.postal_code()
        } else if (postal_code.length < 3 || postal_code.length > 9) {
            ValidationView.postal_code_length()
        } else if (TextUtils.isEmpty(dob)) {
            ValidationView.dob()
        } else if (TextUtils.isEmpty(cultural_back)) {
            ValidationView.cultural_back()
        } else if (cultural_back.length < 2 || postal_code.length > 30) {
            ValidationView.cultural_back_length()
        } else if (Material_Status == AppConstants.EXTRA_MARTIAL_STATUS) {
            ValidationView.Material_Status()
        } else {
            ValidationView.Success()
        }

    }

    fun addProperty(
        name: String,
        address: String,
        h_flat_Num: String,
        rent_per_month: String,
        imgList: ArrayList<String>,
        bedroomQty: String,
        bedroomQtyPrice: String,
        about_room: String,
        property_type_id: Int,
        aminities: ArrayList<String>,
        availability: String,
        check_in: String,
        check_out: String,
        phNumber: String,
        email: String,
        desc: String,
        h_rules: String,
        health_sefaty: String,
        cancel_policy: String,
        ValidationView: ValidationView.AddProperty
    ) {
        if (TextUtils.isEmpty(name)) {
            ValidationView.name()
//        } else if (name.length < 1 || name.length > 30) {
//            ValidationView.name_length()
        } else if (TextUtils.isEmpty(address)) {
            ValidationView.Address()
        } else if (TextUtils.isEmpty(h_flat_Num)) {
            ValidationView.flat_name()
        } else if (TextUtils.isEmpty(rent_per_month)) {
            ValidationView.rent_per_month()
        } else if (rent_per_month.toInt() == 0) {
            ValidationView.rent_per_month_length()
        } else if (imgList.size == 0) {
            ValidationView.image_length()
        } else if (bedroomQty == "0") {
            ValidationView.bedRoom_length()
        } else if (TextUtils.isEmpty(bedroomQtyPrice)) {
            ValidationView.bedRoomprice()
        } else if (bedroomQtyPrice.toDouble() == 0.0) {
            ValidationView.bedRoomprice_length()
        } else if (TextUtils.isEmpty(about_room)) {
            ValidationView.aboutRoom()
        } else if (about_room.length < 3 ) {
            ValidationView.aboutRoom_length()
        } else if (property_type_id == -1) {
            ValidationView.property_Type_id()
        } else if (aminities.isEmpty()) {
            ValidationView.aminities()
        } else if (availability == AppConstants.EXTRA_SELECT) {
            ValidationView.availability()
        } else if (TextUtils.isEmpty(check_in)) {
            ValidationView.check_in()
        } else if (TextUtils.isEmpty(check_out)) {
            ValidationView.check_out()
        } else if (TextUtils.isEmpty(phNumber)) {
            ValidationView.ph_number()
        } else if (phNumber.length < 14) {
            ValidationView.ph_length_number()
        } else if (TextUtils.isEmpty(email)) {
            ValidationView.email()
        } else if (!SmartUtils.emailValidator(email)) {
            ValidationView.emailValidation()
        }else if (TextUtils.isEmpty(desc)) {
                ValidationView.desc()
        }else if (desc.length<3) {
            ValidationView.desc_length()
        } else if (TextUtils.isEmpty(h_rules)) {
            ValidationView.h_rules()
        } else if (h_rules.length < 3) {
            ValidationView.h_rules_length()
        } else if (TextUtils.isEmpty(health_sefaty)) {
            ValidationView.health_sefaty()
        } else if (health_sefaty.length < 3) {
            ValidationView.health_sefaty_length()
        }
//        else if (TextUtils.isEmpty(cancel_policy)) {
//            ValidationView.cancel_policy()
//        } else if (cancel_policy.length < 3) {
//            ValidationView.cancel_policy_length()
        else {
            ValidationView.Success()
        }
    }

    fun isValidUpdateProfile(
        name: String,
//        last_name: String,
        email: String,
        member_fullname: String,
        member_email: String,
        ValidationView: ValidationView.UpdateProfile
    ) {
        if (TextUtils.isEmpty(name)) {
            ValidationView.first_name()
        } else if (name.length > 30 || name.length < 3) {
            ValidationView.first_name_length()
//        } else if (TextUtils.isEmpty(last_name)) {
//            ValidationView.last_name()
//        } else if (last_name.length > 30 || last_name.length < 3) {
//            ValidationView.last_name_length()
        } else if (TextUtils.isEmpty(email)) {
            ValidationView.email()
        } else if (!SmartUtils.emailValidator(email)) {
            ValidationView.emailValidation()
        } else if (member_email.isNotEmpty() && member_fullname.isEmpty()) {
            ValidationView.family_member_fullname_Validation()
        } else if (member_fullname.isNotEmpty() && member_email.isEmpty()) {
            ValidationView.family_member_email_Validation()
        } else if (member_email.isNotEmpty() && member_fullname.isEmpty() && (member_fullname.length < 3 || member_fullname.length > 35)) {
            ValidationView.family_full_name_length()
        } else if (member_fullname.isNotEmpty() && member_email.isEmpty() && !SmartUtils.emailValidator(
                email
            )
        ) {
            ValidationView.family_emailValidation()
        } else {
            ValidationView.success()
        }
    }

    fun isValidChangePass(
        oldPass: String,
        newPass: String,
        conPass: String,
        ValidationView: ValidationView.ChnagePass
    ) {
        if (TextUtils.isEmpty(oldPass)) {
            ValidationView.oldpasswordValidation()
        } else if (oldPass.length < 6) {
            ValidationView.OldpasswordMinValidation()
        } else if (!SmartUtils.checkSpecialPasswordValidation(oldPass)) {
            ValidationView.oldpasswordSpecialValidation()
        } else if (TextUtils.isEmpty(newPass)) {
            ValidationView.newPass()
        } else if (newPass.length < 6) {
            ValidationView.newpasswordMinValidation()
        } else if (!SmartUtils.checkSpecialPasswordValidation(newPass)) {
            ValidationView.newpasswordSpecialValidation()
        } else if (TextUtils.isEmpty(conPass)) {
            ValidationView.conPass()
        } else if (conPass.length < 6) {
            ValidationView.conpasswordMinValidation()
        } else if (!SmartUtils.checkSpecialPasswordValidation(conPass)) {
            ValidationView.conpasswordSpecialValidation()
        } else if (newPass != conPass) {
            ValidationView.comparePass()
        } else {
            ValidationView.success()
        }
    }

    fun addReviews(
        booleanExtra: Boolean,
        itemRate: Int,
        restRate: Int,
        ValidationView: ValidationView.AddReviews
    ) {
        if(booleanExtra){
            if(itemRate == 0){
                ValidationView.item_rate()
            }else{
                ValidationView.success()
            }
        }else{
            when {
                itemRate == 0 -> {
                    ValidationView.item_rate()
                }
                restRate == 0 -> {
                    ValidationView.rest_rate()
                }
                else -> {
                    ValidationView.success()
                }
            }
        }
    }

    fun addAppReview(rattings: String, reviews: String, addAppReviews: ValidationView.AddAppReviews) {
        when {
            rattings.equals("0.0") -> {
                addAppReviews.rattings()
            }
            TextUtils.isEmpty(reviews) -> {
                addAppReviews.reviews()
            }
            else -> {
                addAppReviews.success()
            }
        }
    }
}