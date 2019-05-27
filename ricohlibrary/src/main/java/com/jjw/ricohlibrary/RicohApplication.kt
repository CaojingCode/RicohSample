package com.jjw.ricohlibrary

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.zhouyou.http.EasyHttp

/**
 * Created by Caojing on 2019/5/11.
 *  你不是一个人在战斗
 */
open class RicohApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        EasyHttp.init(this)//默认初始化
        EasyHttp.getInstance().setBaseUrl(CompanionClass.BaseUrl).debug("BaseAndroids")
        LogUtils.getConfig().setGlobalTag("BaseAndroids").setConsoleSwitch(true).setLogSwitch(true)
    }

}