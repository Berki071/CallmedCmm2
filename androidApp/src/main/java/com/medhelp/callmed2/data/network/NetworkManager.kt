package com.medhelp.callmed2.data.network

import android.content.Context
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.OkHttpResponseListener
import com.medhelp.callmed2.data.model.*
import com.medhelp.callmed2.data.model.timetable.*
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_add_user.AddUserDialog.NewUserData
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model.RecordData
import com.medhelp.callmed2.utils.main.AppConstants
import com.medhelp.callmedcmm2.model.chat.LoadDataZaklAmbItem
import com.medhelp.callmedcmm2.model.chat.ResultZakl2Item
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class NetworkManager @Inject constructor(private val prefManager: PreferencesManager) {
    fun doLoginApiCall(username: String, password: String, context: Context): Observable<UserList> {
        val pd = ProtectionData()
        return Rx2AndroidNetworking.post(LocalEndPoint.LOGIN)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(AUTH, AppConstants.API_KEY)
            .addBodyParameter(USERNAME, username)
            .addBodyParameter(PASSWORD, password)
            //.addBodyParameter(HASH, pd.getSignature(context))
            .build()
            .getObjectObservable(UserList::class.java)
    }

    val currentDateApiCall: Observable<DateList>
        get() = Rx2AndroidNetworking.get(LocalEndPoint.CURRENT_DATE)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(AUTH, AppConstants.API_KEY)
            .build()
            .getObjectObservable(DateList::class.java)

    val centerApiCall: Observable<CenterList>
        get() = Rx2AndroidNetworking.get(LocalEndPoint.CENTER)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(AUTH, AppConstants.API_KEY)
            .addPathParameter(ID_CENTER, prefManager.currentCenterId.toString())
            .build()
            .getObjectObservable(CenterList::class.java)

    fun sendLogToServer(item: LogData): Observable<SimpleResponseBoolean> {
        return Rx2AndroidNetworking.post(LocalEndPoint.SEND_LOG_TO_SERVER)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(AUTH, AppConstants.API_KEY)
            .addPathParameter(ID_USER, item.idUser!!)
            .addPathParameter(ID_BRANCH, item.idBranch)
            .addPathParameter(ID_CENTER, item.idCenter)
            .addPathParameter(TYPE, item.type)
            .addPathParameter(VERSION_CODE, item.version)
            .addBodyParameter("log", item.log)
            .build()
            .getObjectObservable(SimpleResponseBoolean::class.java)
    }

    val allIPCall: Observable<String>
        get() {
            val toc = prefManager.accessToken
            val dd = prefManager.currentUserId.toString()
            val sdfs = prefManager.centerInfo!!.db_name
            return Rx2AndroidNetworking.get(CenterEndPoint.GET_ALL_IP_LIST)
                .addHeaders("host", "oneclick.tmweb.ru")
                .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
                .addHeaders(AUTH, prefManager.accessToken)
                .addHeaders(ID_DOC, prefManager.currentUserId.toString())
                .build() //.getObjectObservable(IPDataList.class);
                //.getJSONObjectObservable();
                .stringObservable
        }

    fun getClientBD(datee: String, ip: String): Observable<JSONObject> {
//        val dbName = prefManager.centerInfo!!.db_name
//        val token = prefManager.accessToken
//        val id_doc = prefManager.currentUserId.toString()
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        return Rx2AndroidNetworking.get(CenterEndPoint.GET_CLIENT_BD_LIST)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(DATE, datee)
            .addPathParameter(IP_ADRESS, ip)
            .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
            .build()
            .jsonObjectObservable
    }

    //+
    fun setCallStatus(id: String, status: String): Observable<RefreshStatusResponse> {
        return Rx2AndroidNetworking.post(CenterEndPoint.SET_CALL_STATUS)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_CALL, id)
            .addPathParameter(STATUS, status)
            .build()
            .getObjectObservable(RefreshStatusResponse::class.java)
    }

    fun uploadImage(idSync: Int, imgBase64: String): Observable<SimpleBooleanList> {

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

       // AndroidNetworking.initialize(getApplicationContext(), okHttpClient)

        return Rx2AndroidNetworking.post(CenterEndPoint.SEND_IMG_FOR_SCANNER)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_SYNC, idSync.toString())
            .addBodyParameter("image", imgBase64)
            .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
            .build()
            .getObjectObservable(SimpleBooleanList::class.java)
    }



    val externalIp: Observable<IPDataResponse>
        get() = Rx2AndroidNetworking.get("https://api.ipify.org?format=json")
            .build()
            .getObjectObservable(IPDataResponse::class.java)
    val allHospitalBranch: Observable<SettingsAllBaranchHospitalList>
        get() = Rx2AndroidNetworking.get(CenterEndPoint.BRANCH_DOC)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_USER, prefManager.currentUserId.toString())
            .build()
            .getObjectObservable(SettingsAllBaranchHospitalList::class.java)

    fun getAllReceptionApiCall(branch: Int, dateMonday: String): Observable<VisitList> {

        return Rx2AndroidNetworking.get(CenterEndPoint.TIME_TABLE_DOC)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_USER, prefManager.currentUserId.toString())
            .addPathParameter(DATE, dateMonday)
            .addPathParameter(ID_BRANCH, branch.toString())
            .build()
            .getObjectObservable(VisitList::class.java)
    }

    val scheduleTomorrow: Observable<TimetableDocNotificationList>
        get() {
            //val dd = prefManager.currentUserId.toString()
            return Rx2AndroidNetworking.get(CenterEndPoint.SCHEDULE_TOMORROW)
                .addHeaders("host", "oneclick.tmweb.ru")
                .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
                .addHeaders(AUTH, prefManager.accessToken)
                .addHeaders(ID_DOC, prefManager.currentUserId.toString())
                .addPathParameter(
                    ID_USER,
                    prefManager.currentUserId.toString()
                ) //.addPathParameter(ID_USER,String.valueOf(7))
                .build()
                .getObjectObservable(TimetableDocNotificationList::class.java)
        }


    //статичные значения как для сайта (здесь нужен id клиента а не доктора)
    val doctorById: Observable<DoctorList>
        get() = Rx2AndroidNetworking.get(CenterEndPoint.DOCTOR_BY_ID)
                .addHeaders("host", "oneclick.tmweb.ru")
                .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
                .addHeaders(AUTH, prefManager.accessToken)
                .addHeaders(ID_DOC, prefManager.currentUserId.toString())
                .addPathParameter(ID_DOCTOR, prefManager.currentUserId.toString())
                .build()
                .getObjectObservable(DoctorList::class.java)

    fun sendIncommingNumber(data: String, phone: String, ip: String?): Observable<SimpleBooleanList> {
        return Rx2AndroidNetworking.get(CenterEndPoint.SEND_PHONE_NUMBER)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(DATE, data)
            .addPathParameter(PHONE, phone)
            .addPathParameter(IP_ADRESS, ip)
            .build()
            .getObjectObservable(SimpleBooleanList::class.java)
    }

    val categoryApiCall: Observable<SpecialtyList>
        get() = Rx2AndroidNetworking.get(CenterEndPoint.CATEGORY)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .build()
            .getObjectObservable(SpecialtyList::class.java)

    val priceApiCall: Observable<ServiceList>
        get() = Rx2AndroidNetworking.get(CenterEndPoint.PRICE)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .build()
            .getObjectObservable(ServiceList::class.java)

    fun getFilialByIdServices(idServices: String): Observable<FilialResponse> {
        return Rx2AndroidNetworking.get(CenterEndPoint.GER_FILIAL_BY_ID_SERVICES)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_SERVICE, idServices)
            .build()
            .getObjectObservable(FilialResponse::class.java)
    }

    fun getScheduleByServiceApiCall(idService: String, date: String, adm: String, branch: String): Observable<ScheduleResponce> {
//        val tmp1 = prefManager.centerInfo!!.db_name
//        val tmp2 = prefManager.accessToken
//        val tmp3 = prefManager.currentUserId.toString()
        return Rx2AndroidNetworking.get(CenterEndPoint.SCHEDULE_SERVICE)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_SERVICE, idService)
            .addPathParameter(DATE, date)
            .addPathParameter(ADM_TIME, adm)
            .addPathParameter(ID_BRANCH, branch)
            .build()
            .getObjectObservable(ScheduleResponce::class.java)
    }

    fun findUserBySurname(surname: String, idBranch: String): Observable<UserForRecordResponse> {
        return Rx2AndroidNetworking.get(CenterEndPoint.FIND_CLIENT_BY_FAM)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(SURNAME, surname)
            .addPathParameter(ID_BRANCH, idBranch)
            .build()
            .getObjectObservable(UserForRecordResponse::class.java)
    }

    fun findUserByPhone(phone: String, idBranch: String): Observable<UserForRecordResponse> {
        return Rx2AndroidNetworking.get(CenterEndPoint.FIND_CLIENT_BY_HONE)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(PHONE, phone)
            .addPathParameter(ID_BRANCH, idBranch)
            .build()
            .getObjectObservable(UserForRecordResponse::class.java)
    }

    fun getUserIdInOtherBranch(oldIdUser: String, oldBranch: String, newBranch: String): Observable<SimpleResponseString> {
        return Rx2AndroidNetworking.post(CenterEndPoint.USER_IN_NEW_BRANCH)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_USER, oldIdUser)
            .addPathParameter(ID_BRANCH, oldBranch)
            .addPathParameter(ID_BRANCH_NEW, newBranch)
            .build()
            .getObjectObservable(SimpleResponseString::class.java)
    }

    fun sendToDoctorVisit(recordData: RecordData): Observable<ResponseString> {
        return Rx2AndroidNetworking.get(CenterEndPoint.RECORD_TO_THE_DOCTOR)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_DOCTOR, recordData.scheduleItem.idDoctor.toString())
            .addPathParameter(DATE, recordData.scheduleItem.admDay)
            .addPathParameter(TIME, recordData.time)
            .addPathParameter(ID_USER, recordData.user.id)
            .addPathParameter(ID_SPEC, recordData.serviceItem.idSpec.toString())
            .addPathParameter(ID_SERVICE, recordData.serviceItem.id.toString())
            .addPathParameter(DURATION, recordData.serviceItem.admission.toString())
            .addPathParameter(ID_BRANCH, recordData.scheduleItem.filialItem.id)
            .build()
            .getObjectObservable(ResponseString::class.java)
    }

    private fun dddddecc(strEncode: String, key: String): String {
        var surname = ""
        try {
            for (i in 0 until strEncode!!.length) surname += (strEncode[i].code xor key!![i].code).toChar()
        } catch (e: Exception) {
        }
        return surname
    }

    fun sendNewUser(newUserData: NewUserData): Observable<SimpleString> {
        val surnDec = dddddecc(newUserData.surnameEncode!!, newUserData.surnameKey!!)
        return Rx2AndroidNetworking.post(CenterEndPoint.SEND_NEW_USER)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_BRANCH, newUserData.idBranch)
            .addPathParameter(SURNAME, newUserData.surname)
            .addPathParameter(USERNAME, newUserData.name)
            .addPathParameter(PATRONYMIC, newUserData.patronymic)
            .addPathParameter(PHONE, newUserData.phone)
            .addBodyParameter("fam_shifr", newUserData.surnameEncode)
            .addBodyParameter("kf", newUserData.surnameKey)
            .addBodyParameter("sot_shifr", newUserData.phoneEncode)
            .addBodyParameter("kt", newUserData.phoneKey)
            .build()
            .getObjectObservable(SimpleString::class.java)
    }

    fun getScheduleDocForVideoCal(idDoc: String, date: String): Observable<VisitResponse2> {
        return Rx2AndroidNetworking.get(CenterEndPoint.SCHEDULE_DOC_FOR_VIDEO_CALL)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_DOC, idDoc)
            .addPathParameter(DATE, date)
            .build()
            .getObjectObservable(VisitResponse2::class.java)
    }

    val syncData: Observable<NewSyncResponse>
        get() = Rx2AndroidNetworking.get(CenterEndPoint.FIND_NEW_SYNC)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_DOC, prefManager.currentUserId.toString())
            .build()
            .getObjectObservable(NewSyncResponse::class.java)

    fun changeStatusSync(idSync: Int, status: String): Observable<SimpleBooleanList> {
        return Rx2AndroidNetworking.get(CenterEndPoint.CHANGE_STATUS_SYNC)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
            .addPathParameter(ID_SYNC, idSync.toString())
            .addPathParameter(STATUS, status)
            .build()
            .getObjectObservable(SimpleBooleanList::class.java)
    }

    fun findAllRaspSotr(date: String): Observable<AllRaspSotrResponse> { //  format 01.2023
        val idDoc = prefManager.currentUserId.toString()
        return Rx2AndroidNetworking.get(CenterEndPoint.GET_ALL_RASP_SOTR)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, idDoc)
            .addPathParameter(ID_DOC, idDoc)
            .addPathParameter(DATE, date)
            .build()
            .getObjectObservable(AllRaspSotrResponse::class.java)
    }

    fun loadStatMkb(dateFrom: String, dateTo: String): Observable<LoadStatMkbResponse> {
        val idDoc = prefManager.currentUserId.toString()
        return Rx2AndroidNetworking.get(CenterEndPoint.LOAD_START_MKB)
            .addHeaders("host", "oneclick.tmweb.ru")
            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
            .addHeaders(AUTH, prefManager.accessToken)
            .addHeaders(ID_DOC, idDoc)
            .addPathParameter(DATE_FROM, dateFrom)
            .addPathParameter(DATE_TO, dateTo)
            .addPathParameter(ID_DOC, idDoc)
            .build()
            .getObjectObservable(LoadStatMkbResponse::class.java)
    }

    fun loadFileZakl(data: LoadDataZaklAmbItem, dirPath: String, fileName: String, listener: Load2FileListener, item: ResultZakl2Item, fioKl: String): Completable {
        // print("")
        return Completable.fromAction {
            Rx2AndroidNetworking.post("http://188.225.25.133/medhelp_client/fpdf/report_ambkarti.php")
                .addHeaders("host", "oneclick.tmweb.ru")
                .setContentType("application/pdf")
                .addBodyParameter("data_priem", data.dataPriem)
                .addBodyParameter("diagnoz", data.diagnoz)
                .addBodyParameter("rekomend", data.rekomend)
                .addBodyParameter("sotr", data.sotr)
                .addBodyParameter("sotr_spec", data.sotrSpec)
                .addBodyParameter("cons", data.cons)
                .addBodyParameter("shapka", data.shapka)
                .addBodyParameter("nom_amb", data.nom_amb.toString())
                .addBodyParameter("OOO", data.ooo)
                .addBodyParameter("fiokl", fioKl)
                .build()
                .getAsOkHttpResponse(object : OkHttpResponseListener {
                    override fun onResponse(response: Response) {
                        listener.onResponse(response, dirPath, fileName, item)
                    }

                    override fun onError(anError: ANError) {
                        listener.onError(anError, item)
                    }
                })
        }
    }
    interface Load2FileListener {
        fun onResponse(response: Response, dirPath: String, fileName: String, item: ResultZakl2Item)
        fun onError(anError: ANError, item: ResultZakl2Item)
    }

    companion object {
        const val ID_CENTER = "id_center"
        const val ID_USER = "id_user"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val FB_TOKEN = "fb_token"
        const val MESSAGE = "message"
        const val STAR = "star"
        const val AUTH = "Authorization"
        const val DATE = "DATE"
        const val DATE_FROM = "DATE_FROM"
        const val DATE_TO = "DATE_TO"
        const val IP_ADRESS = "IP_ADRESS"
        const val ID_CALL = "ID_CALL"
        const val STATUS = "STATUS"
        const val ID_BRANCH = "ID_BRANCH"
        const val ID_ROOM = "ID_ROOM"
        const val TYPE = "TYPE"
        const val HASH = "hashkey"
        const val ID_DOC = "id_sotr"
        const val ID_FILIAL = "id_filial"
        const val ID_SERVICE = "id_service"
        const val ADM_DATE = "date"
        const val ADM_TIME = "adm"
        const val ID_KL = "id_kl"
        const val VERSION_CODE = "version_code"
        const val ID_DOCTOR = "id_doctor"
        const val PHONE = "PHONE"
        const val PHONE_ENCODE = "PHONE_ENCODE"
        const val PHONE_KEY = "PHONE_KEY"
        const val DB_NAME = "db_name"
        const val SURNAME = "SURNAME"
        const val SURNAME_ENCODE = "SURNAME_ENCODE"
        const val SURNAME_KEY = "SURNAME_KEY"
        const val PATRONYMIC = "PATRONYMIC"
        const val ID_BRANCH_NEW = "ID_BRANCH_NEW"
        const val TIME = "time"
        const val ID_SPEC = "id_spec"
        const val DURATION = "duration"
        const val ID_SYNC = "ID_SYNC"
    }

    //    fun updateFcmToken(fdToken: String): Observable<SimpleString> { // "null" при отмене токена
