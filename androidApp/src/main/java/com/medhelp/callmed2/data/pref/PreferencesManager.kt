package com.medhelp.callmed2.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.medhelp.callmed2.data.model.LogData
import com.medhelp.callmed2.utils.main.AppConstants
import com.medhelp.callmedcmm2.model.CenterResponse
import java.util.ArrayList
import java.util.HashSet
import java.util.TreeSet
import javax.inject.Inject

class PreferencesManager @Inject constructor(context: Context) {
    private val preferences: SharedPreferences

    init {
        val prefName = AppConstants.PREF_NAME
        preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    var centerInfo: CenterResponse.CenterItem?
        get() {
            val tmp = preferences.getString(PREF_KEY_CENTER_INFO, "")
            if (tmp == "") return null
            val gson = Gson()
            return gson.fromJson(tmp, CenterResponse.CenterItem::class.java)
        }
        set(info) {
            val gson = Gson()
            val json = gson.toJson(info)
            preferences.edit().putString(PREF_KEY_CENTER_INFO, json).apply()
        }
    var isShowPartMessenger: Boolean
        get() = preferences.getBoolean(PREF_KEY_SHOW_PART_MESSENGER, false)
        set(boo) {
            preferences.edit().putBoolean(PREF_KEY_SHOW_PART_MESSENGER, boo).apply()
        }
    var isShowPartTimetable: Boolean
        get() = preferences.getBoolean(PREF_KEY_SHOW_PART_TIMETABLE, false)
        set(boo) {
            preferences.edit().putBoolean(PREF_KEY_SHOW_PART_TIMETABLE, boo).apply()
        }
    var isShowPartCallCenter: Boolean
        get() = preferences.getBoolean(PREF_KEY_SHOW_PART_CALL_CENTER, false)
        set(boo) {
            preferences.edit().putBoolean(PREF_KEY_SHOW_PART_CALL_CENTER, boo).apply()
        }
    var isShowPartScanDoc: Boolean
        get() = preferences.getBoolean(PREF_KEY_SHOW_SCAN_DOC, false)
        set(boo) {
            preferences.edit().putBoolean(PREF_KEY_SHOW_SCAN_DOC, boo).apply()
        }
    var isShowPassportRecognize: Boolean
        get() = preferences.getBoolean(PREF_KEY_SHOW_PASSPORT_RECOGNIZE, false)
        set(boo) {
            preferences.edit().putBoolean(PREF_KEY_SHOW_PASSPORT_RECOGNIZE, boo).apply()
        }
    var isShowPartRaspDoc: Boolean
        get() = preferences.getBoolean(PREF_KEY_SHOW_RASP_DOC, false)
        set(boo) {
            preferences.edit().putBoolean(PREF_KEY_SHOW_RASP_DOC, boo).apply()
        }
    var screenLock: Boolean
        get() = preferences.getBoolean(PREF_KEY_SCREEN_LOCK, false)
        set(boo) {
            preferences.edit().putBoolean(PREF_KEY_SCREEN_LOCK, boo).apply()
        }
    var lockScreenCallCenter: Boolean
        get() = preferences.getBoolean(PREF_KEY_LOCK_SCREEN_CALL_CENTER, false)
        set(boo) {
            preferences.edit().putBoolean(PREF_KEY_LOCK_SCREEN_CALL_CENTER, boo).apply()
        }
    var currentUserId: Int?
        get() = preferences.getInt(PREF_KEY_CURRENT_USER_ID, 0)
        set(userId) {
            preferences.edit().putInt(PREF_KEY_CURRENT_USER_ID, userId ?: 0).apply()
        }
    var currentUserName: String?
        get() = preferences.getString(PREF_KEY_CURRENT_USER_NAME, null)
        set(userName) {
            preferences.edit().putString(PREF_KEY_CURRENT_USER_NAME, userName).apply()
        }
    var currentName: String?
        get() = preferences.getString(PREF_KEY_CURRENT_NAME, null)
        set(name) {
            preferences.edit().putString(PREF_KEY_CURRENT_NAME, name).apply()
        }
    var fireBaseToken: String?
        get() = preferences.getString(PREF_KEY_CURRENT_FB_TOKEN, null)
        set(token) {
            preferences.edit().putString(PREF_KEY_CURRENT_FB_TOKEN, token).apply()
        }
    var currentPassword: String?
        get() = preferences.getString(PREF_KEY_CURRENT_PASSWORD, null)
        set(password) {
            preferences.edit().putString(PREF_KEY_CURRENT_PASSWORD, password).apply()
        }

    fun deleteCurrentPassword() {
        preferences.edit().remove(PREF_KEY_CURRENT_PASSWORD).apply()
    }

    var isStartMode: Boolean
        get() = preferences.getBoolean(PREF_KEY_START_MODE, true)
        set(mode) {
            preferences.edit().putBoolean(PREF_KEY_START_MODE, mode).apply()
        }
    var currentCenterId: Int?
        get() = preferences.getInt(PREF_KEY_CURRENT_CENTER_ID, 0)
        set(id_center) {
            preferences.edit().putInt(PREF_KEY_CURRENT_CENTER_ID, id_center ?: 0).apply()
        }
    var accessToken: String?
        get() = preferences.getString(PREF_KEY_CURRENT_TOKEN, "")
        set(token) {
            preferences.edit().putString(PREF_KEY_CURRENT_TOKEN, token).apply()
        }
    var selectedIp: String?
        get() = preferences.getString(PREF_KEY_SELECTED_IP, "")
        set(address) {
            preferences.edit().putString(PREF_KEY_SELECTED_IP, address).apply()
        }
    val showHintAddUserInSelectUserForRecord: Boolean
        get() = preferences.getBoolean(PREF_KEY_SELECT_USER_FOR_RECORD_ADD_USER_HINT, true)

    fun setShowHintAddUserInSelectUserForRecord() {
        preferences.edit().putBoolean(PREF_KEY_SELECT_USER_FOR_RECORD_ADD_USER_HINT, false).apply()
    }

    var isShowNotifications: Boolean
        get() = preferences.getBoolean(PREF_KEY_SHOW_NOTIFICATIONS, true)
        set(boo) {
            preferences.edit().putBoolean(PREF_KEY_SHOW_NOTIFICATIONS, boo).apply()
        }

    var dataShowRaspNoty: String?
        get() = preferences.getString(PREF_KEY_DATE_SHOW_RASP_NOTY, "")
        set(boo) {
            preferences.edit().putString(PREF_KEY_DATE_SHOW_RASP_NOTY, boo).apply()
        }

    //region work with log
    fun addLogItem(item : LogData){
        val gson=Gson()
        val strItem : String= gson.toJson(item)

        var allLogItems=getAllLogItems()
        if(allLogItems==null){
            var newSet = mutableSetOf(strItem)
            preferences!!.edit().putStringSet(LOG_LIST, newSet).apply()
        }else{
            var myTreeSet = TreeSet(allLogItems)

            var latchInsert = false

            for(i in  myTreeSet){
                val tmp=gson.fromJson(i, LogData::class.java)
                if(tmp.idCenter == item.idCenter && tmp.idUser == item.idUser){
                    removeLogItem(tmp)
                    tmp.log += "\n"+item.log
                    latchInsert = true
                    allLogItems=getAllLogItems()
                    myTreeSet = TreeSet(allLogItems)
                    myTreeSet.add(gson.toJson(tmp))
                    break
                }
            }
            if(latchInsert == false)
                myTreeSet.add(strItem)
            preferences!!.edit().putStringSet(LOG_LIST, myTreeSet).apply()
        }
    }
    fun getOneLogItem() : LogData?{
        var allLogItems=getAllLogItems()

//        if (allLogItems != null) {
//            val gson=Gson()
//            for(i in allLogItems){
//                val tmp=gson.fromJson(i, LogData::class.java)
//                Log.wtf("rrrtrettt",MDate.longToString(tmp.getaTime().toLong(),MDate.DATE_FORMAT_HHmmss_ddMMyyyy)+" "+i)
//            }
//            Log.wtf("rrrtrettt","---------------------------")
//        }

        if(allLogItems == null || allLogItems.size==0)
            return null
        else {
            val stringsList: List<String> = ArrayList(allLogItems)
            val gson=Gson()
            val item : LogData = gson.fromJson(stringsList[0], LogData::class.java)
            return item
        }
    }
    fun removeLogItem(item : LogData){
        val gson=Gson()
        val strItem : String= gson.toJson(item)

        var allLogItems=getAllLogItems()

        if(allLogItems!=null) {
            val inSet: MutableSet<String> = HashSet<String>(allLogItems)
            inSet.remove(strItem)
            preferences.edit().putStringSet(LOG_LIST, inSet).apply()
        }
    }
    fun getAllLogItems(): TreeSet<String>? {
        val res=preferences.getStringSet(LOG_LIST, null)
        return if(res == null)
            null
        else
            TreeSet(preferences.getStringSet(LOG_LIST, null))
    }
    //endregion

    companion object {
        private const val PREF_KEY_CURRENT_USER_ID = "PREF_KEY_CURRENT_USER_ID"
        private const val PREF_KEY_CURRENT_USER_NAME = "PREF_KEY_CURRENT_USER_NAME"
        private const val PREF_KEY_CURRENT_PASSWORD = "PREF_KEY_CURRENT_PASSWORD"
        private const val PREF_KEY_CURRENT_NAME = "PREF_KEY_CURRENT_CLIENT_ID"
        private const val PREF_KEY_CURRENT_TOKEN = "PREF_KEY_TOKEN"
        private const val PREF_KEY_CURRENT_FB_TOKEN = "PREF_KEY_CURRENT_FB_TOKEN"
        private const val PREF_KEY_NOTIFICATIONS = "notifications"
        private const val PREF_KEY_CURRENT_CENTER_ID = "PREF_KEY_CURRENT_CENTER_ID"
        private const val PREF_KEY_START_MODE = "PREF_KEY_START_MODE"
        private const val PREF_KEY_CENTER_URL = "PREF_KEY_CENTER_URL"
        private const val PREF_KEY_SELECTED_IP = "PREF_KEY_SELECTED_IP"
        private const val PREF_KEY_SCREEN_LOCK = "PREF_KEY_SCREEN_LOCK"
        private const val PREF_KEY_LOCK_SCREEN_CALL_CENTER = "PREF_KEY_LOCK_SCREEN_CALL_CENTER"
        private const val PREF_KEY_SHOW_PART_CALL_CENTER = "PREF_KEY_SHOW_PART_CALL_CENTER"
        private const val PREF_KEY_SHOW_PART_MESSENGER = "PREF_KEY_SHOW_PART_MESSENGER"
        private const val PREF_KEY_SHOW_PART_TIMETABLE =
            "PREF_KEY_SHOW_PART_TIMETABLE" // запись на прием
        private const val PREF_KEY_SHOW_PASSPORT_RECOGNIZE = "PREF_KEY_SHOW_PASSPORT_RECOGNIZE"
        private const val PREF_KEY_SHOW_SCAN_DOC = "PREF_KEY_SHOW_SCAN_DOC"
        private const val PREF_KEY_SHOW_RASP_DOC = "PREF_KEY_SHOW_RASP_DOC" // расписание доктора
        private const val PREF_KEY_CENTER_INFO = "PREF_KEY_CENTER_INFO"
        private const val PREF_KEY_SELECT_USER_FOR_RECORD_ADD_USER_HINT =
            "PREF_KEY_SELECT_USER_FOR_RECORD_ADD_USER_HINT"
        private const val PREF_KEY_SHOW_NOTIFICATIONS = "PREF_KEY_SHOW_NOTIFICATIONS"
        private const val PREF_KEY_DATE_SHOW_RASP_NOTY = "PREF_KEY_DATE_SHOW_RASP_NOTY"
        private const val LOG_LIST = "LOG_LIST"
    }
}