package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_month_fragment

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.AllRaspSotrItem
import com.medhelp.callmed2.databinding.FragmentTimetableOnMonthBinding
import com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_month_fragment.recy.TimetableOnMonthAdapter
import com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_month_fragment.view.ThreeMonthPickerDialog
import com.medhelp.callmed2.ui.base.BaseFragment
import com.medhelp.callmed2.utils.main.MDate
import timber.log.Timber

class TimetableOnMonthFragment: BaseFragment() {

    lateinit var binding : FragmentTimetableOnMonthBinding

    lateinit var presenter: TimetableOnMonthPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Timber.i("Расписание доктора на месяц")
        val rootView: View = inflater.inflate(R.layout.fragment_timetable_on_month, container, false)
        binding = FragmentTimetableOnMonthBinding.bind(rootView)

        return binding.root
    }



    override fun setUp(view: View) {
        presenter = TimetableOnMonthPresenter(this)

        setCurrentDate(MDate.currenDateLong)
        binding.selectDateBox.setOnClickListener{
            val dialog = ThreeMonthPickerDialog()
            dialog.setListener(object : ThreeMonthPickerDialog.ThreeMonthPickerDialogListener {
                override fun selectMonth(date: Long) {
                    binding.selectDateText.text = Html.fromHtml("<u>"+MDate.longToString(date, MDate.DATE_FORMAT_LLLL_yyyyy).uppercase()+"</u>")
                    presenter.findAllRaspSotr(MDate.longToString(date, MDate.DATE_FORMAT_MMyyyy))
                }
            })
            dialog.show(childFragmentManager, ThreeMonthPickerDialog:: class.java.canonicalName)
        }
    }
    override fun destroyFragment() {}
    override fun onStartSetStatusFragment(status: Int) {}

    private fun setCurrentDate(date: Long){
        binding.selectDateText.text = Html.fromHtml("<u>"+MDate.longToString(date, MDate.DATE_FORMAT_LLLL_yyyyy).uppercase()+"</u>")
        presenter.findAllRaspSotr(MDate.longToString(date, MDate.DATE_FORMAT_MMyyyy))
    }

    fun initRecy(list : List<AllRaspSotrItem>){
        if(list.size==1 && list[0].data == null){
            binding.recy.adapter = TimetableOnMonthAdapter(requireContext(), listOf())
            return
        }

        val llm = LinearLayoutManager(requireContext())
        val adapter = TimetableOnMonthAdapter(requireContext(), list)

        binding.recy.layoutManager = llm
        binding.recy.adapter = adapter

        sumWorkHour(list)
    }

    fun sumWorkHour(list : List<AllRaspSotrItem>){
        var sum = 0L

        for(i in list){
            if(i.start == i.end)
                continue

            val tStart = MDate.stringToLong(i.start, MDate.DATE_FORMAT_HHmm)
            val tStop = MDate.stringToLong(i.end, MDate.DATE_FORMAT_HHmm)
            var rez = tStop-tStart

            if(i.obed_start.length == 5 && i.obed_end.length == 5){
                val tStartOb = MDate.stringToLong(i.obed_start, MDate.DATE_FORMAT_HHmm)
                val tStopOb = MDate.stringToLong(i.obed_end, MDate.DATE_FORMAT_HHmm)
                rez -= tStopOb-tStartOb
            }

            sum += rez
        }

        val tt3 = sum.toFloat()/(1000*60*60).toFloat()
        binding.totalTimeV.text = String.format("%.1f", tt3)
    }

    override fun onDestroy() {
        presenter.processingDestroy()
        super.onDestroy()
    }
}