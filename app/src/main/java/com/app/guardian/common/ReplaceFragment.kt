package com.app.guardian.common

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.guardian.R
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.User.UserHome.UserHomeFragment

object ReplaceFragment {
    val transaction : FragmentTransaction ?= null


    fun homeFragmentReplace(currentActivity : HomeActivity,fragment: Fragment,backStackName : String?){
        val transaction  = currentActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flUserContainer, fragment)
        transaction.addToBackStack(backStackName)
        transaction.commit()
    }
}