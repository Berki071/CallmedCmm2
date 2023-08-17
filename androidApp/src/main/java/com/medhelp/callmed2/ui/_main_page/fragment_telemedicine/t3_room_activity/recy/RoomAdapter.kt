package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.recy

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmedcmm2.model.chat.MessageRoomItem
import com.medhelp.callmed2.databinding.ItemChatFileBinding
import com.medhelp.callmed2.databinding.ItemChatImgBinding
import com.medhelp.callmed2.databinding.ItemChatMsgBinding
import com.medhelp.callmed2.databinding.ItemChatMsgDateBinding
import com.medhelp.callmed2.databinding.ItemChatMsgTariffBinding
import com.medhelp.callmed2.databinding.ItemChatRecordAudioBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.main.MDate
import io.realm.kotlin.types.ObjectId

class RoomAdapter(private val contex: Context, var list: MutableList<MessageRoomItem>, val recyListener: RecyListener, val recy: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recyList: MutableList<MessageRoomItem> = ArrayList<MessageRoomItem>()

//    private val visibleThreshold = 10
//    private var isLoading = false
    init {
        recyList = list.asReversed()
    }



    override fun getItemViewType(position: Int): Int {
        return when (recyList[position].type) {
            T3RoomActivity.MsgRoomType.DATE.toString() -> T3RoomActivity.MsgRoomType.DATE.id
            T3RoomActivity.MsgRoomType.TARIFF.toString() -> T3RoomActivity.MsgRoomType.TARIFF.id
            T3RoomActivity.MsgRoomType.TEXT.toString() -> T3RoomActivity.MsgRoomType.TEXT.id
            T3RoomActivity.MsgRoomType.IMG.toString() -> T3RoomActivity.MsgRoomType.IMG.id
            T3RoomActivity.MsgRoomType.FILE.toString() -> T3RoomActivity.MsgRoomType.FILE.id
            T3RoomActivity.MsgRoomType.REC_AUD.toString() -> T3RoomActivity.MsgRoomType.REC_AUD.id
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            T3RoomActivity.MsgRoomType.DATE.id -> {
                val view = LayoutInflater.from(contex).inflate(R.layout.item_chat_msg_date, parent, false)
                val bindingItem = ItemChatMsgDateBinding.bind(view)
                return RoomHolderDate(bindingItem)
            }
            T3RoomActivity.MsgRoomType.TARIFF.id -> {
                val view = LayoutInflater.from(contex).inflate(R.layout.item_chat_msg_tariff, parent, false)
                val bindingItem = ItemChatMsgTariffBinding.bind(view)
                return RoomHolderTariff(bindingItem)
            }
            T3RoomActivity.MsgRoomType.TEXT.id -> {
                val view = LayoutInflater.from(contex).inflate(R.layout.item_chat_msg, parent, false)
                val bindingItem = ItemChatMsgBinding.bind(view)
                return RoomHolderMsg(bindingItem, recyListener)
            }
            T3RoomActivity.MsgRoomType.IMG.id -> {
                val view = LayoutInflater.from(contex).inflate(R.layout.item_chat_img, parent, false)
                val bindingItem = ItemChatImgBinding.bind(view)
                return RoomHolderImg(bindingItem, recyListener)
            }
            T3RoomActivity.MsgRoomType.FILE.id -> {
                val view = LayoutInflater.from(contex).inflate(R.layout.item_chat_file, parent, false)
                val bindingItem = ItemChatFileBinding.bind(view)
                return T3RoomHolderFile(bindingItem, recyListener)
            }
            T3RoomActivity.MsgRoomType.REC_AUD.id -> {
                val view = LayoutInflater.from(contex).inflate(R.layout.item_chat_record_audio, parent, false)
                val bindingItem = ItemChatRecordAudioBinding.bind(view)
                return T3RoomHolderRecordAudio(bindingItem, recyListener)
            }
        }

        val view = LayoutInflater.from(contex).inflate(R.layout.item_chat_msg_date, parent, false)
        val bindingItem = ItemChatMsgDateBinding.bind(view)
        return RoomHolderDate(bindingItem)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type: String = recyList[position].type ?: "0"
        when (type) {
            T3RoomActivity.MsgRoomType.DATE.toString() -> (holder as RoomHolderDate).onBinView(recyList[position])
            T3RoomActivity.MsgRoomType.TARIFF.toString() -> (holder as RoomHolderTariff).onBinView(recyList[position])
            T3RoomActivity.MsgRoomType.TEXT.toString() -> (holder as RoomHolderMsg).onBinView(recyList[position])
            T3RoomActivity.MsgRoomType.IMG.toString() -> (holder as RoomHolderImg).onBinView(recyList[position])
            T3RoomActivity.MsgRoomType.FILE.toString() -> (holder as T3RoomHolderFile).onBinView(recyList[position])
            T3RoomActivity.MsgRoomType.REC_AUD.toString() -> (holder as T3RoomHolderRecordAudio).onBinView(recyList[position])
        }
    }
    override fun getItemCount(): Int {
        return recyList.size
    }


    fun getAllMessageWithImage() : MutableList<MessageRoomItem> {
        var newList: MutableList<MessageRoomItem> = mutableListOf()

        for(i in recyList){
            if(i.type == T3RoomActivity.MsgRoomType.IMG.toString()) {
                newList.add(i)
            }
        }
        return newList
    }

    fun updateItemByPathFileUri(pathUri: String){
        for (i in 0 until recyList.size){
            if((recyList[i].type == T3RoomActivity.MsgRoomType.IMG.toString() || recyList[i].type == T3RoomActivity.MsgRoomType.FILE.toString()) && recyList[i].text==pathUri){
                notifyItemChanged(i)
            }
        }
    }
    fun updateItemIdMessageById(id: ObjectId, idMessage: Int, date: String, text: String? = null){
        for (i in 0 until recyList.size){
            if(recyList[i].idMessage == idMessage && recyList[i]._id!=id){
                recyList.remove(recyList[i])
                notifyItemRemoved(i)
                break
            }
        }

        for (i in 0 until recyList.size){
            if(recyList[i]._id == id){
                recyList[i].idMessage = idMessage
                recyList[i].data = date
                if(text!=null)
                    recyList[i].text=text
                notifyItemChanged(i)
                break
            }
        }
    }

    fun addMessagesForShow(listMsg: MutableList<MessageRoomItem>){
        if(listMsg.size>0 && recyList.size>0 && recyList.first().idMessage == listMsg[0].idMessage){
            return
        }

        for(i in 0 until listMsg.size){

            if (listMsg[i].type == T3RoomActivity.MsgRoomType.DATE.toString()) {
                var isAdd = true
                val dateFormat = MDate.stringToString(listMsg[i].data!!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_ddMMyyyy)

                for (j in 0 until recyList.size) {
                    if(recyList[j].type == T3RoomActivity.MsgRoomType.DATE.toString()) {
                        val dateFormatRecy = MDate.stringToString(recyList[j].data!!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_ddMMyyyy)
                        if (dateFormat == dateFormatRecy) {
                            isAdd = false
                            break
                        }
                    }
                }

                if (isAdd) {
                    recyList.add(0, listMsg[i])
                    notifyItemRangeInserted(0, 1)
                }
            }

            if (listMsg[i].type == T3RoomActivity.MsgRoomType.TARIFF.toString()) {
                var isAdd = true
                for (j in 0 until recyList.size) {
                    if (recyList[j].type == T3RoomActivity.MsgRoomType.TARIFF.toString() && listMsg[i].idTm == recyList[j].idTm) {
                        isAdd = false
                        break
                    }
                }

                if (isAdd) {
                    recyList.add(0, listMsg[i])
                    notifyItemRangeInserted(0, 1)
                }
            }

            if (listMsg[i].type == T3RoomActivity.MsgRoomType.TEXT.toString() || listMsg[i].type == T3RoomActivity.MsgRoomType.IMG.toString() || listMsg[i].type == T3RoomActivity.MsgRoomType.FILE.toString() || listMsg[i].type == T3RoomActivity.MsgRoomType.REC_AUD.toString()) {
                var isExist: Int? = null

                if(listMsg[i].idMessage != null) {
                    for (j in 0 until recyList.size) {
                        if (recyList[j].type == T3RoomActivity.MsgRoomType.TEXT.toString() || recyList[j].type == T3RoomActivity.MsgRoomType.IMG.toString() || recyList[j].type == T3RoomActivity.MsgRoomType.FILE.toString()) {
                            if (listMsg[i].idMessage == recyList[j].idMessage) {
                                isExist = j
                                break
                            }
                        }
                    }
                }

                if(isExist == null){
                    recyList.add(0, listMsg[i])
                    notifyItemRangeInserted(0, 1)

                }else{
                    recyList.removeAt(isExist)
                    recyList.add(isExist, listMsg[i])
                    notifyItemChanged(isExist)
                }
            }
        }

        recy.scrollToPosition(0);
    }

    fun deleteItem(item: MessageRoomItem){
        val position = recyList.indexOf(item)
        recyList.remove(item)
        notifyItemRemoved(position)

        var positionDate: Int? = null
        var positionTariff: Int? = null

        //удаляет ненужные даты и тарифы в начале и середине списка
        for(i in recyList.size-1 downTo 0){
            if(recyList[i].type == T3RoomActivity.MsgRoomType.DATE.toString()){
                if(positionDate == null)
                    positionDate = i
                else{
                    recyList.removeAt(positionDate)
                    notifyItemRemoved(positionDate)
                    positionDate = i
                }
            }

            if(recyList[i].type == T3RoomActivity.MsgRoomType.TARIFF.toString()){
                if(positionTariff == null)
                    positionTariff = i
                else{
                    recyList.removeAt(positionTariff)
                    notifyItemRemoved(positionTariff)
                    positionTariff = i
                }
            }

            if(recyList[i].type != T3RoomActivity.MsgRoomType.DATE.toString() && recyList[i].type != T3RoomActivity.MsgRoomType.TARIFF.toString()){
                positionDate = null
                positionTariff = null
            }
        }

        // если positionDate или positionTariff не равны нулю значит в конце списка ненужные дата или тарифф
        positionDate?.let {
            recyList.removeAt(it)
            notifyItemRemoved(it)
            positionDate = null
        }
        positionTariff?.let{
            recyList.removeAt(it)
            notifyItemRemoved(it)
            positionTariff = null
        }
    }

    fun getLastMessage(): MessageRoomItem? {
        return if(recyList.size == 0) null else recyList[0]
    }


    interface RecyListener {
        fun finishLoading()
        fun clickedShowBigImage(item: MessageRoomItem)
        fun clickedShowFile(item: MessageRoomItem)
        //fun clickDeleteFile(item: MessageRoomItem)
        fun clickLongClick(item: MessageRoomItem?) : Boolean

        fun getStateProximity() : T3RoomActivity.ProximitySensorState
        fun addListenerProximityState(listener: ListenerStateProximity?)
    }

    interface ListenerStateProximity{
        fun changeState(state: T3RoomActivity.ProximitySensorState?)
    }
}