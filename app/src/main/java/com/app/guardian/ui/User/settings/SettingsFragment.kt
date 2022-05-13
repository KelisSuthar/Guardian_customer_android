package com.app.guardian.ui.User.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.app.guardian.R
import com.app.guardian.termsandcondtions.TermAndConditionsActivity
import com.app.guardian.ui.ResetPassword.ResetPasswordActivity
import com.app.guardian.ui.aboutus.AboutUsActivity
import com.app.guardian.ui.editProfile.EditProfileActivity
import com.app.guardian.ui.virtualWitness.VirtualWitnessActivity


class SettingsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val btnEditprofile = view?.findViewById(R.id.btnEditProfile) as AppCompatButton
        val tvChangePwd = view?.findViewById(R.id.tvChangePwd) as TextView
        val tvAbout = view?.findViewById(R.id.tvAbout) as TextView
        val tvVirtualWitness= view?.findViewById(R.id.tvVirtualWitness) as TextView
        val tvTerms= view?.findViewById(R.id.tvTerms) as TextView

        tvAbout.setOnClickListener {
            startActivity(Intent(context,AboutUsActivity::class.java))

        }
        tvTerms.setOnClickListener {
            startActivity(Intent(context,TermAndConditionsActivity::class.java))

        }
        tvVirtualWitness.setOnClickListener {
            startActivity(Intent(context,VirtualWitnessActivity::class.java))

        }

        tvChangePwd.setOnClickListener {
            startActivity(Intent(context,ResetPasswordActivity::class.java))

        }

        btnEditprofile.setOnClickListener {
            startActivity(Intent(context,EditProfileActivity::class.java))
        }
    }

}