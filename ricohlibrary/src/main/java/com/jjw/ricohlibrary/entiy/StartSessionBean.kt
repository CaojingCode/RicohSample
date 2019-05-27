package com.jjw.ricohlibrary.entiy

data class StartSessionBean(
    var name: String = "", // camera.startSession
    var results: Results = Results(),
    var state: String = "",// done
    var id: String = "",
    var error: Errors = Errors()
) {
    data class Results(
        var sessionId: String = "", // SID_0001
        var timeout: Int = 0, // 180,
        var fileUri: String = ""
    )

    data class Errors(
        var message: String = ""//错误信息
    )
}

