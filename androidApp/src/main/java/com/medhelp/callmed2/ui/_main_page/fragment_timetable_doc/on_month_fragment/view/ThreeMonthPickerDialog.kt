package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_month_fragment.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.DialogMonthPickerBinding
import com.medhelp.callmed2.utils.main.MDate
import java.util.*

class ThreeMonthPickerDialog: DialogFragment() {
    private var listener: ThreeMonthPickerDialogListener? = null
    fun setListener(listener: ThreeMonthPickerDialogListener?) {
        this.listener = listener
    }

    lateinit var binder: DialogMonthPickerBinding

    var arr = arrayOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.dialog_month_picker, null)
        binder = DialogMonthPickerBinding.bind(view)

        binder.btnClose.setOnClickListener{
            dialog?.dismiss()
        }

        binder.btnSelect.setOnClickListener{
            val tmp = binder.pickerMonth.value
            val tmp2 = arr[tmp]
            listener?.selectMonth(MDate.stringToLong(tmp2, MDate.DATE_FORMAT_LLLL_yyyyy))
            dialog?.dismiss()
        }

        return view
        //binding.pickerMonth.value
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cal: Calendar = Calendar.getInstance()

        val currDayL = cal.timeInMillis
        cal.add(Calendar.MONTH, -1)
        val backDayL = cal.timeInMillis
        cal.add(Calendar.MONTH, 2)
        val nextDayL = cal.timeInMillis

        arr = arrayOf(MDate.longToString(backDayL, MDate.DATE_FORMAT_LLLL_yyyyy), MDate.longToString(currDayL, MDate.DATE_FORMAT_LLLL_yyyyy), MDate.longToString(nextDayL, MDate.DATE_FORMAT_LLLL_yyyyy))


        binder.pickerMonth.run {
            minValue = 0
            maxValue = 2
            value = cal.get(Calendar.MONTH)
            displayedValues = arr
        }
    }

    override fun onStart() {
        super.onStart()

        //костыль, по умолчанию окно показывается не во весь размер
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            Objects.requireNonNull(dialog.window)?.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


    interface ThreeMonthPickerDialogListener{
        fun selectMonth(date : Long)
    }

}