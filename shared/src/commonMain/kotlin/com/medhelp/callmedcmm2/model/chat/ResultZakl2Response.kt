package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ResultZakl2Response {
    @SerialName("error")
    var error = false

    @SerialName("message")
    var message: String? = null

    @SerialName("response")
    var response: List<ResultZakl2Item> = ArrayList<ResultZakl2Item>()

    @Serializable
    class ResultZakl2Item: DataClassForElectronicRecy {
        @SerialName("data_priem")
        var dataPriema: String? = null

        @SerialName("name_spec")
        var nameSpec: String? = null

        @SerialName("id_kl")
        var idKl: String? = null

        @SerialName("id_filial")
        var idFilial: Int? = null

        constructor()
        constructor(item: ResultZakl2Item) {                     //for ios
            this.pathToFile = item.pathToFile
            this.dataPriema = item.dataPriema
            this.nameSpec = item.nameSpec
            this.idKl = item.idKl
            this.idFilial = item.idFilial
        }

        fun textTitle(): String? {
            return nameSpec
        }
        fun getTitleRes() : String
        {
            return dataPriema + " " + nameSpec;
        }

        fun getNameFileWithExtension(name: String, extension: String): String   //extention default ".pdf"
        {
            return name + "_" + dataPriema?.replace(".", "_") + "_" + nameSpec + extension;
        }

        fun getNameFileWithoutExtension(name: String): String{
            return name + "_" + dataPriema?.replace(".", "_") + "_" + nameSpec;
        }
    }
}