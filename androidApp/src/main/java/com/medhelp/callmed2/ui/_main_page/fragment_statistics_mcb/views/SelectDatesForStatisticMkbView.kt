package com.medhelp.callmed2.ui._main_page.fragment_statistics_mcb.views

import android.app.DatePickerDialog
import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.utils.main.MDate
import java.util.*

class SelectDatesForStatisticMkbView: LinearLayout {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    var listener: SelectDatesForStatisticMkbListener? = null

    lateinit var dateFrom: TextView
    lateinit var dateTo: TextView
    lateinit var btnRequest: Button

    private fun init(context: Context) {
        val view = inflate(context, R.layout.view_select_dates_for_statistics, this)

        dateFrom=view.findViewById(R.id.dateFrom)
        dateTo=view.findViewById(R.id.dateTo)
        btnRequest=view.findViewById(R.id.btnRequest)

        setStartDates()

        dateFrom.setOnClickListener{
            clickDateForTuning(1)
        }
        dateTo.setOnClickListener{
            clickDateForTuning(2)
        }
        btnRequest.setOnClickListener{
            listener?.requestMcbList(dateFrom.text.toString(), dateTo.text.toString())
        }
    }

    private fun setStartDates(){
        val currentD = MDate.curentDate

        dateFrom.text = Html.fromHtml("<u>"+currentD+"</u>")
        dateTo.text = Html.fromHtml("<u>"+currentD+"</u>")
    }

    private fun clickDateForTuning(type: Int){
        val tmp: DatePickerDialog.OnDateSetListener
        val dateAndTime = Calendar.getInstance()
        if (type == 1) {
            tmp = r2d1

            dateAndTime.clear()
            dateAndTime.timeInMillis = MDate.stringToLong(dateFrom.getText().toString(), MDate.DATE_FORMAT_ddMMyyyy) ?: 0
            object : DatePickerDialog(context, tmp, dateAndTime[Calendar.YEAR], dateAndTime[Calendar.MONTH], dateAndTime[Calendar.DAY_OF_MONTH]) {
                override fun onDateChanged(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
                    var month = month
                    month++
                    var str = if (dayOfMonth > 9) "$dayOfMonth." else "0$dayOfMonth."
                    str += if (month > 9) "$month." else "0$month."
                    str += year.toString()

                    dateFrom.text = Html.fromHtml( "<u>"+str +"</u>")
                    dismiss()
                }
            }.show()
        } else if(type == 2) {
            tmp = r2d2

            dateAndTime.clear()
            dateAndTime.timeInMillis = MDate.stringToLong(dateTo.getText().toString(), MDate.DATE_FORMAT_ddMMyyyy) ?: 0
            object : DatePickerDialog(context, tmp, dateAndTime[Calendar.YEAR], dateAndTime[Calendar.MONTH], dateAndTime[Calendar.DAY_OF_MONTH]) {
                override fun onDateChanged(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
                    var month = month
                    month++
                    var str = if (dayOfMonth > 9) "$dayOfMonth." else "0$dayOfMonth."
                    str += if (month > 9) "$month." else "0$month."
                    str += year.toString()

                    dateTo.text = Html.fromHtml( "<u>"+str +"</u>")
                    dismiss()
                }
            }.show()
        }

    }

    // установка обработчика для выпадающего списка "конкретная дата"
    val r2d1 = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        var monthOfYear = monthOfYear
        monthOfYear++
        var str = if (dayOfMonth > 9) "$dayOfMonth." else "0$dayOfMonth."
        str += if (monthOfYear > 9) "$monthOfYear." else "0$monthOfYear."
        str += year.toString() + ""
        dateFrom.text = Html.fromHtml( "<u>"+str +"</u>")
    }
    val r2d2 = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        var monthOfYear = monthOfYear
        monthOfYear++
        var str = if (dayOfMonth > 9) "$dayOfMonth." else "0$dayOfMonth."
        str += if (monthOfYear > 9) "$monthOfYear." else "0$monthOfYear."
        str += year.toString() + ""
        dateTo.text = Html.fromHtml( "<u>"+str +"</u>")
    }

    interface SelectDatesForStatisticMkbListener{
        fun requestMcbList(dFrom: String, dTo: String)
    }
}