package com.jjw.ricohlibrary.entiy

data class StateBean(
    var fingerprint: String = "", // FIG_0003
    var state: State = State()
) {
    data class State(
        var apiVersion: Int = 0, // 2
        var batteryState: String = "", // disconnect
        var captureStatus: String = "", // idle
        var latestFileUrl: String = "", // http://192.168.1.1/files/abcde/100RICOH/R0010004.MP4
        var recordableTime: Int = 0, // 0
        var recordedTime: Int = 0, // 0
        var batteryLevel: Double = 0.0, // 1.0
        var storageUri: String = "" // http://192.168.1.1/files/abcde/
    )
}