package com.app.guardian.common

import android.content.Context
import android.content.SharedPreferences
import com.app.guardian.common.AppConstants.APP_NAME
import com.app.guardian.common.AppConstants.SKIP_INTRO
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.cms.CMSResp
import com.app.guardian.utils.ApiConstant
import com.google.gson.Gson


object SharedPreferenceManager {
    val PREF_NAME = APP_NAME + "_pref"
    private var isInit = false
    private var prefs: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    fun init(context: Context) {
        if (isInit)
            return
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = prefs!!.edit()
        isInit = true
    }

    fun getUser(): LoginResp? {
        val userJson = getString(AppConstants.USER_DETAIL_LOGIN, "")
        if (userJson?.isEmpty()!!) {
            return null
        }
        try {
            return Gson().fromJson(userJson, LoginResp::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getCMS(): CMSResp? {
        val userJson = getString(AppConstants.CMS_DETAIL, "")
        if (userJson?.isEmpty()!!) {
            return null
        }
        try {
            return Gson().fromJson(userJson, CMSResp::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun putBoolean(key: String, value: Boolean) {
        editor!!.putBoolean(key, value)
        editor!!.commit()
    }

    fun putString(key: String, value: String) {
        editor!!.putString(key, value)
        editor!!.commit()
    }

    fun putFloat(key: String, value: Float) {
        editor!!.putFloat(key, value)
        editor!!.commit()
    }

    fun putLong(key: String, value: Long) {
        editor!!.putLong(key, value)
        editor!!.commit()
    }

    fun putLat(key: String, value: Long) {
        editor!!.putLong(key, value)
        editor!!.commit()
    }

    fun putInt(key: String, value: Int) {
        editor!!.putInt(key, value)
        editor!!.commit()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs!!.getBoolean(key, defaultValue)
    }

    fun getString(key: String, defValue: String): String? {
        return prefs!!.getString(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        return prefs!!.getFloat(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        return prefs!!.getInt(key, defValue)
    }

    fun getLong(key: String, defValue: Long): Long {
        return prefs!!.getLong(key, defValue)
    }

    fun removeAllData() {
        val device_token = getString(ApiConstant.EXTRAS_DEVICETOKEN, "")
        editor!!.clear().commit()
        putString(ApiConstant.EXTRAS_DEVICETOKEN, device_token.toString())
        putBoolean(SKIP_INTRO, true)
        putBoolean(AppConstants.IS_WALKTHROUGH_VIEWED, true)
    }

    fun removeCategory() {
        putString(AppConstants.GET_CAT, "")
        editor!!.remove(AppConstants.GET_CAT)
    }

    fun removeCard() {
        putString(AppConstants.CARD_DETAILS, "")
        editor!!.remove(AppConstants.CARD_DETAILS)
    }

    fun clearCityState() {
        putString(AppConstants.CITY, "")
        putString(AppConstants.STATE, "")
        editor!!.remove(AppConstants.CITY)
        editor!!.remove(AppConstants.STATE)
    }

    fun removeString(key: String) {
        editor!!.remove(key)
    }

    fun removeFilterData() {
        removeString(AppConstants.EXTRA_TOTAL_CART_ITEM_COUNT)
        removeString(AppConstants.EXTRA_TOTAL_CART_ITEM_COUNT)
        removeString(AppConstants.EXTRA_JOB_TYPE)
        removeString(AppConstants.EXTRA_CAT_ID)
    }

}