package com.medhelp.callmedcmm2.db

import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.max
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


object RealmDb {
    val config = RealmConfiguration.create(schema = setOf(MessageRoomItem::class))
    val realm: Realm = Realm.open(config)

    fun deleteMessage(item: MessageRoomItem){
        GlobalScope.launch {
            realm.write {
                query<MessageRoomItem>("idMessage == $0", item.idMessage)
                    .first()
                    .find()
                    ?.also { delete(it) }
            }
        }
    }

    fun getAllMessageByIdRoom(idRoom: String): List<MessageRoomItem>{
        val incompleteItems: RealmResults<MessageRoomItem> = realm.query<MessageRoomItem>("idRoom == $0", idRoom).find()

        if(incompleteItems.size==0)
            return listOf()
        else
            return realm.copyFromRealm(incompleteItems)
    }

    fun getMaxIdMessageByIdRoom(idRoom: String) : Int {
        val maxIdMessage: Int? = realm.query<MessageRoomItem>("idRoom == $0", idRoom).max("idMessage", Int::class).find()
        return maxIdMessage ?: 0
    }

    var latchWrite = false
    fun addListMessages(list: List<MessageRoomItem>): List<MessageRoomItem>{
            latchWrite = true


            var listNewM: MutableList<MessageRoomItem> = mutableListOf()

            for (i in list) {
                val item: MessageRoomItem? = realm.query<MessageRoomItem>("idMessage == $0", i.idMessage!!).first().find()

                if (item == null) {
                    listNewM.add(i)
                }
            }

            GlobalScope.launch {
                realm.write {
                    for (i in listNewM) {
                        copyToRealm(i)
                    }
                    latchWrite = false
                }
            }

            return listNewM
    }
    fun updateItemMessageText(item : MessageRoomItem){
        GlobalScope.launch {
            realm.write {
                // fetch a frog from the realm by primary key
                val itemTmp: MessageRoomItem? = this.query<MessageRoomItem>("_id == $0", item._id).first().find()
                // modify the frog's age in the write transaction to persist the new age to the realm
                itemTmp?.text = item.text
            }
        }
    }


    fun isPossibleDeleteCheckMsgUserAfterSelect(item: MessageRoomItem) : Boolean {
        val listItems: RealmResults<MessageRoomItem> = realm.query<MessageRoomItem>("idRoom == $0", item.idRoom!!).find()
        if(listItems.size==0)
            return true

        val listItems2: RealmResults<MessageRoomItem> =  listItems.query("idMessage > $0", item.idMessage).find()
        if(listItems2.size==0)
            return true

        for(i in listItems2){
            if(i.otpravitel == "kl")
                return false
        }

        return true
    }
}