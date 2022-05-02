package com.app.guardian.common

import android.content.Context
import android.graphics.Typeface

class TypeFactory(context: Context) {
    companion object {
        lateinit var Poppins_Light: Typeface
        lateinit var Poppins_Regular: Typeface
        lateinit var Poppins_Medium: Typeface
        lateinit var Poppins_SemiBold: Typeface
        lateinit var Poppins_Bold: Typeface

        var POPPINS_LIGHT = "fonts/Poppins-Light.ttf"
        var POPPINS_REGULAR = "fonts/Poppins-Regular.ttf"
        var POPPINS_MEDIUM = "fonts/Poppins-Medium.ttf"
        var POPPINS_SEMIBOLD = "fonts/Poppins-SemiBold.ttf"
        var POPPINS_BOLD = "fonts/Poppins-Bold.ttf"
    }

    init {
        Poppins_Light = Typeface.createFromAsset(context.assets, POPPINS_LIGHT)
        Poppins_Regular = Typeface.createFromAsset(context.assets, POPPINS_REGULAR)
        Poppins_Medium = Typeface.createFromAsset(context.assets, POPPINS_MEDIUM)
        Poppins_SemiBold = Typeface.createFromAsset(context.assets, POPPINS_SEMIBOLD)
        Poppins_Bold = Typeface.createFromAsset(context.assets, POPPINS_BOLD)
    }

}