package com.jjw.ricohlibrary.entiy

data class SetOptionsRequestBean(
    var options: Options = Options(),
    var sessionId: String = "" // SID_0001
) {
    data class Options(
        var clientVersion: Int = 0,// api版本可不传
        var captureMode: String = ""//相机模式
    )
}