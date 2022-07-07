package com.app.guardian.ui.createorjoin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.ui.videocalljoin.VideoCallJoinActivity
import org.json.JSONException
import org.json.JSONObject

class CreateOrJoinActivity : AppCompatActivity() {
    private val AUTH_URL = null
    private val apiServerUrl = "http://192.168.0.101:9000"
    var meetingId = ""
    var history_id = ""
    var to_id = ""
    var role = ""
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_join)
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        val btnCreate = findViewById<Button>(R.id.btnCreateMeeting)
        val txtUserName = findViewById<TextView>(R.id.txtUserName)
        txtUserName.text = "Create Meeting\nWith\n" + intent.getStringExtra(AppConstants.EXTRA_NAME)
//        val btnJoin = findViewById<Button>(R.id.btnJoinMeeting)
//        etMeetingId = findViewById(R.id.etMeetingId)

        role = intent.getStringExtra(AppConstants.EXTRA_TO_ROLE)!!
        name = intent.getStringExtra(AppConstants.EXTRA_NAME)!!
        to_id = intent.getStringExtra(AppConstants.EXTRA_TO_ID)!!
        history_id = intent.getStringExtra(AppConstants.EXTRA_CALLING_HISTORY_ID)!!
        //
        btnCreate.setOnClickListener { v: View? -> getToken(null) }
    }


    private fun isNullOrEmpty(str: String?): Boolean {
        return "null" == str || "" == str || null == str
    }


    private fun getToken(meetingId: String?) {
        if (!ReusedMethod.isNetworkConnected(this@CreateOrJoinActivity)) {
            return
        }
        if (!isNullOrEmpty(resources.getString(R.string.video_call_auth)) && !isNullOrEmpty(AUTH_URL)) {
            ReusedMethod.displayMessage(
                this,
                "Please Provide only one - either auth_token or auth_url"
            )
            return
        }
        if (!isNullOrEmpty(resources.getString(R.string.video_call_auth))) {
            if (meetingId == null) {
                createMeeting(resources.getString(R.string.video_call_auth))
            } else {
                joinMeeting(resources.getString(R.string.video_call_auth))
            }
            return
        }
        if (!isNullOrEmpty(AUTH_URL)) {
            AndroidNetworking.get("$apiServerUrl/get-token")
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        try {
                            val token = response.getString("token")
                            if (meetingId == null) {
                                createMeeting(token)
                            } else {
                                joinMeeting(token)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onError(anError: ANError) {
                        anError.printStackTrace()
                        Log.e("VIDEO_CALL", "ERROR:    $anError")
//                        ReusedMethod.displayMessage(this@CreateOrJoinActivity, anError.errorDetail)
                    }
                })
            return
        }
        ReusedMethod.displayMessage(
            this@CreateOrJoinActivity,
            "Please Provide auth_token or auth_url"
        )
    }

    private fun createMeeting(token: String) {
        AndroidNetworking.post("https://api.videosdk.live/v1/meetings")
            .addHeaders("Authorization", token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        meetingId = response.getString("meetingId")
                        Log.e("VIDEO_CALL", "CREATE_MEATING_RESP:    $response")
                        val meetingId = response.getString("meetingId")
                        val intent =
                            Intent(this@CreateOrJoinActivity, VideoCallJoinActivity::class.java)

                        intent.putExtra("token", token)
                        intent.putExtra("meetingId", meetingId)
                        intent.putExtra(
                            AppConstants.EXTRA_CALLING_HISTORY_ID,
                            history_id
                        )
                        intent.putExtra(
                            AppConstants.EXTRA_TO_ID,
                            to_id
                        )
                        intent.putExtra(
                            AppConstants.EXTRA_TO_ROLE,
                            role
                        )
                        intent.putExtra(
                            AppConstants.EXTRA_NAME,
                            name
                        )
                        intent.putExtra(
                            AppConstants.IS_JOIN,
                            false
                        )
                        intent.putExtra(
                            AppConstants.EXTRA_URL,
                            "https://api.videosdk.live/v1/meetings/$meetingId"
                        )
                        intent.putExtra(AppConstants.EXTRA_ROOM_ID, meetingId)
                        startActivity(intent)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError) {
                    anError.printStackTrace()
                    Log.e("VIDEO_CALL", "CREATE_MEATING_ERROR:    " + anError.errorDetail)
                    ReusedMethod.displayMessage(
                        this@CreateOrJoinActivity,
                        anError.message.toString()
                    )
                }
            })
    }

    private fun joinMeeting(token: String) {
        AndroidNetworking.post("https://api.videosdk.live/v1/meetings/$meetingId")
            .addHeaders("Authorization", token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    meetingId = response.getString("meetingId")
                    Log.e("VIDEO_CALL", "JOIN_MEATING_RESP:    $response")
                    val intent =
                        Intent(this@CreateOrJoinActivity, VideoCallJoinActivity::class.java)
                    intent.putExtra("token", token)
                    intent.putExtra("meetingId", meetingId)
                    intent.putExtra(
                        AppConstants.EXTRA_CALLING_HISTORY_ID,
                        intent.getStringExtra(AppConstants.EXTRA_CALLING_HISTORY_ID)
                    )
                    intent.putExtra(
                        AppConstants.EXTRA_TO_ID,
                        intent.getStringExtra(AppConstants.EXTRA_TO_ID)
                    )
                    intent.putExtra(
                        AppConstants.EXTRA_TO_ROLE,
                        intent.getStringExtra(AppConstants.EXTRA_TO_ROLE)
                    )
                    intent.putExtra(
                        AppConstants.EXTRA_NAME,
                        intent.getStringExtra(AppConstants.EXTRA_NAME)
                    )
                    intent.putExtra(
                        AppConstants.EXTRA_URL,
                        "https://api.videosdk.live/v1/meetings/$meetingId"
                    )
                    intent.putExtra(AppConstants.EXTRA_ROOM_ID, meetingId)
                    startActivity(intent)

                }

                override fun onError(anError: ANError) {
                    anError.printStackTrace()
                    ReusedMethod.displayMessage(
                        this@CreateOrJoinActivity,
                        anError.message.toString()
                    )
                    Log.e("VIDEO_CALL", "JOIN_MEATING_ERRRO:    " + anError.errorDetail)

                }
            })
    }
}