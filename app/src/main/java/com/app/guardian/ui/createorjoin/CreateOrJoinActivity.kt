package com.app.guardian.ui.createorjoin

import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.AppConstants.EXTRA_VIDEOCALL_RESP
import com.app.guardian.common.ReusedMethod
import com.app.guardian.model.GetVideoCallRequestResp.GetVideoCallRequestListResp
import com.app.guardian.ui.videocalljoin.VideoCallJoinActivity
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject

class CreateOrJoinActivity : AppCompatActivity() {

    // private val AUTH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiJmNjllODdhOS1jMzhkLTQwYjMtYTVmMi01NTlkMTEwYzc3N2QiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTY1NTI3MjUxNCwiZXhwIjoxNjU1ODc3MzE0fQ.PH-2FRmSDZPXnzatozsfSFKAnbOQWxrIVywzIW8Yt4g"
//    private val AUTH_TOKEN =
//        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiIxY2FhZmFmNi1kYjc5LTRlMDctOTk5Yy00YTNlMjUxNjQ1OGEiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTY1NTcyNjg0NCwiZXhwIjoxNjU2MzMxNjQ0fQ.y8LY-LgA4f7nTDnVKbMjgioJ4HgRcXmZbWuOYqUP1Jk"
//    private val AUTH_TOKEN =
//        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiIxN2Y2YWRhOC1kOWJkLTQwNjktYTQ3Yi0yNTQ2NGNiMDA0YjEiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTY1NjQ5NDY0NywiZXhwIjoxNjU3MDk5NDQ3fQ.Fs_JaVDxW3LGa44tkD9WgwWiGrUtO0AEgxr7rA1I6Pw"
    private val AUTH_URL = null

    //    private var etMeetingId: EditText? = null
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

        //Join Metting
//        btnJoin.setOnClickListener { v: View? ->
//            val meetingId = etMeetingId!!.getText().toString().trim { it <= ' ' }
//            val pattern = Regex("\\w{4}\\-\\w{4}\\-\\w{4}")
//            if ("" == meetingId) {
//                ReusedMethod.displayMessage(this,"Please enter meeting ID")
        //            } else if (!pattern.matches(meetingId)) {
//                ReusedMethod.displayMessage(this,"Please enter valid meeting ID")
//            } else {
//                getToken(meetingId)
//            }
//        }
    }


    private fun isNullOrEmpty(str: String?): Boolean {
        return "null" == str || "" == str || null == str
    }


    private fun getToken(meetingId: String?) {
        if (!ReusedMethod.isNetworkConnected(this@CreateOrJoinActivity)) {
            return
        }
        if (!isNullOrEmpty( resources.getString(R.string.video_call_auth)) && !isNullOrEmpty(AUTH_URL)) {
            ReusedMethod.displayMessage(
                this,
                "Please Provide only one - either auth_token or auth_url"
            )
            return
        }
        if (!isNullOrEmpty( resources.getString(R.string.video_call_auth))) {
            if (meetingId == null) {
                createMeeting( resources.getString(R.string.video_call_auth))
            } else {
                joinMeeting( resources.getString(R.string.video_call_auth))
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
                        ReusedMethod.displayMessage(this@CreateOrJoinActivity, anError.errorDetail)
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
                    Log.e("VIDEO_CALL", "CREATE_MEATING_ERROR:    " + anError)
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
                    Log.e("VIDEO_CALL", "JOIN_MEATING_ERRRO:    " + anError)

                }
            })
    }
}