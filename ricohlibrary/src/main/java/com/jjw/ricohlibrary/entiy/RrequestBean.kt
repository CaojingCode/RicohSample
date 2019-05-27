package com.jjw.ricohlibrary.entiy

data class RrequestBean(
    var id:String="",
    var name: String = "", // camera.setOptions
    var parameters: Parameters = Parameters(),
    var stateFingerprint: String=""
) {
    data class Parameters(
        var options: Options = Options(),
        var sessionId: String = ""
    ) {
        data class Options(
            var fileFormat: FileFormat = FileFormat()
        ) {
            data class FileFormat(
                var height: Int = 0, // 1024
                var type: String = "", // jpeg
                var width: Int = 0 // 2048
            )
        }
    }
}