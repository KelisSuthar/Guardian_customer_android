package com.app.guardian.ui.editProfile

import android.app.Activity
import android.os.Bundle
import android.text.Layout
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.google.android.material.textfield.TextInputEditText
import com.rilixtech.widget.countrycodepicker.CountryCodePicker

class EditProfileActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        val edtEmail = findViewById<TextInputEditText>(R.id.edtLoginEmail)
        val ccp = findViewById<CountryCodePicker>(R.id.ccp)
        val cl1 = findViewById<ConstraintLayout>(R.id.clayout1)

        ReusedMethod.changePhoneState(
            this,
            edtEmail,
            ccp,
            cl1
        )
        ccp.setCountryForPhoneCode(1)

    }

}