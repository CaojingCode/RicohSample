package com.jjw.ricohlibrary

import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.jjw.ricohlibrary.callback.UpdateViewCallBack
import com.jjw.ricohlibrary.entiy.RrequestBean
import com.jjw.ricohlibrary.entiy.StartSessionBean
import com.jjw.ricohlibrary.entiy.StateBean
import com.jjw.ricohlibrary.view.MjpegInputStream
import com.zhouyou.http.EasyHttp
import com.zhouyou.http.callback.DownloadProgressCallBack
import com.zhouyou.http.callback.SimpleCallBack
import com.zhouyou.http.exception.ApiException
import io.reactivex.disposables.Disposable
import java.sql.Time
import java.util.*
import com.zhouyou.http.utils.HttpLog




/**
 * Created by Caojing on 2019/5/11.
 *  你不是一个人在战斗
 */
class AppRepository {

    var updateViewCallBack: UpdateViewCallBack? = null

    var sessionId: String = ""
    var fingerprint: String = ""

    companion object {
        private var INSTANCE: AppRepository? = null

        fun getInstance(): AppRepository {
            if (INSTANCE == null) {
                INSTANCE = AppRepository()
            }
            return INSTANCE as AppRepository
        }

    }

    /**
     * 2.0版本开始要先建立会话。
     */
    fun requestStartSession() {
        var requestBean = RrequestBean()
        requestBean.name = CompanionClass.startSession
        EasyHttp.post(CompanionClass.CameraMarkName)
            .upJson(GsonUtils.toJson(requestBean))
            .execute(object : SimpleCallBack<String>() {
                override fun onSuccess(t: String?) = try {
                    var startSessionBean = GsonUtils.fromJson<StartSessionBean>(t, StartSessionBean::class.java)
                    ToastUtils.showShort(startSessionBean.results.sessionId)
                    updateViewCallBack?.updateText("链接成功" + startSessionBean.results.sessionId)
                    requestSetOptions(startSessionBean.results.sessionId)
                    sessionId = startSessionBean.results.sessionId
                } catch (e: Exception) {

                }

                override fun onError(e: ApiException?) {
                    ToastUtils.showShort(e!!.displayMessage)
                }

            })
    }

    /**
     * 关闭会话
     */
    fun closeSession() {
        var requestBean = RrequestBean()
        requestBean.parameters.sessionId = sessionId
        EasyHttp.post(CompanionClass.closeSession)
            .upJson(GsonUtils.toJson(requestBean))
            .execute(object : SimpleCallBack<String>() {
                override fun onSuccess(t: String?) {
                }

                override fun onError(e: ApiException?) {
                }

            })
    }


    /**
     * 配置相机相关属性
     */
    fun requestSetOptions(sessionId: String) {
        var requestBean = RrequestBean()
        requestBean.name = CompanionClass.setOptions
        requestBean.parameters.sessionId = sessionId
        requestBean.parameters.options.fileFormat.type = "jpeg"
        requestBean.parameters.options.fileFormat.width = 2048
        requestBean.parameters.options.fileFormat.height = 1024
        EasyHttp.post(CompanionClass.CameraMarkName)
            .upJson(GsonUtils.toJson(requestBean))
            .execute(object : SimpleCallBack<String>() {
                override fun onSuccess(t: String?) {
                    var startSessionBean = GsonUtils.fromJson<StartSessionBean>(t, StartSessionBean::class.java)
                    if (startSessionBean.state == "done") {
                        updateViewCallBack?.updateText("设置参数成功")
//                        getLivePreview(sessionId)
                    }
                }

                override fun onError(e: ApiException?) {
                }

            })
    }

    /**
     * 拍照前，获取相机状态
     */
    fun getState() {
        EasyHttp.post(CompanionClass.CameraStateName).execute(object : SimpleCallBack<StateBean>() {
            override fun onSuccess(t: StateBean?) {
                ToastUtils.showShort(t.toString())
                fingerprint = t?.fingerprint!!
            }

            override fun onError(e: ApiException?) {
            }

        })
    }

