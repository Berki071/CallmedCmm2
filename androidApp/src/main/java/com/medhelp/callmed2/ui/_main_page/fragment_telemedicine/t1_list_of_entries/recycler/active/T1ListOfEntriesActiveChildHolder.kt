package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.recycler.active

import android.os.Handler
import android.text.Html
import android.view.View
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.data.model.AllRecordsTelemedicineItemAndroid
import com.medhelp.callmed2.databinding.ItemChatWithDoctorBinding
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.main.MainUtils
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder

class T1ListOfEntriesActiveChildHolder(val bindingItem: ItemChatWithDoctorBinding, val listener : ChatWithDoctorActiveChildHolderListener): ChildViewHolder(bindingItem.root) {
    private var item: AllRecordsTelemedicineItemAndroid? = null

    val timeForNoty24Hour: Int = 1000*60*60*24
    val timeForNoty12Hour: Int = 1000*60*60*12
    val timeForNoty4Hour: Int = 1000*60*60*4
    val timeForNoty1Hour: Int = 1000*60*60*1
    val timeForNoty5Min: Int = 1000*60*5

    init {
        itemView.setOnClickListener { click: View? ->
            item?.let{
                listener.enterTheRoom(it)
            }
        }
    }

    fun onBindView(item: AllRecordsTelemedicineItemAndroid) {
        this.item = item

        bindingItem.title.text = item.fullNameKl
        bindingItem.tariffName.text =Html.fromHtml("<b>Тариф: </b>" + item.tmName)
        bindingItem.tariffSmallInfo.text = Html.fromHtml("<b>Специальность: </b>" + item.specialty)  //??

        if(item.statusPay == "true"){
            bindingItem.statusPaid.setImageResource(R.drawable.rubl)
        }else{
            bindingItem.statusPaid.setImageResource(R.drawable.rubl_red)
        }

        if(item.isShowNewMsgIco){
            bindingItem.statusPaid.visibility = View.GONE
            bindingItem.newIcoBox.visibility = View.VISIBLE
        }else{
            bindingItem.statusPaid.visibility = View.VISIBLE
            bindingItem.newIcoBox.visibility = View.GONE
        }

        if(item.status == Constants.TelemedicineStatusRecord.active.toString()){
            checkTimerActive()
        }else{
            checkTimerWait()
        }

        var idSpace = item.fullNameKl!!.indexOf(" ")
        idSpace = if(idSpace>0) idSpace else 1
        val tmpStr = item.fullNameKl!!.substring(0,1)+item.fullNameKl!!.substring(idSpace+1,idSpace+2)
        bindingItem.ico.setImageBitmap(MainUtils.creteImageIco(bindingItem.root.context, tmpStr,item!!.status!!))

    }

