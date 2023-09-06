package com.medhelp.callmedcmm2.model.chat

open class DataClassForElectronicRecy /*: Comparable<DataClassForElectronicRecy>*/ {
    // сортировка перенесена с AnaliseResponse а должна работать без проблем в анализах
    var isHideDownload = true
    var pathToFile = ""
    var isShowTooltip = false
    var isShowHideBox = false
    val isDownloadIn: Boolean
        get() = pathToFile != ""

    val datePer: String?
        get() = if (this is AnaliseResponse) (this as AnaliseResponse).getDateForZakl() else  (this as ResultZakl2Item).dataPriema

    val textTitle : String?
        get()= if (this is AnaliseResponse) (this as AnaliseResponse).textTitle() else  (this as ResultZakl2Item).textTitle()

    // дата + тайтле
    val title: String?
        get() = if (this is AnaliseResponse) (this as AnaliseResponse).getTitleAnalise() else  (this as ResultZakl2Item).getTitleRes()

}