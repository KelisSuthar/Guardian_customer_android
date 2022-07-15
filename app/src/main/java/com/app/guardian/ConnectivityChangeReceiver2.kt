package com.app.guardian

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.NetworkOnMainThreadException
import android.util.Log
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.injection.OAuthInterceptor
import com.app.guardian.model.CommonResponseModel
import com.app.guardian.model.OfflineVideos.UploadOfflineVideoResp
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.endpoint.ApiEndPoint
import com.app.guardian.utils.ApiConstant
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.webrtc.ContextUtils.getApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class ConnectivityChangeReceiver2 : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {


        val paths =
            intent?.getStringArrayListExtra("file_path")
        Log.i("ConnectivityChange_2", "RESUME")
        Log.i("ConnectivityChange_2", paths.toString())
        for (i in paths?.indices!!) {
            val file = File(paths[i].toString())

            if (file.exists()) {
                Log.i("THIS_FILE", file.absolutePath)
                uploadFile(file, context!!)
            }
        }


    }
}

private fun uploadFile(selectedFile: File?, context: Context) {
    var attachmentUrl = ""
    try {
        val options = StorageUploadFileOptions.defaultInstance()
        val clientConfig = ClientConfiguration()
        clientConfig.socketTimeout = 120000
        clientConfig.connectionTimeout = 10000
        clientConfig.maxErrorRetry = 2
        clientConfig.protocol = Protocol.HTTP

        val credentials = BasicAWSCredentials(

            context.resources.getString(R.string.aws_access_1) + context.resources.getString(R.string.aws_access_2),
            context.resources.getString(R.string.aws_sec_1) + context.resources.getString(R.string.aws_sec_2) + context.resources.getString(
                R.string.aws_sec_3
            )
        )
        val s3 = AmazonS3Client(credentials, clientConfig)
        s3.setRegion(Region.getRegion(Regions.US_EAST_2))
        Amplify.Storage.uploadFile(selectedFile?.name.toString(), selectedFile!!, options, {
            Log.i("MyAmplifyApp_BROADCAST", "Fraction completed: ${it.fractionCompleted}")
        },
            {

                Log.i("MyAmplifyApp_BROADCAST", "Successfully uploaded: ${it.key}")
                attachmentUrl =
                    "${context.resources.getString(R.string.aws_base_url)}${selectedFile?.name}"
                Log.i("attachmentUrl", attachmentUrl)
                Handler().postDelayed(
                    {
                        Log.i("THIS_FILE", selectedFile.absolutePath)
                        if (selectedFile.exists()) {
                            selectedFile.canonicalFile.delete()
                            if (selectedFile.exists()) {
                                getApplicationContext().deleteFile (selectedFile.name);

                                Log.i("MyAmplifyApp_BROADCAST", "FILE Deleted Successfully")
                                postData(attachmentUrl)
                            } else {
                                Log.i("MyAmplifyApp_BROADCAST", "FILE IS NOT DELETED")
                            }
                        } else {
                            Log.i("MyAmplifyApp_BROADCAST", "FILE IS NOT EXIXTS")
                        }
                    }, 2000
                )


//                    HomeActivity().callUploadOfflienVideoAPI(attachmentUrl)


            },
            {

                Log.i("MyAmplifyApp_BROADCAST", "Upload failed", it)
            }
        )
    } catch (exception: Exception) {
        Log.i("MyAmplifyApp_BROADCAST", "Upload failed", exception)
    } catch (e: NetworkOnMainThreadException) {
        Log.i("MyAmplifyApp_BROADCAST", "Upload failed$e.message")
    }
}

private fun postData(attachmentUrl: String) {
    val berearToken: String? = SharedPreferenceManager.getString(AppConstants.BEREAR_TOKEN, "")
    val client = OkHttpClient.Builder()
        .addInterceptor(OAuthInterceptor("Bearer", berearToken.toString()))
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val retrofitAPI: ApiEndPoint = retrofit.create(ApiEndPoint::class.java)
    val body = JsonObject()
    body.addProperty(ApiConstant.EXTRAS_VIDEO_URL, attachmentUrl)

    val call: Call<CommonResponseModel<UploadOfflineVideoResp>> =
        retrofitAPI.uploadOfflineVideos(body)

    call.enqueue(object : Callback<CommonResponseModel<UploadOfflineVideoResp>> {
        override fun onResponse(
            call: Call<CommonResponseModel<UploadOfflineVideoResp>>,
            response: Response<CommonResponseModel<UploadOfflineVideoResp>>
        ) {
            Log.i("MyAmplifyApp_BROADCAST", "Upload API SUCCESSFULLY")
            Log.i("MyAmplifyApp_BROADCAST", response.toString())
        }

        override fun onFailure(
            call: Call<CommonResponseModel<UploadOfflineVideoResp>>,
            t: Throwable
        ) {
            Log.i("MyAmplifyApp_BROADCAST", "Upload API FAILED")
        }


    })
}


