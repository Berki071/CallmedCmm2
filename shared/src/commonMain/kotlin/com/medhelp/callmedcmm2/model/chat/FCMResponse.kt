package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FCMResponse {
    @SerialName("multicast_id")
    var multicastId: Long? = null
    @SerialName("success")
    var success: Int? = null
    @SerialName("failure")
    var failure: Int? = null
    @SerialName("canonical_ids")
    var canonicalIds: Int? = null
    @SerialName("results")
    var results: List<Result> = ArrayList<Result>()


    @Serializable
    class Result{
        @SerialName("message_id")
        var messageId: String? = null
    }
}
