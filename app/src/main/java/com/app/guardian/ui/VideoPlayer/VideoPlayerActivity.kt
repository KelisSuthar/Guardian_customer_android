package com.app.guardian.ui.VideoPlayer

import android.net.Uri
import android.util.Log
import android.widget.MediaController
import androidx.core.content.FileProvider
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.databinding.ActivityVideoPlayerBinding
import com.app.guardian.shareddata.base.BaseActivity
import java.io.File

class VideoPlayerActivity : BaseActivity() {
    lateinit var mBinding: ActivityVideoPlayerBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_video_player
    }

    override fun initView() {
        mBinding = getBinding()
        if (intent != null && intent.extras != null) {
            Log.i("THIS_APP", intent.getStringExtra(AppConstants.EXTRA_PATH).toString())


//            mBinding.videoView.setVideoURI(
//                Uri.fromFile(
//                    File(
//                        intent.getStringArrayExtra(AppConstants.EXTRA_PATH).toString()
//                    )
//                )
//            )
            mBinding.videoView.setMediaController(MediaController(this));
            mBinding.videoView.requestFocus()

            mBinding.videoView.requestFocus();
            mBinding.videoView.start();
        }

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

}