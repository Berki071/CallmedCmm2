package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.ScheduleItem
import com.medhelp.callmed2.data.model.ServiceItem
import com.medhelp.callmed2.databinding.DfSelectDoctorAndTimeBinding
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model.DataRecyServiceRecord
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model.RecordData
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.recy.RecordAdapter
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.recy.RecordItemHolder
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_user.SelectUserForRecordDialog
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.main.MDate
import java.util.*

class SelectDoctorAndTimeDialog : DialogFragment() {
    fun setData(serviceItem: ServiceItem?) {
        this.serviceItem = serviceItem
    }


   // @BindView(R.id.app_bar_layout)
//    var appBarLayout: AppBarLayout? = null
//    @BindView(R.id.compactcalendar_view)
//    var compactCalendarView: CompactCalendarView? = null
//    @BindView(R.id.toolbar)
//    var toolbar: Toolbar? = null
//    @BindView(R.id.date_picker_button)
//    var datePickerButton: RelativeLayout? = null
//    @BindView(R.id.date_picker_text_view)
//    var datePickerTextView: TextView? = null
//    @BindView(R.id.collapsingToolbarLayout)
//    var toolbarLayout: CollapsingToolbarLayout? = null
//    @BindView(R.id.recy)
//    var recy: RecyclerView? = null
//    @BindView(R.id.recordingModeTitle)
//    var recordingModeTitle: TextView? = null
//    @BindView(R.id.rootEmpty)
//    var rootEmpty: ConstraintLayout? = null
//

    lateinit var binding: DfSelectDoctorAndTimeBinding

