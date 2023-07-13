package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_entry_user_to_doctor

import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.DialogEntryToDoctorBinding
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model.RecordData
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.Different.AlertInfoListener

class EntryUserToDoctorDialog : DialogFragment() {
    fun setData(recordData: RecordData?, listener: EntryUserToDoctorDialogListener?) {
        this.recordData = recordData
        this.listener = listener
    }

    lateinit var binding: DialogEntryToDoctorBinding

    var recordData: RecordData? = null
    var listener: EntryUserToDoctorDialogListener? = null
    var presenter: EntryUserToDoctorPresenter? = null

    //region auxiliary
    override fun onStart() {
        super.onStart()
        //костыль, по умолчанию окно показывается не во весь размер
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        }
        setHasOptionsMenu(true)
    }

    //endregion
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_entry_to_doctor, null)
        binding = DialogEntryToDoctorBinding.bind(v)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = EntryUserToDoctorPresenter(this)
        binding.doctorName!!.text = recordData!!.scheduleItem.fullName
        binding.ysl!!.text = recordData!!.serviceItem.title
        binding.recordingDate!!.text = recordData!!.scheduleItem.admDay
        binding.recordingTime!!.text = recordData!!.time
        val tmpFullName: String
        tmpFullName =
            if (recordData!!.user.patronymic != null && recordData!!.user.patronymic != "" && recordData!!.user.patronymic != "null") recordData!!.user.surname + " " + recordData!!.user.name + " " + recordData!!.user.patronymic else recordData!!.user.surname + " " + recordData!!.user.name
        binding.patient!!.text = tmpFullName
        if (recordData!!.user.dr != null && !recordData!!.user.dr.isEmpty() && recordData!!.user.dr != "null" && recordData!!.user.phone != null && !recordData!!.user.phone.isEmpty() && recordData!!.user.phone != "null") binding.patientHint.text =
            recordData!!.user.dr + ", " + Different.getFormattedPhone(recordData!!.user.phone) else if (recordData!!.user.dr != null && !recordData!!.user.dr.isEmpty() && recordData!!.user.dr != "null") binding.patientHint.text =
            recordData!!.user.dr else if (recordData!!.user.phone != null && !recordData!!.user.phone.isEmpty() && recordData!!.user.phone != "null") binding.patientHint.text =
            Different.getFormattedPhone(
                recordData!!.user.phone
            ) else binding.patientHint!!.visibility = View.GONE

        binding.btnYes.setOnClickListener {
            if (recordData!!.user.idFilial == recordData!!.scheduleItem.filialItem.id)
                presenter!!.sendEntryToServer(recordData!!)
            else
                recordData?.let { presenter!!.getUserIdInOtherBranch(it) }
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    fun recordingSuccessful() {
        Different.showAlertInfo(activity, null, "Запись успешна!", object : AlertInfoListener {
            override fun clickOk() {
                dismiss()
                listener!!.positiveClickButton(recordData)
            }

            override fun clickNo() {}
        }, false)
    }

    interface EntryUserToDoctorDialogListener {
        fun positiveClickButton(recordData: RecordData?)
    }
}