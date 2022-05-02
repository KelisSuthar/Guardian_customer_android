package com.app.guardian.common.extentions

import android.annotation.SuppressLint
import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun Date.showDateFormat(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(this)
}

fun Date.apiDateFormat(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(this)
}

fun Date.formatTime12hr(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(this)
}

fun Date.formatTimeIn24hr(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

fun String.formatTimeInGMT(): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val newDate = sdf.parse(this)
    val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    sdf1.timeZone = TimeZone.getTimeZone("GMT")
    return sdf1.format(newDate)
}

fun String.convertIntoAnother(dateFormat: String): String? {
    val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date: Date?
    try {
        date = simpleFormat.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }
    return SimpleDateFormat(dateFormat, Locale.getDefault()).format(date)
}

@SuppressLint("SimpleDateFormat")
fun String.stringToDate(dateFormat: String): String? {

    val simpleFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    simpleFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date: Date?
    try {
        date = simpleFormat.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }
    return SimpleDateFormat(dateFormat).format(date)
}

fun String.plusTimeInCurrentTime(): String? {
    return try {
        val simpleFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val myTime = simpleFormat.format(Date())
        val newTime: String = simpleFormat.format(myTime.plusTime(this.toInt()))
        newTime
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

}

fun String.convertDateTime(): String? {
    return try {
        /*val simpleFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val myTime = simpleFormat.format("MMM-yyyy")
        val newTime: String = simpleFormat.format(myTime.plusTime(this.toInt()))
        newTime*/

        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).parse(this)
        SimpleDateFormat("MMM,yyyy", Locale.US).format(date).toUpperCase(Locale.ROOT)


    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun plusCurrentTime(): String? {
    return try {
        val simpleFormat = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
        val myTime = simpleFormat.format(Date())
        myTime
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

}

@SuppressLint("SimpleDateFormat")
fun String.stringToDateObj(): Date? {
    val simpleFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    // simpleFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date: Date?
    try {
        date = simpleFormat.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }
    return date
}

fun String.plusTime(plusTime: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this.stringToDateObj()
    calendar.add(Calendar.MINUTE, plusTime)
    return calendar.time
}
@SuppressLint("SimpleDateFormat")
fun changeDateFormat(current_format: String, change_formate: String, str_date: String):String{
    val inputFormat = SimpleDateFormat(current_format)
    val outputFormat = SimpleDateFormat(change_formate)
    var date: Date? = null
    var str: String? = null
    try {
        date = inputFormat.parse(str_date)
        str = outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return str!!
}
fun String.getRelativeTimeDisplay(): String {

    val currentTime = Calendar.getInstance().timeInMillis
    val pastTime = this.stringToDateObj()?.time

    pastTime?.let {
        return if (pastTime - currentTime > 0) {
            DateUtils.getRelativeTimeSpanString(
                currentTime, currentTime,
                DateUtils.FORMAT_ABBREV_ALL.toLong(), DateUtils.FORMAT_ABBREV_ALL
            ).toString()
        } else {
            DateUtils.getRelativeTimeSpanString(
                pastTime, currentTime,
                DateUtils.FORMAT_ABBREV_ALL.toLong(), DateUtils.FORMAT_ABBREV_ALL
            ).toString()
        }
    } ?: return ""
}

@SuppressLint("SimpleDateFormat")
fun currentDateFormat():String{
    val formatter = SimpleDateFormat("MMM dd, yyyy")
    val date = Date()
    return  formatter.format(date)
}

@SuppressLint("SimpleDateFormat")
fun String.getGmtdate():String?{
    val sandlotTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val outputPattern = "dd MMM, yyyy"
    val outputFormat = SimpleDateFormat(outputPattern)
    sandlotTime.timeZone = TimeZone.getTimeZone("UTC")
    val native: Date = sandlotTime.parse(this)
    return outputFormat.format(native)
}