//        val tt = prefManager.centerInfo!!.db_name
//        val t2 = prefManager.accessToken
//        val idDoc = prefManager.currentUserId.toString()
//        return Rx2AndroidNetworking.get(CenterEndPoint.UPDATE_FCM_DOCTOR)
//            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
//            .addHeaders(AUTH, prefManager.accessToken)
//            .addHeaders(ID_DOC, idDoc)
//            .addPathParameter(ID_DOC, idDoc)
//            .addPathParameter(FB_TOKEN, fdToken)
//            .build()
//            .getObjectObservable(SimpleString::class.java)
//    }

    //    fun testConnectedToServer(path: String): TestConnectedToServer {
//        return try {
//            val myUrl = URL(path)
//            val connection = myUrl.openConnection()
//            connection.connectTimeout = 3000
//            connection.connect()
//            TestConnectedToServer(true, "")
//        } catch (e: Exception) {
//            TestConnectedToServer(false, e.message)
//        }
//    }


//    fun getAllRoom(idUser: Int): Observable<InfoAboutKlList> {
//        return Rx2AndroidNetworking.get(CenterEndPoint.ALL_ROOM)
//            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
//            .addHeaders(AUTH, prefManager.accessToken)
//            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
//            .addPathParameter(ID_USER, idUser.toString())
//            .build()
//            .getObjectObservable(InfoAboutKlList::class.java)
//    }



