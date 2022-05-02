package com.app.guardian.common


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.app.guardian.R


class CustomProgressDialog(context: Context) : Dialog(context, R.style.full_screen_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress_dialog)

    }
}