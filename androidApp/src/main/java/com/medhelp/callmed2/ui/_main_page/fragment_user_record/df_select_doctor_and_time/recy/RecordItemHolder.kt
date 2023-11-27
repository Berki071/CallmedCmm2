package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.recy

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.ScheduleItem
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.databinding.ItemRecordItemHolderBinding
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.WorkTofFile.show_file.ShowFile2
import com.medhelp.callmed2.utils.WorkTofFile.show_file.ShowFile2.BuilderImage
import com.medhelp.callmed2.utils.WorkTofFile.show_file.ShowFile2.ShowListener
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class RecordItemHolder(val bindingItem: ItemRecordItemHolderBinding, listener: RecordItemHolderListener) : ChildViewHolder(bindingItem.root) {

//    @BindView(R.id.topBox)
//    var topBox: ConstraintLayout? = null
//    @BindView(R.id.userIco)
//    var userIco: CircleImageView? = null
//    @BindView(R.id.title)
//    var title: TextView? = null
//    @BindView(R.id.address)
//    var address: TextView? = null
//    @BindView(R.id.timeBox)
//    var timeBox: LinearLayout? = null
//    @BindView(R.id.title_picker_arrow)
//    var title_picker_arrow: ImageView? = null

    var context: Context
    var data: ScheduleItem? = null
    var date: String? = null
    var listener: RecordItemHolderListener
    var prefManager: PreferencesManager? = null

    init {
        context = itemView.context
        //ButterKnife.bind(this, itemView)
        this.listener = listener
        bindingItem.timeBox!!.visibility = View.GONE
        bindingItem.topBox!!.setOnClickListener {
            if (bindingItem.timeBox!!.visibility == View.VISIBLE) {
                changeVisibleTimeBox(false)
            } else {
                changeVisibleTimeBox(true)
            }
        }
        val prefManager = PreferencesManager(context)
        val networkManager = NetworkManager(prefManager)
    }

    fun onBind(data: ScheduleItem, date: String?) {
        this.data = data
        this.date = date
        bindingItem.title!!.text = data.fullName
        bindingItem.address!!.text = data.filialItem.name + " (" + data.filialItem.address + ")"
        fillingTimeBox()
        if (data.isOpenListTime) changeVisibleTimeBox(true) else changeVisibleTimeBox(false)

        //userIco.setImageResource(R.drawable.user);
        if (data.foto != null && data.foto != "") {
            BuilderImage(context)
                .setType(ShowFile2.TYPE_ICO)
                .load(data.foto)
                .token(prefManager!!.accessToken)
                .imgError(R.drawable.sh_doc)
                .into(bindingItem.userIco)
                .setListener(object : ShowListener {
                    override fun complete(file: File?) {}
                    override fun error(error: String?) {
                        Log.wtf("", "")
                    }
                })
                .build()
        } else {
            bindingItem.userIco!!.setImageResource(R.drawable.sh_doc)
        }
    }

    private fun fillingTimeBox() {
        bindingItem.timeBox!!.removeAllViews()
        val lines =if(data!!.admTime == null) 0 else {if (data!!.admTime.size % 4 != 0) data!!.admTime.size / 4 + 1 else data!!.admTime.size / 4}
        val listTimetable = creteButton(data!!.admTime)
        for (i in 0 until lines) {
            val tmpLL = LinearLayout(context)
            tmpLL.orientation = LinearLayout.HORIZONTAL
            val layoutParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            bindingItem.timeBox!!.addView(tmpLL, layoutParam)

            //1 btn
            tmpLL.addView(listTimetable[i * 4])

            //2 btn
            if (i * 4 + 1 < data!!.admTime.size) {
                tmpLL.addView(listTimetable[i * 4 + 1])
            } else {
                tmpLL.addView(creteEmptyButton())
            }

            //3 btn
            if (i * 4 + 2 < data!!.admTime.size) {
                tmpLL.addView(listTimetable[i * 4 + 2])
            } else {
                tmpLL.addView(creteEmptyButton())
            }

            //4 btn
            if (i * 4 + 3 < data!!.admTime.size) {
                tmpLL.addView(listTimetable[i * 4 + 3])
            } else {
                tmpLL.addView(creteEmptyButton())
            }
        }
    }

    private fun creteButton(timeList: List<String>): List<Button> {
        val listTimetable: MutableList<Button> = ArrayList()
        for (i in timeList.indices) {
            val layoutParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                Different.convertDpToPixel(37f, context).toInt(),
                1f
            )
            layoutParam.setMargins(8, 8, 8, 8)
            val btn = Button(context)
            btn.layoutParams = layoutParam
            btn.text = timeList[i]
            btn.setBackgroundResource(R.drawable.green_btn)
            btn.setTextColor(Color.WHITE)
            btn.setOnClickListener { v -> listener.clickTime(data!!, (v as Button).text.toString()) }
            listTimetable.add(btn)
        }
        return listTimetable
    }

    private fun creteEmptyButton(): Button {
        val lP = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            Different.convertDpToPixel(37f, context).toInt(),
            1f
        )
        lP.setMargins(4, 4, 4, 4)
        val btn = Button(context)
        btn.layoutParams = lP
        btn.visibility = View.INVISIBLE
        return btn
    }

    private fun changeVisibleTimeBox(isVisible: Boolean) {
        if (isVisible) {
            bindingItem.timeBox!!.visibility = View.VISIBLE
            data!!.openListTime = true
        } else {
            bindingItem.timeBox!!.visibility = View.GONE
            data!!.openListTime = false
        }
        val rotation: Float = if (!isVisible) 0F else 180.toFloat()
        ViewCompat.animate(bindingItem.titlePickerArrow).rotation(rotation).start()
    }

    interface RecordItemHolderListener {
        fun clickTime(data: ScheduleItem, time: String)
    }
}