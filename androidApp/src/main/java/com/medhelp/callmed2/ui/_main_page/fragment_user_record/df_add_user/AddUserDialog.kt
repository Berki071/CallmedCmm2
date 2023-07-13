package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_add_user

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import br.com.sapereaude.maskedEditText.MaskedEditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.UserForRecordItem
import com.medhelp.callmed2.databinding.DfAddUserBinding
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model.RecordData
import com.medhelp.callmed2.utils.Different

class AddUserDialog : DialogFragment() {
    fun setData(recordData: RecordData?, listener: AddUserDialogListener?) {
        this.recordData = recordData
        this.listener = listener
    }

    var recordData: RecordData? = null
    var listener: AddUserDialogListener? = null
    var presenter: AddUserPresenter? = null

    lateinit var binding: DfAddUserBinding

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
        val v = inflater.inflate(R.layout.df_add_user, null)
        binding = DfAddUserBinding.bind(v)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = AddUserPresenter(this)
        setupToolbar()
        binding.btnOk!!.setOnClickListener { testData() }
        binding.phone!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            false
        }
        binding.surname!!.post { showKeyboard() }
        binding.cbWithoutPhone!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                hideKeyboard()
                binding.til4!!.visibility = View.GONE
            } else binding.til4!!.visibility = View.VISIBLE
        }
    }

    private fun hideKeyboard() {
//        View view = getActivity().getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            if (imm != null) {
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            }
//        }
        val view = binding.root
        if (view != null) {
            val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.surname, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { dismiss() }
    }

    private fun testData() {
        val tmpSurname = binding.surname.text.toString()
        val tmpName = binding.name.text.toString()
        var tmpPatronymic = binding.patronymic.text.toString()
        var tmpPhone = binding.phone.text.toString()
        tmpPhone = Different.getClearPhone(tmpPhone)
        val tmpCdWithoutPhone = binding.cbWithoutPhone.isChecked
        if (tmpSurname.length < 1) {
            showMsg("Поле «Фамилия» обязательно для заполнения")
            return
        }
        if (tmpName.length < 1) {
            showMsg("Поле «Имя» обязательно для заполнения")
            return
        }
        if (!tmpCdWithoutPhone && tmpPhone.length < 1) {
            showMsg("Если не выбрано «без телефона», поле «Телефон» обязательно для заполнения")
            return
        } else if (!tmpCdWithoutPhone && tmpPhone.length != 10) {
            showMsg("Не корректный номер телефона")
            return
        } else if (tmpCdWithoutPhone) tmpPhone = "нет_сотового"
        if (tmpPatronymic.length < 1) tmpPatronymic = "null"
        val newUserData = NewUserData(
            recordData!!.scheduleItem.filialItem.id,
            tmpSurname, tmpName, tmpPatronymic, tmpPhone
        )
        presenter!!.sendNewUser(newUserData)
    }

    fun showMsg(msg: String?) {
        Different.showAlertInfo(activity, null, msg)
    }

    fun createSuccessful(idUser: String?, newUserData: NewUserData) {
        val user = UserForRecordItem()
        user.id = idUser
        user.idFilial = newUserData.idBranch
        user.dr = "null"
        user.patronymic = newUserData.patronymic
        user.surname = newUserData.surname
        user.surnameEncoded = newUserData.surnameEncode
        user.keySurname = newUserData.surnameKey
        user.name = newUserData.name
        user.phone = newUserData.phone
        user.phoneEncoded = newUserData.phoneEncode
        user.keyPhone = newUserData.phoneKey
        recordData!!.user = user
        dismiss()
        listener!!.createdUser(recordData)
    }

    interface AddUserDialogListener {
        fun createdUser(recordData: RecordData?)
    }

    inner class NewUserData(
        var idBranch: String,
        var surname: String,
        var name: String,
        var patronymic: String,
        var phone: String
    ) {
        var surnameEncode: String? = null
        var surnameKey: String? = null
        var phoneEncode: String? = null
        var phoneKey: String? = null

        init {
            encodeSurname()
            encodePhone()

            //String test=Different.encodeWord(surnameEncode,surnameKey);
        }

        private fun encodeSurname() {
            surnameKey = Different.getEncodeKey(surname)
            surnameEncode = Different.encodeDecodeWord(surname, surnameKey!!)
        }

        private fun encodePhone() {
            phoneKey = Different.getEncodeKey(phone)
            phoneEncode = Different.encodeDecodeWord(phone, phoneKey!!)
        }
    }
}