//    fun getAllExternalMsg(idUser: Int): Observable<MessageFromServerList> {
////        val auth = prefManager.accessToken
////        val idkl = prefManager.currentUserId.toString()
//        return Rx2AndroidNetworking.get(CenterEndPoint.EXTERNAL_MSG)
//            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
//            .addHeaders(AUTH, prefManager.accessToken)
//            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
//            .addPathParameter(ID_USER, idUser.toString())
//            .build()
//            .getObjectObservable(MessageFromServerList::class.java)
//    }

//    fun sendOurMsgToServer(idRoom: Long, msg: String, type: String): Observable<SimpleResponseBoolean> {
//        return Rx2AndroidNetworking.post(CenterEndPoint.SEND_MSG)
//            .addHeaders(DB_NAME, prefManager.centerInfo!!.db_name)
//            .addHeaders(AUTH, prefManager.accessToken)
//            .addHeaders(ID_DOC, prefManager.currentUserId.toString())
//            .addPathParameter(ID_ROOM, idRoom.toString())
//            .addBodyParameter(MESSAGE, msg)
//            .addPathParameter(TYPE, type)
//            .build()
//            .getObjectObservable(SimpleResponseBoolean::class.java)
//    }

//    fun sendImageToService(ip: String, json: JSONObject): Observable<Boolean> {
//        return Rx2AndroidNetworking.post(ip)
//            .addHeaders("Content-Type", "application/json")
//            .setContentType("application/json; charset=utf-8")
//            .addJSONObjectBody(json)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getObjectObservable(Boolean::class.java)
//    }

}