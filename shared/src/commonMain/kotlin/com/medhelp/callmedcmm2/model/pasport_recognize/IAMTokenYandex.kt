package com.medhelp.callmedcmm2.model.pasport_recognize

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class IAMTokenYandex {
    @SerialName("iamToken")
    var iamToken: String? = null
    @SerialName("expiresAt")
    var expiresAt: String? = null
}