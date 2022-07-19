package com.app.guardian.ui.VideoPlayer


import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import android.widget.MediaController
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.loadWebViewData
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityVideoPlayerBinding
import com.app.guardian.shareddata.base.BaseActivity


class VideoPlayerActivity : BaseActivity() {
    lateinit var mBinding: ActivityVideoPlayerBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_video_player
    }

    override fun initView() {
        mBinding = getBinding()
        if (intent != null && intent.extras != null) {
            Log.i("THIS_APP_PATH", intent.getStringExtra(AppConstants.EXTRA_PATH).toString())
            val path = intent.getStringExtra(AppConstants.EXTRA_PATH).toString()
            if (path.startsWith("http://") || path.startsWith("https://")) {
                mBinding.videoView.gone()
                mBinding.webview.visible()
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                mBinding.webview.loadWebViewData(path)
            } else {
                mBinding.videoView.visible()
                mBinding.webview.gone()
                mBinding.videoView.setVideoURI(
                    Uri.parse(
                        intent.getStringExtra(AppConstants.EXTRA_PATH).toString()
                    )
                )
            }
            val mediaController = MediaController(this)
            mediaController.setAnchorView(mBinding.videoView);
            mediaController.setMediaPlayer(mBinding.videoView);
            mBinding.videoView.setMediaController(mediaController);

            mBinding.videoView.start();
        }

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

}