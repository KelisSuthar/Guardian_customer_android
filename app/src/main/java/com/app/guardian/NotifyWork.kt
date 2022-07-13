package com.app.guardian

import android.content.Context
import android.os.NetworkOnMainThreadException
import android.util.Log
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.app.guardian.common.AppConstants
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.model.CommonResponseModel
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.OfflineVideos.UploadOfflineVideoResp
import com.app.guardian.model.Video.VideoResp
import com.app.guardian.shareddata.endpoint.ApiEndPoint
import com.app.guardian.utils.ApiConstant
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class NotifyWork(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {


        val paths =
            inputData.getString("file_path")


        val chars = listOf(paths)
        Log.e("MyAmplifyApp_BROADCAST", chars.toString())
        chars.forEach {
            val c = listOf(it)
            c.forEach {i->
                Log.e("MyAmplifyApp_BROADCAST", i.toString())
            }
        }
//        paths!!.forEach {
//            Log.e("MyAmplifyApp_BROADCAST", it + "\n\n")
//        }


//        handler.postDelayed(Runnable {
//            handler.postDelayed(runnable!!, 2000)
//        val path =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//                .toString() + "/" + applicationContext!!.resources.getString(R.string.app_name) + "/"
//        val f = File(path);
//        val file = f.listFiles()
//        if (!file.isNullOrEmpty()) {
//            for (element in file) {
//                Log.d("Files", "FileName:" + element.name)
//                Log.d(
//                    "Files",
//                    "FileName:" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//                        .toString() + "/" + applicationContext!!.resources.getString(R.string.app_name) + "/" + element.name
//                )
//                val file = File(
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//                        .toString() + "/" + applicationContext!!.resources.getString(R.string.app_name) + "/" + element.name
//                )
//                if (SharedPreferenceManager.getBoolean(
//                        AppConstants.IS_OFFLINE_VIDEO_UPLOAD,
//                        false
//                    )
//                ) {
//                    if (ReusedMethod.isNetworkConnected(applicationContext)) {
//                        uploadFile(file, applicationContext)
//                    }
//
//                }
//
//
//            }
//        }

        return success()
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
                    if (selectedFile.exists()) {
                        selectedFile.delete()
                    }
//                    HomeActivity().callUploadOfflienVideoAPI(attachmentUrl)
                    postData(attachmentUrl)

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

    class OAuthInterceptor(private val tokenType: String, private val acceessToken: String) :
        Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()
            request =
                request.newBuilder().header("Authorization", "$tokenType $acceessToken").build()

            return chain.proceed(request)
        }
    }
}

