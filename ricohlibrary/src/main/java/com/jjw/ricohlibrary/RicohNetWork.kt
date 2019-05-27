package com.jjw.ricohlibrary

import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * Created by Caojing on 2019/5/11.
 *  你不是一个人在战斗
 */
class RicohNetWork {

    companion object {
        fun isWifiState(): Boolean {

            val isWifi = NetworkUtils.isWifiConnected()
            return if (!isWifi) {
                ToastUtils.showShort("请连接设备wifi")
                false
            } else {
                true
            }
        }
    }


}