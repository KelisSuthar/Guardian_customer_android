package com.app.guardian.ui.chatting

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.*
import android.view.*
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.extentions.formatTime12hr
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentChattingBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.chatting.adapter.ChatMessageAdapter
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChattingFragment(selectUserId: Int,var selectUserFullName: String, profilePicUrl: String) : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentChattingBinding
    var chatMessageAdapter: ChatMessageAdapter? = null
    var chatArray = ArrayList<String>()

    override fun getInflateResource(): Int {
        return R.layout.fragment_chatting
    }

    override fun initView() {
        mBinding = getBinding()
        chatMessageAdapter?.notifyDataSetChanged()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        mBinding.txtChatUserName.text= selectUserFullName

        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.lawyer_list),
            false,
            false
        )

        mBinding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        setAdapter()
        callApi()
        mBinding.nodataChat.gone()
        mBinding.noInternetChat.llNointernet.gone()
        mBinding.rvChat.visible()
    }

    private fun callApi() {
//        if (ReusedMethod.isNetworkConnected(requireActivity())) {
//
//        } else {
//            mBinding.noInternetChat.llNointernet.visible()
//            mBinding.nodataChat.gone()
//            mBinding.rvChat.gone()
//        }
    }

    private fun setAdapter() {
        mBinding.rvChat.adapter = null
        chatMessageAdapter = ChatMessageAdapter(requireActivity(), chatArray)
        mBinding.rvChat.adapter = chatMessageAdapter
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.btnSend.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSend -> {


                val dialog = Dialog(
                    requireActivity(),
                    com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
                )
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setContentView(R.layout.virtual_witness_request_dialog)
                dialog.setCancelable(true)

                val cvScheduleDate: MaterialCardView = dialog.findViewById(R.id.cvScheduleDate)
                val cvScheduleTime: MaterialCardView = dialog.findViewById(R.id.cvScheduleTime)
                val txtDate: TextView = dialog.findViewById(R.id.txtDate)
                val txtTime: TextView = dialog.findViewById(R.id.txtTime)
                val btnImmediateJoin: Button = dialog.findViewById(R.id.btnImmediateJoin)
                val btnRequestSend: Button = dialog.findViewById(R.id.btnRequestSend)

                cvScheduleDate.setOnClickListener {
                    selectDate(txtDate)

                }
                cvScheduleTime.setOnClickListener {
                    selectTime(txtTime)
                }
                btnImmediateJoin.setOnClickListener {

                }
                btnRequestSend.setOnClickListener {

                }

                dialog.show()
            }
        }
    }

    private fun selectTime(txtTime: TextView) {
        val currentTime = Calendar.getInstance().time
        val hrsFormatter = SimpleDateFormat("HH", Locale.getDefault())
        val minFormatter = SimpleDateFormat("mm", Locale.getDefault())

        val timePicker = TimePickerDialog(
            requireContext(), R.style.DialogTheme,
            // listener to perform task
            // when time is picked
            { view, hourOfDay, minute ->
                val formattedTime: String = when {
                    hourOfDay == 0 -> {
                        if (minute < 10) {
                            "${hourOfDay + 12}:0${minute} am"
                        } else {
                            "${hourOfDay + 12}:${minute} am"
                        }
                    }
                    hourOfDay > 12 -> {
                        if (minute < 10) {
                            "${hourOfDay - 12}:0${minute} pm"
                        } else {
                            "${hourOfDay - 12}:${minute} pm"
                        }
                    }
                    hourOfDay == 12 -> {
                        if (minute < 10) {
                            "${hourOfDay}:0${minute} pm"
                        } else {
                            "${hourOfDay}:${minute} pm"
                        }
                    }
                    else -> {
                        if (minute < 10) {
                            "${hourOfDay}:${minute} am"
                        } else {
                            "${hourOfDay}:${minute} am"
                        }
                    }
                }
                txtTime.text = formattedTime
            },
            // default hour when the time picker
            // dialog is opened
            hrsFormatter.format(currentTime).toInt(),
            // default minute when the time picker
            // dialog is opened
            minFormatter.format(currentTime).toInt(),
            false
        )

        // then after building the timepicker
        // dialog show the dialog to user
        timePicker.show()
        timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark))
        timePicker.getButton(DatePickerDialog.BUTTON_NEUTRAL)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark))
        timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark))
    }

    fun selectDate(txtDate: TextView) {
        var mYear = 0
        var mMonth = 0
        var mDay = 0

        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            requireActivity(), R.style.DialogTheme,
            { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val calendar = Calendar.getInstance()
                calendar[year, monthOfYear] = dayOfMonth
                txtDate.text = getDate(year, monthOfYear, dayOfMonth)
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEUTRAL)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark))


        val calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.MONTH, 1)
        datePickerDialog.datePicker.minDate = calendar2.timeInMillis


    }

    private fun getDate(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar[year, monthOfYear] = dayOfMonth
        return dateFormatter.format(calendar.time)
    }
}