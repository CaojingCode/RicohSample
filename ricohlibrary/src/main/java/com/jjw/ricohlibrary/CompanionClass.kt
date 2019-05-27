package com.jjw.ricohlibrary

/**
 * Created by Caojing on 2019/5/11.
 *  你不是一个人在战斗
 */
class CompanionClass {

    companion object {
        val BaseUrl = "http://192.168.1.1"

        //对相机进行操作的方法
        val CameraMarkName = "/osc/commands/execute"

        //拍摄前检查状态，以便确定文件是否已保存
        val CameraStateName="/osc/state"

        //检查文件是否保存完成。
        val checkForUpdates="/osc/checkForUpdates"
        //
        val info="/osc/info"

        //建立链接，API v2.0要求在运行命令之前启动一个会话。使用相机。启动会话和摄像机。设置相机和拍摄属性的设置。
        val startSession="camera.startSession"

        val closeSession="camera.closeSession"

        //设置api版本
        val setOptions="camera.setOptions"

        //拍照
        val takePicture="camera.takePicture"

        //获取预览数据
        val getLivePreview="camera._getLivePreview"

        val startCapture="camera._startCapture"

        //检查拍照状态
        val CommandsStatus="/osc/commands/status"
    }

}