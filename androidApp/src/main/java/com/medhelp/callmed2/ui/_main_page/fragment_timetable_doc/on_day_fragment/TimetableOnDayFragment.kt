package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.SettingsAllBranchHospitalResponse
import com.medhelp.callmed2.databinding.FragmentTimetableOnDayBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.T1ListOfEntriesFragment
import com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.decorators.DayDecorator
import com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.decorators.SelectDecorator
import com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.recy_branch.BranchSpinnerAdapter
import com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.resyAppointment.AppointmentAdapter
import com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.resyAppointment.AppointmentHolder
import com.medhelp.callmed2.ui.base.BaseFragment
import com.medhelp.callmed2.utils.main.TimesUtils
import com.medhelp.callmedcmm2.model.VisitResponse.*
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import java.util.*

class TimetableOnDayFragment: BaseFragment(), OnDateSelectedListener, OnMonthChangedListener {
    lateinit var binding: FragmentTimetableOnDayBinding

    lateinit var presenter: TimetableOnDayPresenter

    private var selectedBranch = -1
    private val visitList: MutableList<VisitItem> = mutableListOf()
    var appointmentAdapter: AppointmentAdapter? = null

    var currentDateForRequest: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Timber.i("Расписание доктора yна день")
        val rootView: View = inflater.inflate(R.layout.fragment_timetable_on_day, container, false)
        binding = FragmentTimetableOnDayBinding.bind(rootView)
        presenter = TimetableOnDayPresenter()
        presenter.onAttachView(this)
        return binding.root
    }
    override fun onResume() {
        super.onResume()

        if (binding.spinnerBranch != null && binding.spinnerBranch!!.count > 0) {
            binding.cabinetName!!.visibility = View.GONE
            binding.rvSchedule!!.adapter = null
            val ss = binding.calendarSchedule!!.currentDate.date
            val dateMon = TimesUtils.dateToString(ss, TimesUtils.DATE_FORMAT_ddMMyyyy)
            currentDateForRequest = dateMon
            presenter.getAllReceptionApiCall(selectedBranch, currentDateForRequest!!)
        }
    }

    override fun setUp(view: View) {
        setupCalendarView()
        presenter.allHospitalBranch()
        setupRefresh()
    }

    private fun setupRefresh() {
        binding.swipeProfile!!.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                if(currentDateForRequest != null) {
                    presenter.getAllReceptionApiCall(selectedBranch, currentDateForRequest!!)
                }else{
                    binding?.swipeProfile?.isRefreshing = false
                }
            }
        })
        binding.swipeProfile!!.setColorSchemeColors(
            getResources().getColor(android.R.color.holo_blue_bright),
            getResources().getColor(android.R.color.holo_green_light),
            getResources().getColor(android.R.color.holo_orange_light),
            getResources().getColor(android.R.color.holo_red_light)
        )
    }

    override fun onDestroyB() {
        presenter.onDetachView()
    }
    override fun onStartSetStatusFragment(status: Int) {}

    private fun setupCalendarView() {
        binding.calendarSchedule!!.state().edit()
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setCalendarDisplayMode(CalendarMode.WEEKS)
            .commit()
        binding.calendarSchedule!!.setTitleFormatter(MonthArrayTitleFormatter(resources.getStringArray(R.array.calendar_months_array)))
        binding.calendarSchedule!!.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getStringArray(R.array.calendar_days_array)))
        binding.calendarSchedule!!.visibility = View.GONE
    }

    fun setHospitalBranch(list: List<SettingsAllBranchHospitalResponse>) {
        if (list[0].nameBranch != null && list.size > 0) initSpinner(list)
    }

    var spinnerAdapter: BranchSpinnerAdapter? = null
    private fun initSpinner(list: List<SettingsAllBranchHospitalResponse>) {
        val spinnerAdapter = BranchSpinnerAdapter(requireContext(), list)
        binding.spinnerBranch!!.adapter = spinnerAdapter
        binding.spinnerBranch!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (spinnerAdapter.getListItem(position).idBranch == selectedBranch) return
                selectedBranch = spinnerAdapter.getListItem(position).idBranch
                presenter.getDataFrom(selectedBranch)
                if (appointmentAdapter != null) appointmentAdapter?.clear()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }



    private fun showAndHideRootEmpty(boo: Boolean) {
        if (boo) {
            binding.rootEmpty!!.visibility = View.VISIBLE
            binding.rvSchedule!!.visibility = View.GONE
        } else {
            binding.rootEmpty!!.visibility = View.GONE
            binding.rvSchedule!!.visibility = View.VISIBLE
        }
    }


    fun setupCalendar(todayString: String?, response: MutableList<VisitItem>) {
        val cd = binding.calendarSchedule!!.minimumDate
        if (cd == null) {
            val min = CalendarDay.from(TimesUtils.stringToDate(todayString))
            val max = Calendar.getInstance()
            max.time = TimesUtils.stringToDate(todayString)
            max.add(Calendar.DAY_OF_MONTH, 27)
            binding.calendarSchedule!!.state().edit()
                .setMinimumDate(min)
                .setMaximumDate(max)
                .commit()
            binding.calendarSchedule!!.visibility = View.VISIBLE
            binding.calendarSchedule!!.addDecorators(
                SelectDecorator(
                    activity
                )
            )
            binding.calendarSchedule!!.setOnDateChangedListener(this)
            binding.calendarSchedule!!.setOnMonthChangedListener(this)
        }
        updateCalendar(response, todayString)
    }


    fun updateCalendar(response: MutableList<VisitItem>, todayString: String?) {
        //listCalendarPlaces=new ArrayList<>();
        binding.rvSchedule!!.visibility = View.GONE
        visitList.clear()
        val calendar = Calendar.getInstance()
        calendar.time = TimesUtils.stringToDate(todayString)
        calendar.add(Calendar.MONTH, 0)
        binding.calendarSchedule!!.removeDecorators()
        binding.calendarSchedule!!.addDecorators(
            SelectDecorator(
                activity
            )
        )
        binding.calendarSchedule!!.clearSelection()
        if (response[0].naim == null) {
            showAndHideRootEmpty(true)
            binding.dayEmpty!!.visibility = View.GONE
            return
        } else {
            visitList.addAll(response)
            showAndHideRootEmpty(false)
            binding.dayEmpty!!.visibility = View.GONE
        }
        while (response.size != 0) {
            val currentDay = response[0].date
            val date = TimesUtils.stringToDate(currentDay)
            val calendarDay = CalendarDay.from(date)
            binding.calendarSchedule!!.addDecorator(
                DayDecorator(
                    requireContext(),
                    calendarDay,
                    DayDecorator.DAY_MODE_MANY
                )
            )
            calendar.add(Calendar.DATE, 1)
            val dateTmp = response[0].date
            var i = 0
            while (response.size > i) {
                if (response[i].date == dateTmp) {
                    response.remove(response[i])
                    i--
                }
                i++
            }
        }
        binding.calendarSchedule!!.visibility = View.VISIBLE
        checkNotSectCurrentDay()
        if (binding.calendarSchedule!!.selectedDate != null) {
            onDateSelected(binding.calendarSchedule!!, binding.calendarSchedule!!.selectedDate, true)
        }
    }

    fun checkNotSectCurrentDay() {
        val todayString = TimesUtils.currrentDate
        var i = 0
        while (visitList.size > i) {
            if (visitList.get(i).date == todayString) {
                val date = TimesUtils.stringToDate(todayString)
                binding.calendarSchedule!!.setSelectedDate(date)
                break
            }
            i++
        }
    }


    override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
        val response: List<VisitItem> = visitList
        binding.dayEmpty!!.visibility = View.VISIBLE
        binding.rvSchedule!!.visibility = View.GONE
        val listSelected: MutableList<VisitItem> = ArrayList()
        val dd = date.date
        val sData = TimesUtils.dateToString(dd, TimesUtils.DATE_FORMAT_ddMMyyyy)
        for (dateResponse in visitList) {
            if (sData == dateResponse.date) {
                binding.dayEmpty!!.visibility = View.GONE
                listSelected.add(dateResponse)
            }
        }
        if (visitList.size == 0 || listSelected.size == 0) {
            binding.cabinetName!!.visibility = View.GONE
            return
        } else {
            binding.cabinetName!!.visibility = View.VISIBLE
            binding.cabinetName!!.text =
                listSelected[0].kab + " (" + listSelected[0].start + " - " + listSelected[0].end + ")"
        }
        binding.rvSchedule!!.visibility = View.VISIBLE
        appointmentAdapter =
            AppointmentAdapter(
                requireContext(),
                listSelected, listener = object : AppointmentHolder.AppointmentHolderListener{
                    override fun appointmentConfirmed(item: VisitItem) {
                        presenter.appointmentConfirm(item, selectedBranch, currentDateForRequest)
                    }

                }
            )
        binding.rvSchedule!!.layoutManager = LinearLayoutManager(context)
        binding.rvSchedule!!.adapter = appointmentAdapter
    }

    override fun onMonthChanged(widget: MaterialCalendarView?, date: CalendarDay) {
        binding.cabinetName!!.visibility = View.GONE
        val sData = TimesUtils.dateToString(date.date, TimesUtils.DATE_FORMAT_ddMMyyyy)
        currentDateForRequest = sData
        presenter.getAllReceptionApiCall(selectedBranch, currentDateForRequest!!)
        if (appointmentAdapter != null) appointmentAdapter?.clear()
    }

    fun visitItemIsConfirmedNeedUpdateInRecy(item: VisitItem){
        appointmentAdapter?.updateConfirmedItem(item)
    }

    fun showErrorScreen() {}
}