package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_user

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.UserForRecordItem
import com.medhelp.callmed2.databinding.DfSelectUserForRecordBinding
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_add_user.AddUserDialog
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_add_user.AddUserDialog.AddUserDialogListener
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_entry_user_to_doctor.EntryUserToDoctorDialog
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_entry_user_to_doctor.EntryUserToDoctorDialog.EntryUserToDoctorDialogListener
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model.RecordData
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_user.recy.SelectUserForRecordAdapter
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_user.recy.SelectUserForRecordHolder
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_user.recy.SelectUserForRecordTooltip
import java.util.*

class SelectUserForRecordDialog : DialogFragment() {
    fun setData(recordData: RecordData?, listener: SelectUserForRecordDialogListener?) {
        this.recordData = recordData
        this.listener = listener
    }

    var recordData: RecordData? = null
    var listener: SelectUserForRecordDialogListener? = null

//    @BindView(R.id.toolbar)
//    var toolbar: Toolbar? = null
//    @BindView(R.id.recy)
//    var recy: RecyclerView? = null
//    @BindView(R.id.etDopFilter)
//    var etDopFilter: EditText? = null
//    @BindView(R.id.rootEmpty)
//    var rootEmpty: ConstraintLayout? = null
//    @BindView(R.id.til)
//    var til: TextInputLayout? = null
//    var searchView: SearchView? = null
//    @BindView(R.id.rootEmptyText)
//    var rootEmptyText: TextView? = null
//    @BindView(R.id.viewHintSend)
//    var viewHintSend: View? = null
//    @BindView(R.id.cardFilter)
//    var cardFilter: View? = null

    lateinit var binding: DfSelectUserForRecordBinding
    var searchView: SearchView? = null
    var presenter: SelectUserForRecordPresenter? = null

    //region auxiliary
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
            dialog.window!!.setBackgroundDrawableResource(R.color.white)
        }
        setHasOptionsMenu(true)
    }

    //endregion
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.df_select_user_for_record, null)
        binding = DfSelectUserForRecordBinding.bind(v)

        binding.cardFilter!!.visibility = View.GONE
        searchView = binding.toolbar!!.findViewById(R.id.searchView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = SelectUserForRecordPresenter(this)
        showEmptyRecy(true)
        setupToolbar()

//        etDopFilter.setOnEditorActionListener((v, actionId, event) -> {
////            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
////                adapter.setFilter(etDopFilter.getText().toString());
////                etDopFilter.clearFocus();
////            }
////            return false;
////        });
        binding.etDopFilter!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter!!.filter = s.toString()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.til!!.setEndIconOnClickListener {
            binding.etDopFilter!!.setText("")
            if (adapter != null) adapter!!.filter = ""
        }
        binding.includedEmpty.rootEmptyText!!.text =
            "Нет данных для отображения! Измените параметр поиска или фильтра, либо создайте нового пациента."
        testHint()
    }

    private fun setupToolbar() {
        searchView!!.isIconified = false
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (TextUtils.isDigitsOnly(query)) presenter!!.findUserByPhone(
                    query.uppercase(
                        Locale.getDefault()
                    ), recordData!!
                ) else presenter!!.findUserBySurname(
                    query.uppercase(
                        Locale.getDefault()
                    ), recordData!!
                )
                searchView!!.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        searchView!!.clearFocus()
        binding.toolbar!!.setNavigationOnClickListener { dismiss() }
        binding.toolbar!!.inflateMenu(R.menu.menu_select_user_for_record)
        binding.toolbar!!.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_new) {
                val addUserDialog = AddUserDialog()
                addUserDialog.setData(recordData, object : AddUserDialogListener {
                    override fun createdUser(recordData: RecordData?) {
                        initEntryUserToDoctorDialog(recordData)
                    }
                })
                addUserDialog.show(requireFragmentManager(), "AddUserDialog")
                return@OnMenuItemClickListener true
            }
            false
        })
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

    var adapter: SelectUserForRecordAdapter? = null
    fun initRecy(list: MutableList<UserForRecordItem>) {
        if ((list.size == 0 || list.size == 1) && list[0].name == null) {
            showEmptyRecy(true)
            binding.cardFilter!!.visibility = View.GONE
        } else {
            showEmptyRecy(false)
            binding.cardFilter!!.visibility = View.VISIBLE
            if (adapter == null) {
                val linearLayoutManage = LinearLayoutManager(context)
                adapter = SelectUserForRecordAdapter(requireContext(), list, object : SelectUserForRecordHolder.SelectUserForRecordHolderListener{
                    override fun selectUser(data: UserForRecordItem?) {
                        recordData!!.user = data
                        initEntryUserToDoctorDialog(recordData)
                    }
                })
                binding.recy!!.layoutManager = linearLayoutManage
                binding.recy!!.adapter = adapter
            } else {
                adapter!!.setNewList(list)
            }
        }
    }

    private fun initEntryUserToDoctorDialog(recordData: RecordData?) {
        val entryUserToDoctorDialog = EntryUserToDoctorDialog()
        entryUserToDoctorDialog.setData(recordData, object : EntryUserToDoctorDialogListener {
            override fun positiveClickButton(recordData: RecordData?) {
                dismiss()
                listener!!.positiveClickButton()
            }
        })
        entryUserToDoctorDialog.show(requireFragmentManager(), "EntryUserToDoctorDialog")
    }

    private fun testHint() {
        if (presenter!!.prefManager.showHintAddUserInSelectUserForRecord) {
            presenter!!.prefManager.setShowHintAddUserInSelectUserForRecord()
            binding.viewHintSend!!.postDelayed({ SelectUserForRecordTooltip.showTooltipPlus(binding.viewHintSend) }, 100)
        }
    }


    interface SelectUserForRecordDialogListener {
        fun positiveClickButton()
    }
}