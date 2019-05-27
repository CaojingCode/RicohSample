package com.caojing.ricohsample

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.caojing.ricohsample.v2.GLPhotoActivity
import com.caojing.ricohsample.v2.network.HttpConnector
import com.caojing.ricohsample.v2.network.HttpEventListener
import com.jjw.ricohlibrary.AppRepository
import com.jjw.ricohlibrary.CompanionClass
import com.jjw.ricohlibrary.RicohNetWork
import com.jjw.ricohlibrary.callback.UpdateViewCallBack
import com.jjw.ricohlibrary.view.MjpegInputStream
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity(), UpdateViewCallBack {

    var thread: Thread? = null
    var handler: Handler = Handler()

    override fun livePreviewInputStream(inputStream: MjpegInputStream) {
        initMjpegView(inputStream)
    }


    private fun initMjpegView(mis: MjpegInputStream?) {
        if (mis == null) {
            ToastUtils.showShort("Mjpeg数据流为空")
            return
        }
        liveView.setSource(mis)// 设置数据来源
//        liveView.displayMode = liveView.displayMode/*设置mjpegview的显示模式*/
//        /**
//         * setFps和getFps方法是为了在屏幕的右上角动态显示当前的帧率
//         * 如果我们只需观看画面，下面这句完全可以省去
//         */
//        liveView.fps = liveView.fps
//        /**
//         * 调用mjpegView中的线程的run方法，开始显示画面
//         */
////        liveView.displayMode = MjpegView.FULLSCREEN_MODE/*全屏*/
//        liveView.startPlay()
    }

    var appRepository: AppRepository = AppRepository.getInstance()

    override fun updateText(text: String) {
        tvState.text = text
        if (text.contains("设置参数成功")) {
            showLiveViewTask?.execute()
        }
    }

    override fun ImageFile(imageFilePath: String) {
        ivImage.setImageBitmap(ImageUtils.getBitmap(imageFilePath))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (RicohNetWork.isWifiState()) {
            buttonTakePic.isEnabled = true
            appRepository.updateViewCallBack = this
            appRepository.requestStartSession()
        } else {
            buttonTakePic.isEnabled = false
        }
        buttonTakePic.setOnClickListener {
            buttonTakePic.isEnabled = false
//            ShootTask().execute()
            AppRepository.getInstance().takePicture()
        }

        AppRepository.getInstance().info()
        AppRepository.getInstance().getState()
        showLiveViewTask = ShowLiveViewTask()
    }


    override fun onPause() {
        super.onPause()
        liveView.stopPlay()
    }

    var showLiveViewTask: ShowLiveViewTask? = null
    override fun onResume() {
        super.onResume()
        liveView.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        CompanionClass.closeSession
        if (thread != null)
            thread!!.stop()
    }

    inner class ShowLiveViewTask : AsyncTask<String, String, MjpegInputStream>() {
        override fun doInBackground(vararg ipAddress: String): MjpegInputStream? {
            var mjis: MjpegInputStream? = null
            val MAX_RETRY_COUNT = 20

            var retryCount = 0
            while (retryCount < MAX_RETRY_COUNT) {
                try {
                    publishProgress("start Live view")
                    val camera = HttpConnector(CompanionClass.BaseUrl)
                    val `is` = camera.livePreview
                    mjis = MjpegInputStream(`is`)
                    retryCount = MAX_RETRY_COUNT
                } catch (e: IOException) {
                    try {
                        Thread.sleep(500)
                    } catch (e1: InterruptedException) {
                        e1.printStackTrace()
                    }

                } catch (e: JSONException) {
                    try {
                        Thread.sleep(500)
                    } catch (e1: InterruptedException) {
                        e1.printStackTrace()
                    }

                }
                Log.d("BaseAndroid", retryCount.toString() + "==")
                retryCount++
            }

            return mjis
        }

        override fun onProgressUpdate(vararg values: String) {
            for (log in values) {
                Log.d("BaseAndroid", log)
            }
        }

        override fun onPostExecute(mJpegInputStream: MjpegInputStream?) {
            if (mJpegInputStream != null) {
                Log.d("BaseAndroid", "成功了")
                initMjpegView(mJpegInputStream)
            } else {
                Log.d("BaseAndroid", "failed to start live view")
            }
        }
    }


    private inner class ShootTask : AsyncTask<Void, Void, HttpConnector.ShootResult>() {

        override fun onPreExecute() {
            LogUtils.d("takePicture")
        }

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg params: Void): HttpConnector.ShootResult {
            liveView.setSource(null)
            val postviewListener = CaptureListener()
            val camera = HttpConnector(CompanionClass.BaseUrl)

            return camera.takePicture(postviewListener)
        }

        override fun onPostExecute(result: HttpConnector.ShootResult) {
            if (result == HttpConnector.ShootResult.FAIL_CAMERA_DISCONNECTED) {
                LogUtils.d("takePicture:FAIL_CAMERA_DISCONNECTED")
            } else if (result == HttpConnector.ShootResult.FAIL_STORE_FULL) {
                LogUtils.d("takePicture:FAIL_STORE_FULL")
            } else if (result == HttpConnector.ShootResult.FAIL_DEVICE_BUSY) {
                LogUtils.d("takePicture:FAIL_DEVICE_BUSY")
            } else if (result == HttpConnector.ShootResult.SUCCESS) {
                LogUtils.d("takePicture:SUCCESS")

            }
        }

        private inner class CaptureListener : HttpEventListener {
            private var latestCapturedFileId: String? = null
            private var ImageAdd = false

            override fun onCheckStatus(newStatus: Boolean) {
                if (newStatus) {
                    LogUtils.d("takePicture:FINISHED")
                } else {
                    LogUtils.d("takePicture:IN PROGRESS")
                }
            }

            override fun onObjectChanged(latestCapturedFileId: String) {
                this.ImageAdd = true
                this.latestCapturedFileId = latestCapturedFileId
                LogUtils.d("ImageAdd:FileId " + this.latestCapturedFileId!!)
            }

            override fun onCompleted() {
                LogUtils.d("CaptureComplete")
                if (ImageAdd) {
                    runOnUiThread {
                        buttonTakePic.setEnabled(true)
//                        textCameraStatus.setText(R.string.text_camera_standby)
                        GetThumbnailTask(latestCapturedFileId).execute()
                    }
                }
            }

            override fun onError(errorMessage: String) {
                LogUtils.d("CaptureError $errorMessage")
                runOnUiThread {
                    buttonTakePic.setEnabled(true)
                }
            }
        }

    }

    private inner class GetThumbnailTask(private val fileId: String?) : AsyncTask<Void, String, Void>() {

        override fun doInBackground(vararg params: Void): Void? {
            val camera = HttpConnector(CompanionClass.BaseUrl)
            val thumbnail = camera.getThumb(fileId)
            if (thumbnail != null) {
                val baos = ByteArrayOutputStream()
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val thumbnailImage = baos.toByteArray()

                GLPhotoActivity.startActivityForResult(
                    this@MainActivity,
                    CompanionClass.BaseUrl,
                    fileId,
                    thumbnailImage,
                    true
                )
            } else {
                publishProgress("failed to get file data.")
            }
            return null
        }

        override fun onProgressUpdate(vararg values: String) {
            for (log in values) {
                LogUtils.d(log)
            }
        }
    }
}
