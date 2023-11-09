package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat.recy.RoomAdapter
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmedcmm2.db.RealmDb
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem

class RecyChatView : RecyclerView {
    //region constructors
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }
    //endregion

    var latchScrollToEnd = true
    private val scroll: Parcelable? = null
    var adapter: RoomAdapter? = null
    var listener: RecyChatViewListener? = null

    var presenter: RecyChatPresenter = RecyChatPresenter()
    var presenterItems: RecyItemPresenter = RecyItemPresenter()

    fun init(context: Context) {}
    fun onCreateMainView(){
        presenter.onAttachView(this)
        presenterItems.attachView(this)
    }
    fun onDestroyMainView(){
        presenter.onDetachView()
        presenterItems.detachView()
    }
    fun setData(listener: RecyChatViewListener){
        this.listener = listener
    }
    fun setUp(idRoom: Int){
        presenter.getAllMessageFromRealm(idRoom)
        presenter.getNewMessagesInLoopFromServer(idRoom.toString())
    }

    fun initRecy(listMsg: MutableList<MessageRoomItem>) {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        linearLayoutManager.setReverseLayout(true)


        adapter = RoomAdapter(context, listMsg, object : RoomAdapter.RecyListener {
            override fun finishLoading() {
                if (latchScrollToEnd) {
                    if (scroll == null)
                        recyScrollToStart()
                    else
                        getLayoutManager()?.onRestoreInstanceState(scroll)
                }
            }

            override fun clickedShowBigImage(item: MessageRoomItem) {
                listener?.clickedShowBigImage(item, adapter!!.getAllMessageWithImage())

            }

            override fun clickedShowFile(item: MessageRoomItem) {
                listener?.clickedShowFile(item)

            }

            override fun clickLongClick(item: MessageRoomItem): Boolean {
                val isPossibleDeleteCheckMsgUserAfterSelect = RealmDb.isPossibleDeleteCheckMsgUserAfterSelect(item)

                if (isPossibleDeleteCheckMsgUserAfterSelect) {
                    if (item.otpravitel == "sotr" && listener!!.getRecordItem()!!.status != Constants.TelemedicineStatusRecord.complete.toString() /*&& it.viewKl == "false"*/) {
                        Different.showAlertInfo(context as Activity, "", "Удалить собщение?",
                            object : Different.AlertInfoListener {
                                override fun clickOk() {
                                    presenter.deleteMessageFromServer(item)
                                }
                                override fun clickNo() {}
                            },
                            true
                        )
                        return true
                    }
                }
                return false
            }

            override fun getStateProximity(): T3RoomActivity.ProximitySensorState {
                return listener!!.getStateProximity()
            }

            override fun addListenerProximityState(listenerIn: RoomAdapter.ListenerStateProximity?) {

                if(listenerIn == null){
                    listener!!.setListenerStateProximity(null)
                }
                else if(listener!!.getListenerStateProximity() == null)
                    listener!!.setListenerStateProximity(listenerIn)
                else{
                    listener!!.getListenerStateProximity()?.changeState(null)
                    listener!!.setListenerStateProximity(listenerIn)
                }
            }

            override fun loadFile(message: MessageRoomItem) {
                presenterItems.loadFile(message, object : RecyItemPresenter.RoomHolderProcessingFileListener{
                    override fun error(item: MessageRoomItem, title: String, msg: String) {
                        item.isShowLoading = false
                        adapter?.updateItemByItem(item)
                        Different.showAlertInfo((context as T3RoomActivity), title, msg)
                    }

                    override fun processingFileComplete(item: MessageRoomItem) {
                        RealmDb.updateItemMessageText(item)
                        item.isShowLoading = false
                        adapter?.updateItemByItem(item)
                    }

                })
            }

        }, this)

        layoutManager = linearLayoutManager
        setAdapter(adapter);

        recyScrollToStart()
    }

    fun recyScrollToStart() {
        if (adapter == null)
            return

        scrollToPosition(0)
    }

    interface RecyChatViewListener{
        fun clickedShowBigImage(item: MessageRoomItem, list : MutableList<MessageRoomItem>)
        fun clickedShowFile(item: MessageRoomItem)
        fun getRecordItem(): AllRecordsTelemedicineItem?
        fun getStateProximity(): T3RoomActivity.ProximitySensorState
        fun setListenerStateProximity(item: RoomAdapter.ListenerStateProximity?)
        fun getListenerStateProximity(): RoomAdapter.ListenerStateProximity?
    }
}