    /**
     * 开始拍照
     */
    fun takePicture() {
        var requestBean = RrequestBean()
        requestBean.name = CompanionClass.takePicture
        requestBean.parameters.sessionId = sessionId
        EasyHttp.post(CompanionClass.CameraMarkName).upJson(GsonUtils.toJson(requestBean))
            .execute(object : SimpleCallBack<String>() {
                override fun onSuccess(t: String?) {
                    var bean = GsonUtils.fromJson(t, StartSessionBean::class.java)
                    if (bean.state == "done") {
                        var fileUri = bean.results.fileUri
                    } else if (bean.state == "inProgress") {
                        updateViewCallBack?.updateText("图片合成中...")
                        //拍摄中加载进度，开始计时
                        var mCheckStatusTimer = Timer()
                        mCheckStatusTimer.scheduleAtFixedRate(object : TimerTask() {
                            override fun run() {
                                checkCaptureStatus(bean.id, mCheckStatusTimer)
                            }
                        }, 300, 300)
                    }
                }

                override fun onError(e: ApiException?) {

                }

            })

    }

    /**
     * 预览数据请求
     */
    fun getLivePreview(sessionId: String) {
        var requestBean = RrequestBean()
        requestBean.name = CompanionClass.getLivePreview
        requestBean.parameters.sessionId = sessionId
        requestBean.parameters.options.fileFormat.type = "jpeg"
        requestBean.parameters.options.fileFormat.width = 2048
        requestBean.parameters.options.fileFormat.height = 1024
        EasyHttp.post(CompanionClass.CameraMarkName)
            .addInterceptor { chain ->
                var response = chain.proceed(chain.request())
                var inputStream = response.body()!!.byteStream()
                updateViewCallBack?.livePreviewInputStream(MjpegInputStream(inputStream))
                return@addInterceptor response
            }
            .upJson(GsonUtils.toJson(requestBean))
            .execute(object : SimpleCallBack<String>() {
                override fun onSuccess(t: String?) {

//                    updateViewCallBack?.livePreviewInputStream(t!!.byteInputStream(Charsets.UTF_8))
                }

                override fun onError(e: ApiException?) {
                }

            })

    }


    /**
     * 开始拍照后，请求该接口检查拍照完成后返回url
     */
    fun checkCaptureStatus(id: String, mCheckStatusTimer: Timer): Disposable? {
        var requestBean = RrequestBean()
        requestBean.id = id
        return EasyHttp.post(CompanionClass.CommandsStatus)
            .upJson(GsonUtils.toJson(requestBean))
            .execute(object : SimpleCallBack<String>() {
                override fun onSuccess(t: String?) {
                    var bean = GsonUtils.fromJson(t, StartSessionBean::class.java)
                    if (bean.state == "done") {
                        var fileUri = bean.results.fileUri
                        mCheckStatusTimer.cancel()
                        LogUtils.d(fileUri)
                        updateViewCallBack?.updateText("图片合成完毕")
                        dowloadPic(EasyHttp.getBaseUrl()+"/"+fileUri)
                    }
                }

                override fun onError(e: ApiException?) {

                }

            })

    }

    fun dowloadPic(imgUrl: String) {
        EasyHttp.downLoad(imgUrl)
            .execute(object :DownloadProgressCallBack<String>(){
                override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                    val progress = (bytesRead * 100 / contentLength) as Int
                    HttpLog.e("$progress% ")
                    updateViewCallBack?.updateText("图片接收中$progress%")
                    if (done) {//下载完成
                    }
                }

                override fun onComplete(path: String?) {
                    updateViewCallBack?.updateText("图片接收完成")
                    updateViewCallBack!!.ImageFile(path!!)
                }

                override fun onError(e: ApiException?) {
                    updateViewCallBack?.updateText("图片接收失败")
                }

                override fun onStart() {
                    updateViewCallBack?.updateText("图片开始接收")
                }

            })
    }

    fun info() {
        EasyHttp.get(CompanionClass.info)
            .execute(object : SimpleCallBack<String>() {
                override fun onSuccess(t: String?) {

                }

                override fun onError(e: ApiException?) {

                }

            })

    }

}