    var timerTimeStop = 0L
    private fun checkTimerActive(){
        if(item!=null) {
            bindingItem.timeLeft.text = "Время до завершения:"

            val currentTimePhone = MDate.getCurrentDate()
            val currentTimeServerLong: Long = MDate.stringToLong(item!!.dataServer, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
            var differenceCurrentTime = currentTimeServerLong - currentTimePhone

            val dateStartLong: Long = MDate.stringToLong(item!!.dataStart!!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss) ?: 0L
            val dateEndLong: Long = dateStartLong + (item!!.tmTimeForTm!!.toLong()*60*1000)

            if(dateEndLong<=currentTimeServerLong) {
                bindingItem.timeLeft.visibility = View.GONE
                bindingItem.redDot.visibility = View.GONE
            }else{
                bindingItem.timeLeft.visibility = View.VISIBLE
                bindingItem.redDot.visibility = View.VISIBLE
                if(dateEndLong!=null && currentTimeServerLong!=null)
                    timerTimeStop = dateEndLong-differenceCurrentTime
                startTimerActive()
            }
        }else{
            bindingItem.timeLeft.visibility = View.GONE
            bindingItem.redDot.visibility = View.GONE
        }
    }

    fun startTimerActive(){
        val currentTimePhone = MDate.getCurrentDate()
        if(currentTimePhone >= timerTimeStop){
            bindingItem.timeLeft.visibility = View.GONE
            bindingItem.redDot.visibility = View.GONE

            item?.let{
                listener.closeTm(it)
            }

            return
        }

        val timeLeft = timerTimeStop-currentTimePhone

        val timeLeftSec = timeLeft/1000
        val sec = timeLeftSec % 60
        val minAll = timeLeftSec / 60
        val min = minAll % 60
        val hour = minAll / 60

        var str = if (hour==0L) "" else hour.toString()+":"
        var minStr = min.toString()
        if(minStr.length==1)
            minStr = "0"+minStr
        str += if(minStr.isEmpty()) minStr else minStr+":"

        str += if(sec<10 ) "0"+sec.toString() else sec.toString()
        bindingItem.timeLeft.text =  Html.fromHtml("<b>Время до завершения: </b>"+str)

        val handler = Handler()
        val delay = 1000 //milliseconds
        handler.postDelayed({
            startTimerActive()
        }, delay.toLong())
    }

    private fun checkTimerWait(){
        if(item!=null && item!!.statusPay == "true") {
            bindingItem.timeLeft.text = Html.fromHtml("<b>Приступить в течение: </b>")
            bindingItem.timeLeft.visibility = View.VISIBLE
            bindingItem.redDot.visibility = View.VISIBLE

            val currentTimePhone = MDate.getCurrentDate()
            val currentTimeServerLong: Long = MDate.stringToLong(item!!.dataServer, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
            var differenceCurrentTime = currentTimeServerLong - currentTimePhone

            val dateStartPLong: Long = MDate.stringToLong(item!!.dataPay!!, MDate.DATE_FORMAT_ddMMyyyy_HHmm) ?: 0L
            val dateEndLong: Long = dateStartPLong + (item!!.timeStartAfterPay!!.toLong()*60*1000)

//            val tmp1 = MDate.longToString(currentTimeLong,MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
//            val tmp2 = MDate.longToString(dateStartPLong,MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
//            val tmp3 = MDate.longToString(dateEndLong,MDate.DATE_FORMAT_ddMMyyyy_HHmmss)


            if(dateEndLong<=currentTimeServerLong) {
                bindingItem.timeLeft.text =  Html.fromHtml("<b>Приступить в течение: </b>0!!")
            }else{
                if(dateEndLong!=null && currentTimeServerLong!=null) {
                    //время остановиться в телефонном времени
                    timerTimeStop = dateEndLong-differenceCurrentTime  //differenceCurrentTime для приведения к времени устройства
                }
                startTimerWait()
            }
        }else{
            bindingItem.timeLeft.visibility = View.GONE
            bindingItem.redDot.visibility = View.GONE
        }
    }

    fun startTimerWait(){
        val currentTimePhone = MDate.getCurrentDate()
        if(currentTimePhone >= timerTimeStop){
            bindingItem.timeLeft.text =  Html.fromHtml("<b>Приступить в течение: </b>0!!")
            return
        }

        val timeLeft = timerTimeStop-currentTimePhone

        val timeLeftSec = timeLeft/1000
        val sec = timeLeftSec % 60
        val minAll = timeLeftSec / 60
        val min = minAll % 60
        val hour = minAll / 60

        var str = if (hour==0L) "" else hour.toString()+":"
        var minStr = min.toString()
        if(minStr.length==1)
            minStr = "0"+minStr
        str += if(minStr.isEmpty()) minStr else minStr+":"

        str += if(sec<10 ) "0"+sec.toString() else sec.toString()
        bindingItem.timeLeft.text =  Html.fromHtml("<b>Приступить в течение: </b>"+str)

//        val tt = MDate.longToString(timerTimeLeft, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
//        val tt1 = MDate.longToString((timeForNoty24Hour - timeForNoty5Min).toLong(), MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
//        val tt2 = MDate.longToString(timeForNoty24Hour.toLong(), MDate.DATE_FORMAT_ddMMyyyy_HHmmss)

        if(timeLeft > (timeForNoty24Hour - timeForNoty5Min) && timeLeft < (timeForNoty24Hour) &&  item!!.notif_24 == "false"){
            item?.let{
                it.notif_24 = "true"
                listener?.sendNotyReminder(it, "До начала телемедицины осталось меньше 24 часов","notif_24")
            }
        }
        if(timeLeft > (timeForNoty12Hour - timeForNoty5Min) && timeLeft < (timeForNoty12Hour) &&  item!!.notif_12 == "false"){
            item?.let{
                it.notif_12 = "true"
                listener?.sendNotyReminder(it, "До начала телемедицины осталось меньше 12 часов", "notif_12")
            }
        }
        if(timeLeft > (timeForNoty4Hour - timeForNoty5Min) && timeLeft < (timeForNoty4Hour)  &&  item!!.notif_4 == "false"){
            item?.let{
                it.notif_4 = "true"
                listener?.sendNotyReminder(it, "До начала телемедицины осталось меньше 4 часов", "notif_4")
            }
        }
        if(timeLeft > (timeForNoty1Hour - timeForNoty5Min) && timeLeft < (timeForNoty1Hour)  &&  item!!.notif_1 == "false"){
            item?.let{
                it.notif_1 = "true"
                listener?.sendNotyReminder(it, "До начала телемедицины осталось меньше 1 часа", "notif_1")
            }
        }


        val handler = Handler()
        val delay = 1000 //milliseconds
        handler.postDelayed({
            startTimerWait()
        }, delay.toLong())
    }

    interface ChatWithDoctorActiveChildHolderListener{
        fun enterTheRoom(item: AllRecordsTelemedicineItemAndroid)
        fun closeTm(item: AllRecordsTelemedicineItemAndroid)
        fun sendNotyReminder(item: AllRecordsTelemedicineItemAndroid, msg: String, type: String)
    }
}