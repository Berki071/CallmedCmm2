package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LoadDataZaklAmbItem {
    @SerialName("OOO")
    var ooo: String? = null

    @SerialName("data_priem")
    var dataPriem: String? = null

    @SerialName("diagnoz")
    var diagnoz: String? = null

    @SerialName("rekomend")
    var rekomend: String? = null

    @SerialName("sotr")
    var sotr: String? = null

    @SerialName("sotr_spec")
    var sotrSpec: String? = null

    @SerialName("cons")
    var cons: String? = null

    @SerialName("shapka")
    var shapka: String? = null

    @SerialName("nom_amb")
    var nom_amb: Int? = null

    @SerialName("text_html")
    var textHtml: String? = null
}