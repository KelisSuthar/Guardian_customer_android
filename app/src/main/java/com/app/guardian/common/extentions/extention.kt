package com.app.guardian.common.extentions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import android.os.Build
import android.text.InputFilter
import android.widget.EditText
import androidx.core.content.PermissionChecker
import com.app.guardian.common.AppConstants
import java.text.DecimalFormat


fun Context.showCustomDialog(
    layout: Int,
    isCancelable: Boolean = true,
    listener: (view: View, alertDialog: AlertDialog) -> Unit
) {
    val views = LayoutInflater.from(this).inflate(layout, null)
    val builder = AlertDialog.Builder(this)
    builder.setView(views)
    builder.setCancelable(isCancelable)
    val alertDialog = builder.create()
    alertDialog.setCancelable(isCancelable)
    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.setOnShowListener {
        listener(views, alertDialog)
    }
    if (!alertDialog.isShowing) {
        alertDialog.show()
    }
}



fun checkPermissions(activity: Activity, requestCode: Int, permission: String) {
    if (ActivityCompat.checkSelfPermission(
            activity,
            permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(permission),
            requestCode
        )
    }
}


fun checkLoationPermission(activity: Activity): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (PermissionChecker.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                AppConstants.EXTRA_FINE_PERMISSION
            )
            false
        }
    } else {

        true
    }
}
fun changePointFormat(NUM: String): String {
    return DecimalFormat("0.00").format(NUM.toDouble())
}
fun setMaxLength(ed:EditText)
{
    val maxLength = ed.lineCount
    var FilterArray = arrayOfNulls<InputFilter>(1)
    if(ed.lineCount>7) {
        ed.lineCount
        FilterArray[0] = InputFilter.LengthFilter(maxLength)
        ed.filters = FilterArray

    }else{

        FilterArray = arrayOfNulls(0)
        ed.filters = FilterArray
    }
}
fun changePhNumber(Num:String):String{
    return Num.replace("(","").replace(")","").replace(" ","")
}
fun addFormateToPhnumber(Num: String):String{
    var x = ""
        x = "(" + Num.substring(0, 3) + ") " + Num.substring(3, 6) + " " + Num.substring(6)


    return x
}
fun addCanadianFormateToPhnumber(Num:String): String {
    var x = ""

        if(Num.startsWith("(")){
        if(Num.length == 1){
            if(Num != "(")
            {
                x = "($Num"
            }
        }else if(Num.length == 4){
            if(!Num.endsWith(") "))
            {
                x = "$x$Num) "
            }
        }
        else if(Num.length == 9){
            if(!Num.endsWith(" "))
            {
                x = "$x$Num "
            }
        }
    }else if(Num.length>10){
            x = "(" + Num.substring(0, 3) + ") " + Num.substring(3, 6) + " " + Num.substring(6)
        }
    return x
}

