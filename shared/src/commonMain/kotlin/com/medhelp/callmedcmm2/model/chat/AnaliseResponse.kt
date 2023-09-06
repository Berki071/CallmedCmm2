package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class AnaliseResponse: DataClassForElectronicRecy {
    @SerialName("data")
    var date: String? = null

    @SerialName("file")
    var linkToPDF: String? = null


    constructor(item: AnaliseResponse) {                     //for ios
        this.date = item.date
        this.linkToPDF = item.linkToPDF
        this.pathToFile = item.pathToFile
    }

    constructor(date: String) {                     //for ios
        this.date = date
    }

    constructor()

    //для электронных заключений
    fun getDateForZakl(): String? {
        if(date == null || date!!.length < 10)
            return null

        try {
            return date?.substring(date!!.length - 10)
        }catch (e: Exception){}

        return null
    }

    fun textTitle(): String? {
        return date?.substring(0, date!!.length - 14)
    }

    fun getTitleAnalise(): String? {
        return date?.substring(date!!.length - 10) + " " + date!!.substring(0, date!!.length - 14)
    }
}