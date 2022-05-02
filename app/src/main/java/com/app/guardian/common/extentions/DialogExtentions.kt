package com.app.guardian.common.extentions

import android.app.Dialog

fun isVisible(isShow: Boolean, dialog: Dialog?) =
    if (isShow) {
        if (!dialog?.isShowing!!){
            dialog!!.show()
        }else{

        }
    }
    else {
        if (dialog?.isShowing!!){
            dialog.dismiss()
            dialog.cancel()
        }else{

        }
}