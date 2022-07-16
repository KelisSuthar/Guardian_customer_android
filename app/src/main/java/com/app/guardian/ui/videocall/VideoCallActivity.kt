package com.app.guardian.ui.videocall

import android.Manifest
import android.annotation.TargetApi
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.GuardianApplication
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.databinding.ActivityVideoCallBinding
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.editProfile.EditProfileActivity
import com.app.guardian.ui.videocall.adapter.ParticipantAdapter
import com.app.guardian.utils.Config
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import live.videosdk.rtc.android.Meeting
import live.videosdk.rtc.android.Participant
import live.videosdk.rtc.android.Stream
import live.videosdk.rtc.android.VideoSDK
import live.videosdk.rtc.android.lib.AppRTCAudioManager
import live.videosdk.rtc.android.lib.PeerConnectionUtils
import live.videosdk.rtc.android.listeners.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class VideoCallActivity : BaseActivity() {
    lateinit var mBinding: ActivityVideoCallBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    private var svrShare: SurfaceViewRenderer? = null
    private var btnMic: FloatingActionButton? = null
    private var btnWebcam: FloatingActionButton? = null
    private var micEnabled = true
    private var webcamEnabled = true
    private var recording = false
    private var livestreaming = false
    private var localScreenShare = false
    private var isNetworkAvailable = true
    var is_join = false
    var start_time = ReusedMethod.getCurrentDate()
    var end_time = ReusedMethod.getCurrentDate()
    private var btnScreenShare: FloatingActionButton? = null
    override fun getResource(): Int {
        return R.layout.activity_video_call
    }

    override fun initView() {
        mBinding = getBinding()
        btnScreenShare = findViewById(R.id.btnScreenShare)
        svrShare = findViewById(R.id.svrShare)
        svrShare!!.init(PeerConnectionUtils.getEglContext(), null)
        btnMic = findViewById(R.id.btnMic)
        btnWebcam = findViewById(R.id.btnWebcam)
        val token = intent.getStringExtra("token")
        val meetingId = intent.getStringExtra("meetingId")
        micEnabled = intent.getBooleanExtra("micEnabled", true)
        webcamEnabled = intent.getBooleanExtra("webcamEnabled", true)
        var participantName = intent.getStringExtra("paticipantName")
        if (participantName == null) {
            participantName = "John Doe"
        }
        toggleMicIcon()
        toggleWebcamIcon()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = meetingId

        // pass the token generated from api server
        VideoSDK.config(token)

        // create a new meeting instance
        meeting = VideoSDK.initMeeting(
            this@VideoCallActivity, meetingId, participantName,
            micEnabled, webcamEnabled
        )

        (this.application as GuardianApplication).meeting = meeting
        meeting!!.addEventListener(meetingEventListener)

        //set recycerview
        val rvParticipants = findViewById<RecyclerView>(R.id.rvParticipants)
        rvParticipants.layoutManager = GridLayoutManager(this, 2)
        rvParticipants.adapter = ParticipantAdapter(meeting!!)

        // Local participant listeners
        setLocalListeners()

        //
        checkPermissions()

        // Actions
        setActionListeners()

        setAudioDeviceListeners()

        (findViewById<View>(R.id.btnCopyContent) as ImageButton).setOnClickListener(
            View.OnClickListener { copyTextToClipboard(meetingId) })
        (findViewById<View>(R.id.btnAudioSelection) as ImageButton).setOnClickListener { showAudioInputDialog() }
        is_join = intent.getBooleanExtra(AppConstants.IS_JOIN, false)

    }

    override fun initObserver() {
        mViewModel.getSendEndCallResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            meeting!!.end()
//
//                            ReusedMethod.displayMessage(this, it.message.toString())
//                            val intents = Intent(this@VideoCallActivity, HomeActivity::class.java)
//                            intents.addFlags(
//                                (Intent.FLAG_ACTIVITY_NEW_TASK
//                                        or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                            )
//                            startActivity(intents)
//                            finish()
                        } else {
                            ReusedMethod.displayMessage(this, it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                this,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {}
                    }
                }
            }
        }
    }

    override fun handleListener() {

    }

    private fun copyTextToClipboard(text: String?) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this@VideoCallActivity, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    private fun showAudioInputDialog() {
        val mics = meeting!!.mics

        // Prepare list
        val items = arrayOfNulls<String>(mics.size)
        for (i in mics.indices) {
            items[i] = mics.toTypedArray()[i].toString()
        }
        MaterialAlertDialogBuilder(this@VideoCallActivity)
            .setTitle(getString(R.string.audio_options))
            .setItems(items) { dialog: DialogInterface?, which: Int ->
                var audioDevice: AppRTCAudioManager.AudioDevice? = null
                when (items.get(which)) {
                    "BLUETOOTH" -> audioDevice = AppRTCAudioManager.AudioDevice.BLUETOOTH
                    "WIRED_HEADSET" -> audioDevice = AppRTCAudioManager.AudioDevice.WIRED_HEADSET
                    "SPEAKER_PHONE" -> audioDevice = AppRTCAudioManager.AudioDevice.SPEAKER_PHONE
                    "EARPIECE" -> audioDevice = AppRTCAudioManager.AudioDevice.EARPIECE
                }
                meeting!!.changeMic(audioDevice)
            }
            .show()
    }

    private fun toggleMicIcon() {
        if (micEnabled) {
            btnMic!!.setImageResource(R.drawable.ic_baseline_mic_24)
            btnMic!!.setColorFilter(Color.WHITE)
            btnMic!!.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
        } else {
            btnMic!!.setImageResource(R.drawable.ic_baseline_mic_off_24)
            btnMic!!.setColorFilter(Color.BLACK)
            btnMic!!.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.md_grey_300))
        }
    }


    private fun toggleWebcamIcon() {
        if (webcamEnabled) {
            btnWebcam!!.setImageResource(R.drawable.ic_baseline_videocam_24)
            btnWebcam!!.setColorFilter(Color.WHITE)
            btnWebcam!!.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
        } else {
            btnWebcam!!.setImageResource(R.drawable.ic_baseline_videocam_off_24)
            btnWebcam!!.setColorFilter(Color.BLACK)
            btnWebcam!!.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.md_grey_300))
        }
    }

    private val meetingEventListener: MeetingEventListener = object : MeetingEventListener() {
        override fun onMeetingJoined() {
            Log.d("#meeting", "onMeetingJoined()")

            // notify user of any new messages
            meeting!!.pubSub.subscribe(
                "CHAT"
            ) { pubSubMessage ->
                if (pubSubMessage.senderId != meeting!!.localParticipant.id) {
                    val parentLayout = findViewById<View>(android.R.id.content)
                    Snackbar.make(
                        parentLayout, (pubSubMessage.senderName + " says: " +
                                pubSubMessage.message), Snackbar.LENGTH_SHORT
                    )
                        .setDuration(2000).show()
                }
            }

            //terminate meeting in 10 minutes
//            Handler().postDelayed({
//                if (!isDestroyed) MaterialAlertDialogBuilder(this@VideoCallActivity)
//                    .setTitle("Meeting Left")
//                    .setMessage("Demo app limits meeting to 10 Minutes")
//                    .setCancelable(false)
//                    .setPositiveButton("Ok") { dialog: DialogInterface?, which: Int ->
//                        if (!isDestroyed) meeting!!.leave()
//                        Log.d("Auto Terminate", "run: Meeting Terminated")
//                    }
//                    .create().show()
//            }, 600000)
        }

        override fun onMeetingLeft() {
            Log.d("#meeting", "onMeetingLeft()")
            Log.d("#meeting_USER", "onMeetingLeft()")
            meeting = null
            if (!isDestroyed) {
                val intents = Intent(this@VideoCallActivity, EditProfileActivity::class.java)
                intents.addFlags(
                    (Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY)
                )
                startActivity(intents)
//                finish()
//                startActivity(
//                    Intent(
//                        this@VideoCallActivity,
//                        HomeActivity::class.java
//
//                    )
//                )
//                finish()
            }
        }

        override fun onParticipantJoined(participant: Participant) {

            Toast.makeText(
                this@VideoCallActivity, participant.displayName + " joined",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onParticipantLeft(participant: Participant) {
            Log.d("#meeting_USER", "onParticipantLeft()")
            Toast.makeText(
                this@VideoCallActivity, participant.displayName + " left",
                Toast.LENGTH_SHORT
            ).show()

        }

        override fun onPresenterChanged(participantId: String?) {
            updatePresenter(participantId)
        }

        override fun onRecordingStarted() {
            recording = true
            (findViewById<View>(R.id.recordIcon)).visibility = View.VISIBLE
            Toast.makeText(
                this@VideoCallActivity, "Recording started",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onRecordingStopped() {
            recording = false
            (findViewById<View>(R.id.recordIcon)).visibility = View.GONE
            Toast.makeText(
                this@VideoCallActivity, "Recording stopped",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onLivestreamStarted() {
            livestreaming = true
            Toast.makeText(
                this@VideoCallActivity, "Livestream started",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onLivestreamStopped() {
            livestreaming = false
            Toast.makeText(
                this@VideoCallActivity, "Livestream stopped",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onMicRequested(participantId: String, listener: MicRequestListener) {
            showMicRequestDialog(listener)
        }

        override fun onWebcamRequested(participantId: String, listener: WebcamRequestListener) {
            showWebcamRequestDialog(listener)
        }

        override fun onExternalCallStarted() {
            Toast.makeText(this@VideoCallActivity, "onExternalCallStarted", Toast.LENGTH_SHORT)
                .show()
            Log.d("#meeting", "onExternalCallAnswered: User Answered a Call")
        }
    }

    private fun showMicRequestDialog(listener: MicRequestListener) {
        MaterialAlertDialogBuilder(this@VideoCallActivity)
            .setTitle("Mic requested")
            .setMessage("Host is asking you to unmute your mic, do you want to allow ?")
            .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int -> listener.accept() }
            .setNegativeButton("No") { dialog: DialogInterface?, which: Int -> listener.reject() }
            .show()
    }

    private fun showWebcamRequestDialog(listener: WebcamRequestListener) {
        MaterialAlertDialogBuilder(this@VideoCallActivity)
            .setTitle("Webcam requested")
            .setMessage("Host is asking you to enable your webcam, do you want to allow ?")
            .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int -> listener.accept() }
            .setNegativeButton("No") { dialog: DialogInterface?, which: Int -> listener.reject() }
            .show()
    }

    private fun updatePresenter(participantId: String?) {
        if (participantId == null) {
            svrShare!!.clearImage()
            svrShare!!.visibility = View.GONE
            btnScreenShare!!.isEnabled = true
            return
        } else {
            btnScreenShare!!.isEnabled = (meeting!!.localParticipant.id == participantId)
        }

        // find participant
        val participant = meeting!!.participants[participantId] ?: return

        // find share stream in participant
        var shareStream: Stream? = null
        for (stream: Stream in participant.streams.values) {
            if ((stream.kind == "share")) {
                shareStream = stream
                break
            }
        }
        if (shareStream == null) return

        // display share video
        svrShare!!.visibility = View.VISIBLE
        svrShare!!.setZOrderMediaOverlay(true)
        svrShare!!.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        val videoTrack = shareStream.track as VideoTrack
        videoTrack.addSink(svrShare)

        // listen for share stop event
        participant.addEventListener(object : ParticipantEventListener() {
            override fun onStreamDisabled(stream: Stream) {
                if ((stream.kind == "share")) {
                    val track: VideoTrack = stream.track as VideoTrack
                    track.removeSink(svrShare)
                    svrShare!!.clearImage()
                    svrShare!!.visibility = View.GONE
                    localScreenShare = false
                }
            }
        })
    }

    private fun setLocalListeners() {
        meeting!!.localParticipant.addEventListener(object : ParticipantEventListener() {
            override fun onStreamEnabled(stream: Stream) {
                Log.d("TAG", "onStreamEnabled: " + stream.kind)
                if (stream.kind.equals("video", ignoreCase = true)) {
                    webcamEnabled = true
                    toggleWebcamIcon()
                } else if (stream.kind.equals("audio", ignoreCase = true)) {
                    micEnabled = true
                    toggleMicIcon()
                } else if (stream.kind.equals("share", ignoreCase = true)) {
                    // display share video
                    svrShare!!.visibility = View.VISIBLE
                    svrShare!!.setZOrderMediaOverlay(true)
                    svrShare!!.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                    val videoTrack = stream.track as VideoTrack
                    videoTrack.addSink(svrShare)
                    //
                    localScreenShare = true
                }
            }

            override fun onStreamDisabled(stream: Stream) {
                when {
                    stream.kind.equals("video", ignoreCase = true) -> {
                        webcamEnabled = false
                        toggleWebcamIcon()
                    }
                    stream.kind.equals("audio", ignoreCase = true) -> {
                        micEnabled = false
                        toggleMicIcon()
                    }
                    stream.kind.equals("share", ignoreCase = true) -> {
                        val track: VideoTrack = stream.track as VideoTrack
                        track.removeSink(svrShare)
                        svrShare!!.clearImage()
                        svrShare!!.visibility = View.GONE
                        //
                        localScreenShare = false
                    }
                }
            }
        })
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
        )
        val rationale = "Please provide permissions"
        val options =
            Permissions.Options().setRationaleDialogTitle("Info").setSettingsDialogTitle("Warning")
        Permissions.check(this, permissions, rationale, options, permissionHandler)
    }

    private val permissionHandler: PermissionHandler = object : PermissionHandler() {
        override fun onGranted() {
            if (meeting != null) meeting!!.join()
        }
    }

    private fun setActionListeners() {

        // Toggle mic
        btnMic!!.setOnClickListener { _: View? ->
            if (micEnabled) {
                meeting!!.muteMic()
            } else {
                meeting!!.unmuteMic()
            }
        }

        // Toggle webcam
        btnWebcam!!.setOnClickListener {
            if (webcamEnabled) {
                meeting!!.disableWebcam()
            } else {
                meeting!!.enableWebcam()
            }
        }

        // Leave meeting
        findViewById<View>(R.id.btnLeave).setOnClickListener { view: View? -> showLeaveOrEndDialog() }
        findViewById<View>(R.id.btnMore).setOnClickListener { v: View? -> showMoreOptionsDialog() }
        findViewById<View>(R.id.btnSwitchCameraMode).setOnClickListener { view: View? -> meeting!!.changeWebcam() }

        /*   // Chat
           findViewById<View>(R.id.btnChat).setOnClickListener { view: View? ->
               val intent = Intent(this@VideoCallActivity, ChatActivity::class.java)
               startActivity(intent)
           }*/

        //
        btnScreenShare!!.setOnClickListener { view: View? -> toggleScreenSharing() }
    }

    private fun toggleScreenSharing() {
        if (!localScreenShare) {
            askPermissionForScreenShare()
        } else {
            meeting!!.disableScreenShare()
            btnScreenShare!!.setImageResource(R.drawable.ic_outline_screen_share_24)
        }
        localScreenShare = !localScreenShare
    }

    @TargetApi(21)
    private fun askPermissionForScreenShare() {
        val mediaProjectionManager = application.getSystemService(
            MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE
        )
    }

    private fun setAudioDeviceListeners() {
        meeting!!.setAudioDeviceChangeListener(object : AppRTCAudioManager.AudioManagerEvents {
            override fun onAudioDeviceChanged(
                selectedAudioDevice: AppRTCAudioManager.AudioDevice,
                availableAudioDevices: Set<AppRTCAudioManager.AudioDevice>
            ) {
                when (selectedAudioDevice) {
                    AppRTCAudioManager.AudioDevice.BLUETOOTH -> (findViewById<View>(R.id.btnAudioSelection) as ImageButton).setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext, R.drawable.ic_baseline_bluetooth_audio_24
                        )
                    )
                    AppRTCAudioManager.AudioDevice.WIRED_HEADSET -> (findViewById<View>(R.id.btnAudioSelection) as ImageButton).setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext, R.drawable.ic_baseline_headset_24
                        )
                    )
                    AppRTCAudioManager.AudioDevice.SPEAKER_PHONE -> (findViewById<View>(R.id.btnAudioSelection) as ImageButton).setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext, R.drawable.ic_baseline_volume_up_24
                        )
                    )
                    AppRTCAudioManager.AudioDevice.EARPIECE -> (findViewById<View>(R.id.btnAudioSelection) as ImageButton).setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext, R.drawable.ic_baseline_phone_in_talk_24
                        )
                    )
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showLeaveOrEndDialog()
    }

    private fun showLeaveOrEndDialog() {
        val dialog = ReusedMethod.setUpDialog(this, R.layout.dialog_layout, false)
        val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
        val TITLE = dialog.findViewById<TextView>(R.id.tvTitle)
        val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
        val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)

        MESSAGE.gone()
        CANCEL.text = "No"
        OK.text = "Yes"
        TITLE.text = "Leave from meeting or end the meeting for everyone ?"
        CANCEL.isAllCaps = false
        OK.isAllCaps = false
        CANCEL.setOnClickListener {
            dialog.dismiss()
        }
        OK.setOnClickListener {
            dialog.dismiss()
            try {
                if (intent.getBooleanExtra(AppConstants.IS_JOIN, false)) {
                    meeting?.leave()
                } else {
                    callEndMeetingAPI()
                }

            } catch (e: Exception) {
                Log.e("THIS_APP", e.toString())
            }
        }

        dialog.show()
//        MaterialAlertDialogBuilder(this@VideoCallActivity)
//            .setTitle("Leave or End meeting")
//            .setMessage("Leave from meeting or end the meeting for everyone ?")
//            .setPositiveButton(
//                "Leave"
//            ) { dialog: DialogInterface?, which: Int -> meeting!!.leave() }
//            .setNegativeButton("End") { dialog: DialogInterface?, which: Int ->
//                run {
//                    startActivity(
//                        Intent(
//                            this@VideoCallActivity,
//                            HomeActivity::class.java
//
//                        )
//                    )
//                    meeting!!.end()
//                }
//
//            }
//            .show()
    }

    private fun callEndMeetingAPI() {

        end_time = ReusedMethod.getCurrentDate()
        val duration_min = getMinDuration(start_time, end_time)
        if (ReusedMethod.isNetworkConnected(this)) {
            mViewModel.sendEndCallReq(
                true,
                this@VideoCallActivity,
                intent.getStringExtra(AppConstants.EXTRA_CALLING_HISTORY_ID).toString(),
                "",
                start_time.toString(),
                end_time.toString(),
                duration_min
            )
        } else {
            ReusedMethod.displayMessage(this, resources.getString(R.string.text_error_network))
        }
    }

    private fun getMinDuration(start_time: String, end_time: String): String {
        var final_time = ""

        val date2: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time)
        val date1: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_time)

        val difference: Long = date2.time - date1.time
        val hrs = (difference / (1000 * 60 * 60))
        val min = (difference / (1000 * 60)) % 60
        Log.i("======= Hours", " :: $hrs")
        Log.i("======= MIN", " :: $min")

        final_time = if (hrs.toInt() > 0) {
            abs(min)
            min.toString()
        } else {
            abs(min)
            abs(hrs)
            (((hrs * 60) + min).toString())
        }
        return final_time
    }

    private fun showMoreOptionsDialog() {
        val items = arrayOf(
            if (recording) "Stop recording" else "Start recording",
            //   if (livestreaming) "Stop livestreaming" else "Start livestreaming"
        )
        MaterialAlertDialogBuilder(this@VideoCallActivity)
            .setTitle(getString(R.string.more_options))
            .setItems(items) { dialog: DialogInterface?, which: Int ->
                when (which) {
                    0 -> {
                        toggleRecording()
                    }
                    /* 1 -> {
                         toggleLivestreaming()
                     }*/
                }
            }
            .show()
    }

    private fun toggleRecording() {
        if (!recording) {
            meeting!!.startRecording(null)
        } else {
            meeting!!.stopRecording()
        }
    }


    companion object {
        private var meeting: Meeting? = null
        private val YOUTUBE_RTMP_URL: String? =
            "rtmp://7f63aa16-6f48-4438-8668-b7e8c71327fe.dacastmmd.pri.lldns.net/dacastmmd"
        private val YOUTUBE_RTMP_STREAM_KEY: String? = "46751b80d07f4f0cb27aa4f19181b776_4500"
        private val CAPTURE_PERMISSION_REQUEST_CODE = 1
    }
}