package com.app.guardian.ui.User.MyVideos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.AppConstants.EXTRA_ACCESS_LOCATION_PERMISSION
import com.app.guardian.common.AppConstants.EXTRA_CAMERA_PERMISSION
import com.app.guardian.common.AppConstants.EXTRA_MANAGE_LOCATION_PERMISSION
import com.app.guardian.common.AppConstants.EXTRA_READ_STORAGE_PERMISSION
import com.app.guardian.common.AppConstants.EXTRA_WRITE_STORAGE_PERMISSION
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentMyVideosBinding
import com.app.guardian.model.Video.VideoResp
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.User.MyVideos.adapter.MyVideoListAdapter
import com.app.guardian.ui.VideoPlayer.VideoPlayerActivity
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [MyVideosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyVideosFragment : BaseFragment(), View.OnClickListener {
    var myVideoListAdapter: MyVideoListAdapter? = null
    lateinit var mBinding: FragmentMyVideosBinding
    var isShow = false
    var arrayList = ArrayList<VideoResp>()
    override fun getInflateResource(): Int {
        return R.layout.fragment_my_videos

    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.contacted_history),
            false,
            false
        )

        mBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb1) {
                mBinding.clOfflineVideos.visible()
                mBinding.tvUploadVideos.gone()
                mBinding.ivVideo.gone()
                isShow = false
            } else {
                mBinding.clOfflineVideos.gone()
                mBinding.tvUploadVideos.visible()
                mBinding.ivVideo.visible()
                isShow = true
            }

        }


    }


    override fun onResume() {
        super.onResume()
        if (mBinding.rb1.isChecked) {
            mBinding.clOfflineVideos.visible()
            mBinding.tvUploadVideos.gone()
            mBinding.ivVideo.gone()

        } else {
            mBinding.clOfflineVideos.gone()
            mBinding.tvUploadVideos.visible()
            mBinding.ivVideo.visible()
        }
        setAdapter()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissions(
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                EXTRA_ACCESS_LOCATION_PERMISSION
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            checkPermissions(
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                EXTRA_MANAGE_LOCATION_PERMISSION
            )
        }
        checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, EXTRA_READ_STORAGE_PERMISSION)
        checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, EXTRA_WRITE_STORAGE_PERMISSION)

        checkPermissions(
            Manifest.permission.CAMERA,
            EXTRA_CAMERA_PERMISSION
        )
//        getAllVideos()


        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            .toString() + "/" + resources.getString(R.string.app_name) + "/"
        val f = File(path);
        val file = f.listFiles()
        Log.d("Files", "FileName:" + file)
        for (element in file) {
            Log.d("Files", "FileName:" + element.name)
        }


    }

    private fun getAllVideos() {
        arrayList.clear()
        val selection = MediaStore.Video.Media.DATA + " like?"
        val selectionArgs = arrayOf(
            "%" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + resources.getString(
                R.string.app_name
            ) + "/"
        )
        val videocursor: Cursor? = requireActivity().contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            null, selection, selectionArgs, null
        )
        Log.i("VIDEO_CURSOR", videocursor?.count.toString())
        Log.i("VIDEO_CURSOR", videocursor?.columnNames?.size.toString())
        Log.i("VIDEO_CURSOR", videocursor?.position.toString())
        Log.i("VIDEO_CURSOR", videocursor?.columnCount.toString())
        if (videocursor != null && videocursor.moveToNext()) {
            do {
                arrayList.add(
                    VideoResp(
//                        videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media._ID))
//                            .toInt(),
//                        videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.TITLE))
//                            .toString(),
//                        videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.DATA))
//                            .toString()
                        videocursor.getString(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                            .toInt(),
                        videocursor.getString(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                            .toString(),
                        videocursor.getString(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                            .toString()
                    )

                )
            } while (videocursor.moveToNext())
        }
        Log.i("THIS_STRING", arrayList.toString())
        myVideoListAdapter?.updateAll(arrayList)

    }

    private fun checkPermissions(permissions: String, reqcode: Int): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                permissions
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permissions), reqcode)
            false
        } else {
            true
        }
    }

    private fun setAdapter() {
        myVideoListAdapter =
            MyVideoListAdapter(
                requireContext(),
                isShow,
                object : MyVideoListAdapter.onItemClicklisteners {
                    override fun onItemClick(position: Int?) {
                        startActivity(
                            Intent(
                                requireActivity(),
                                VideoPlayerActivity::class.java
                            ).putExtra(AppConstants.EXTRA_PATH, arrayList[position!!].path)
                        )
                        requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
                        Log.i("THIS_STRING", arrayList[position].title)
                    }

                })
        mBinding.rv.adapter = myVideoListAdapter
        myVideoListAdapter!!.updateAll(arrayList)
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.ivVideo.setOnClickListener(this)
        mBinding.ivBack.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivVideo -> {
                makeFolder()
            }
            R.id.ivBack -> {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun makeFolder() {
        if (checkPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                EXTRA_READ_STORAGE_PERMISSION
            )
            && checkPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                EXTRA_WRITE_STORAGE_PERMISSION
            )
            && checkPermissions(
                Manifest.permission.CAMERA,
                EXTRA_CAMERA_PERMISSION
            )
            && checkPermissions(
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                EXTRA_ACCESS_LOCATION_PERMISSION
            )
        ) {

            val f = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                resources.getString(R.string.app_name)
            )
            if (!f.exists()) {
                f.mkdirs()
            }
            val recordFile = File(
                f,
                SharedPreferenceManager.getUser()!!.user.id.toString() + "_" + System.currentTimeMillis() + ".mp4"
            )
            val recorduri = FileProvider.getUriForFile(
                requireContext(),
                "com.video.record.fileprovider",
                recordFile
            )

            val i = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            i.putExtra(MediaStore.EXTRA_OUTPUT, recorduri)

            startActivityForResult(i, 1001)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EXTRA_WRITE_STORAGE_PERMISSION || requestCode == EXTRA_ACCESS_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeFolder()
            } else {
                ReusedMethod.displayMessage(requireActivity(), "Storage Permission Denied")

            }
        }
    }
}