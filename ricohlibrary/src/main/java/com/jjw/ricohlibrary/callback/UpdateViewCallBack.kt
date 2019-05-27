package com.jjw.ricohlibrary.callback

import com.jjw.ricohlibrary.view.MjpegInputStream

/**
 * Created by Caojing on 2019/5/13.
 *  你不是一个人在战斗
 */
interface UpdateViewCallBack {

    fun updateText(text:String)

    fun livePreviewInputStream(inputStream: MjpegInputStream)

    fun ImageFile(imageFilePath:String)
}