    var serviceItem: ServiceItem? = null
    var presenter: SelectDoctorAndTimePresenter? = null
    private var isExpanded = false //стрелочка раскрытия календаря
    var currentSpaceDateForCalendar: List<String>? = null
    var recordAdapter: RecordAdapter? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {   //косяк на планшете... в районе тулбара кончалось окно и был полупрозрачный фон
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        //костыль, по умолчанию окно показывается не во весь размер
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.df_select_doctor_and_time, null)
        binding = DfSelectDoctorAndTimeBinding.bind(v)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = SelectDoctorAndTimePresenter(this, requireContext())
        binding.recy.post {
            Different.showLoadingDialog(context)
            presenter!!.getFreeTileAll(currentSpaceDateForCalendar!![0], serviceItem!!)
        }
        setupToolbar()
        binding.recordingModeTitle.text = serviceItem!!.title
    }

    //region toolbar
    private fun setupToolbar() {
        binding.toolbar!!.setNavigationOnClickListener { clickBack() }

        //выравнивание содержимого контейнера datePickerButton по центру
        binding.toolbar!!.post {
            val lp = binding.datePickerButton!!.layoutParams as Toolbar.LayoutParams
            lp.rightMargin = binding.toolbar!!.width - binding.datePickerButton!!.width
        }
        lockAppBarClosed()
        binding.compactCalendarView!!.setLocale(TimeZone.getDefault(), Locale.getDefault())
        binding.compactCalendarView!!.setShouldDrawDaysHeader(true)
        binding.compactCalendarView!!.setListener(object : CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                //float rotation = isExpanded ? 0 : 180;
                //ViewCompat.animate(arrow).rotation(rotation).start();
                setExpandedAppBarLayout(!isExpanded)
                binding.appBarLayout!!.setExpanded(isExpanded, true)
                setTitleToolbar(dateClicked.time)
                if (isExpanded) {
                    unlockAppBarOpen()
                } else {
                    lockAppBarClosed()
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                setTitleToolbar(firstDayOfNewMonth.time)
            }
        })

        // Set current date to today
        setCurrentDateInTheCalendarInToolbar(Date())
        binding.datePickerButton!!.setOnClickListener { v: View? ->
            // float rotation = isExpanded ? 0 : 180;
            //ViewCompat.animate(arrow).rotation(rotation).start();
            setExpandedAppBarLayout(!isExpanded)
            if (isExpanded) {
                unlockAppBarOpen()
            } else {
                lockAppBarClosed()
            }
            binding.appBarLayout!!.setExpanded(isExpanded, true)
        }
    }

    fun unlockAppBarOpen() {
        binding.appBarLayout!!.setExpanded(true, false)
        binding.appBarLayout!!.isActivated = true
        val lp = binding.appBarLayout!!.layoutParams as CoordinatorLayout.LayoutParams
        lp.height = Different.convertDpToPixel(250f, requireContext()).toInt()
    }

    private fun setExpandedAppBarLayout(isExpanded: Boolean) {
        this.isExpanded = isExpanded
        //true календарь разворачивается, false сворачивается
    }

    private fun setTitleToolbar(dateClicked: Long) {
//от повтора
        //Long dateClickLong=dateClicked.getTime();
        var l1: Long = 0
        var l2: Long = 0
        if (currentSpaceDateForCalendar != null) {
            l1 = MDate.stringToLong(currentSpaceDateForCalendar!![0], MDate.DATE_FORMAT_ddMMyyyy)
            l2 = MDate.stringToLong(currentSpaceDateForCalendar!![6], MDate.DATE_FORMAT_ddMMyyyy)
        }
        currentSpaceDateForCalendar = MDate.getStringWeek(dateClicked)
        binding.datePickerTextView!!.text = currentSpaceDateForCalendar!!.get(0) + " - " + currentSpaceDateForCalendar!!.get(6)

//от меньше текущей
        val d1 = MDate.currenDateLong
        val d2 = MDate.stringToLong(currentSpaceDateForCalendar!!.get(6), MDate.DATE_FORMAT_ddMMyyyy)
        if (d1 > d2) {
            showEmptyRecy(true)
            return
        } else {
            showEmptyRecy(false)
        }
        if (l1 != 0L && l2 != 0L && (dateClicked < l1 || dateClicked > l2)) {
            binding.recy!!.post {
                presenter!!.getFreeTileAll(currentSpaceDateForCalendar!!.get(0), serviceItem!!)
            }
        }
    }

    fun lockAppBarClosed() {
        binding.appBarLayout!!.setExpanded(false, false)
        binding.appBarLayout!!.isActivated = false
        val lp = binding.appBarLayout!!.layoutParams as CoordinatorLayout.LayoutParams
        lp.height = toolBarHeight
    }

    private fun setCurrentDateInTheCalendarInToolbar(date: Date) {
        setTitleToolbar(date.time)
        if (binding.compactCalendarView != null) {
            binding.compactCalendarView!!.setCurrentDate(date)
        }
    }

    val toolBarHeight: Int
        get() {
            val attrs = intArrayOf(android.R.attr.actionBarSize)
            val ta = requireActivity().obtainStyledAttributes(attrs)
            val toolBarHeight = ta.getDimensionPixelSize(0, -1)
            ta.recycle()
            return toolBarHeight
        }

    //endregion
    private fun clickBack() {
        dismiss()
    }

    private fun showEmptyRecy(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recy!!.visibility = View.GONE
            binding.includedEmpty.rootEmpty!!.visibility = View.VISIBLE
        } else {
            binding.recy!!.visibility = View.VISIBLE
            binding.includedEmpty.rootEmpty!!.visibility = View.GONE
        }
    }

    fun initRecy(listRecy: List<DataRecyServiceRecord?>) {
        if (listRecy.size == 0) showEmptyRecy(true) else {
            showEmptyRecy(false)
            val linearLayoutManager = LinearLayoutManager(context)
            binding.recy!!.layoutManager = linearLayoutManager
            recordAdapter = RecordAdapter(listRecy, requireContext(), object : RecordItemHolder.RecordItemHolderListener{
                override fun clickTime(data: ScheduleItem, time: String) {
                    val recordData = RecordData()
                    recordData.serviceItem = serviceItem
                    recordData.scheduleItem = data
                    recordData.time = time
                    val selectUserForRecordDialog = SelectUserForRecordDialog()
                    selectUserForRecordDialog.setData(recordData, object : SelectUserForRecordDialog.SelectUserForRecordDialogListener{
                        override fun positiveClickButton() {
                            dismiss()
                        }
                    })
                    selectUserForRecordDialog.show(parentFragmentManager, "SelectUserForRecordDialog")
                }
            })

            binding.recy!!.adapter = recordAdapter
            showOpenRecyItem()
        }
    }

    private fun showOpenRecyItem() {
        val curDate = MDate.curentDate
        if (recordAdapter!!.groups.size > 0 && (recordAdapter!!.groups[0].title == curDate || recordAdapter!!.groups.size == 1)) {
            if (!recordAdapter!!.isGroupExpanded(0)) recordAdapter!!.toggleGroup(0)
        }
    }
}