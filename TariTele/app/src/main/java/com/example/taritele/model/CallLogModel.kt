package com.example.taritele.model

data class CallLogModel(
    var id: String = "",
    var phoneNumber: String? = null,
    var callType: String? = null,
    var callDate: Long = 0L,
    var callDuration: Long = 0L,
    var callCost: Double = 0.0
)