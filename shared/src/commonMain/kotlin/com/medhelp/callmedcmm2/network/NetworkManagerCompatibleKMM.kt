package com.medhelp.callmedcmm2.network

import com.medhelp.callmedcmm2.model.DateResponse
import com.medhelp.callmedcmm2.model.SimpleResponseBoolean2
import com.medhelp.callmedcmm2.model.SimpleString2
import com.medhelp.callmedcmm2.model.VisitResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AnaliseResultResponse
import com.medhelp.callmedcmm2.model.chat.FCMResponse
import com.medhelp.callmedcmm2.model.chat.FileForMsgResponse
import com.medhelp.callmedcmm2.model.chat.HasPacChatsResponse
import com.medhelp.callmedcmm2.model.chat.LoadDataZaklAmbResponse
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse
import com.medhelp.callmedcmm2.model.chat.ResultZakl2Response
import com.medhelp.callmedcmm2.model.chat.ResultZakl2Response.ResultZakl2Item
import com.medhelp.callmedcmm2.model.chat.ResultZaklResponse
import com.medhelp.callmedcmm2.model.chat.SendMessageFromRoomResponse
import com.medhelp.callmedcmm2.model.pasport_recognize.IAMTokenFormOurServerResponse
import com.medhelp.callmedcmm2.model.pasport_recognize.RecognizeTextResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.serialization.kotlinx.json.*


class NetworkManagerCompatibleKMM {

    @Throws(Exception::class) suspend fun getAllRecordsTelemedicine (type: String,
                                                                     h_Auth: String, h_dbName: String, h_idDoc: String): AllRecordsTelemedicineResponse {
        //type= old или хоть что другое
        return httpClient.get( BASE_URL + "showTMbyDoc/"+h_idDoc+"/"+type)  {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }
        }
            .body()
    }

    @Throws(Exception::class) suspend fun getSelectRecordsTelemedicine (idRoom: String, idTm: String,
                                                                     h_Auth: String, h_dbName: String, h_idDoc: String): AllRecordsTelemedicineResponse {
        return httpClient.get( BASE_URL + "showTMbyIDtm/"+idRoom+"/"+idTm)  {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun getMessagesRoom (idRoom: String, idLastMsg: String,
                                 h_Auth: String, h_dbName: String, h_idDoc: String): MessageRoomResponse {
        return httpClient.get(BASE_URL + "LoadAllMessagesSOTR_v2/" + idRoom + "/" + idLastMsg) {
            timeout {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
                socketTimeoutMillis = 60000
            }

            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
                //append(ID_FILIAL, h_idFilial)
            }
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun getFileForMessageRoom (idMessage: String,
                                       h_Auth: String, h_dbName: String, h_idDoc: String): FileForMsgResponse{
        return httpClient.get(BASE_URL + "LoadAllMessagesSOTR_v2_fulldata/" + idMessage ) {
            timeout {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
                socketTimeoutMillis = 60000
            }

            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }
        }
            .body()
    }


    @Throws(Exception::class)
    suspend fun sendMessageFromRoom (idRoom: String, idTm: String,idUser: String, typeMsg: String, text: String,
                                     h_Auth: String, h_dbName: String, h_idDoc: String, h_idFilial: String): SendMessageFromRoomResponse {
        return httpClient.post(BASE_URL + "SendNewMessageToChatSOTR/" + idRoom + "/" + idTm+"/"+h_idDoc+"/"+h_idFilial+"/"+idUser+"/"+typeMsg) {
            timeout {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
                socketTimeoutMillis = 60000
            }

            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }

            val tmp = MultiPartFormDataContent(formData {
                append("message", text)
            })

            setBody(tmp)

        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun deleteMessageFromServer (idMsg: String,
                                         h_Auth: String, h_dbName: String, h_idKl: String): SimpleResponseBoolean2 {
        return httpClient.get(BASE_URL + "ChatDeleteMessageSOTR/" + idMsg) {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idKl)
            }
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun areThereAnyNewTelemedicineMsg (h_Auth: String, h_dbName: String, h_idDoc: String): HasPacChatsResponse {
        return httpClient.get(BASE_URL + "Has_sotr_chats/" + h_idDoc) {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun closeRecordTelemedicine (idRoom: String, idTm: String,
                                         h_Auth: String, h_dbName: String, h_idDoc: String): SimpleResponseBoolean2 {
        return httpClient.get(BASE_URL + "CloseTMDoc/" + idRoom+"/"+idTm) {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun toActiveRecordTelemedicine (idRoom: String, idTm: String,
                                         h_Auth: String, h_dbName: String, h_idDoc: String): SimpleResponseBoolean2 {
        return httpClient.get(BASE_URL + "ActiveTMbySotr/" + idRoom+"/"+idTm) {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun sendMsgFCM (json: String, serverKey: String, senderId: String): FCMResponse {
        return httpClient.post("https://fcm.googleapis.com/fcm/send") {
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "key=$serverKey")
                append("Sender", "id=$senderId")
            }

            setBody(json)
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun updateFcmToken (fdToken: String,
                                            h_Auth: String, h_dbName: String, h_idDoc: String): SimpleString2 { //"null" при отмене токена
        return httpClient.get(BASE_URL + "UpdateFCMdoctor/" + h_idDoc + "/" + fdToken) {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun updateTelemedicineReminderDocAboutRecord  (type: String, idTm: String,
                                h_Auth: String, h_dbName: String, h_idDoc: String): SimpleResponseBoolean2 { //"null" при отмене токена
        return httpClient.get(BASE_URL + "UpdateNotifDoctorForTMbyDoc/" + type + "/" + idTm) {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }
        }
            .body()
    }

    @Throws(Exception::class) suspend fun getResultAnalysis(h_Auth : String, h_dbName : String, h_idKl : String, h_idFilial : String) : AnaliseResultResponse {
        return httpClient.get(BASE_URL + "getResultAnaliz/" + h_idKl + "/" + h_idFilial) {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_KL, h_idKl)
                append(ID_FILIAL, h_idFilial)
            }
        }
            .body()
    }

    @Throws(Exception::class) suspend fun getResultZakl(h_Auth : String, h_dbName : String, h_idKl : String, h_idFilial : String) : ResultZaklResponse {
        return httpClient.get(BASE_URL + "getResultZakl/" + h_idKl + "/" + h_idFilial) {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_KL, h_idKl)
                append(ID_FILIAL, h_idFilial)
            }
        }
            .body()
    }
    @Throws(Exception::class) suspend fun getResultZakl2(h_Auth : String, h_dbName : String, h_idKl : String, h_idFilial : String) : ResultZakl2Response {
        return httpClient.get(BASE_URL + "GetZaklAmb/" + h_idKl + "/" + h_idFilial) {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_KL, h_idKl)
                append(ID_FILIAL, h_idFilial)
            }
        }
            .body()
    }

    @Throws(Exception::class) suspend fun geDataResultZakl2(item: ResultZakl2Item,
                                                            h_Auth : String, h_dbName : String, h_idKl : String, h_idFilial : String) : LoadDataZaklAmbResponse {
        return httpClient.get(BASE_URL + "LoadZaklAmb/" + item.idKl + "/" + item.idFilial + "/" + item.nameSpec + "/" + item.dataPriema) {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_KL, h_idKl)
                append(ID_FILIAL, h_idFilial)
            }
        }
            .body()
    }

//    @Throws(Exception::class)
//    suspend fun getIAMTokenYandex (oAuthToken: String): IAMTokenYandex {
//        return httpClient.post("https://iam.api.cloud.yandex.net/iam/v1/tokens?yandexPassportOauthToken=${oAuthToken}") {}
//            .body()
//    }
    @Throws(Exception::class) suspend fun getIAMTokenFromOurServer() : IAMTokenFormOurServerResponse {
        return httpClient.get("http://188.225.25.133/v1/recognize_token" ) {
            headers {
                append("host", "oneclick.tmweb.ru")
            }
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun recognizeTextInYandex (iamToken: String, xFolderId: String, jsonObj: String): RecognizeTextResponse { //RecognizeTextResponse
        return httpClient.post("https://ocr.api.cloud.yandex.net/ocr/v1/recognizeText") {
            contentType(ContentType.Application.Json)
            headers {
                append("Authorization", "Bearer ${iamToken}")
                append("x-folder-id", xFolderId)
                append("x-data-logging-enabled", "true")
                //append("x-client-request-id", "2b4f5411-8eac-41c3-aa5b-462f35321c71")
            }

            setBody(jsonObj)
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun sendPassportDataToServer (json: String, type: String,
                                     h_Auth: String, h_dbName: String, h_idDoc: String):  SimpleResponseBoolean2{
        return httpClient.post(BASE_URL + "/insert_data_recognize/${h_idDoc}/${type}") {

            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }

            val tmp = MultiPartFormDataContent(formData {
                append("recognize", json)
            })

            setBody(tmp)

        }
            .body()
    }



    @Throws(Exception::class)
    suspend fun currentDateApiCall(): DateResponse {
        return httpClient.get("http://188.225.25.133/medhelp_main/v1/" + "date") {
            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, API_KEY)
            }
        }
            .body()
    }
    @Throws(Exception::class)
    suspend fun getAllReceptionApiCall(branch: String, dateMonday: String,
                                       h_Auth: String, h_dbName: String, h_idDoc: String): VisitResponse {

        return httpClient.get(BASE_URL + "scheduleFull/doctor/${h_idDoc}/${dateMonday}/${branch}") {
            timeout {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 30000
                socketTimeoutMillis = 30000
            }

            headers {
                append("host", "oneclick.tmweb.ru")
                append(AUTH, h_Auth)
                append(DB_NAME, h_dbName)
                append(ID_SOTR, h_idDoc)
            }
        }
            .body()
    }

    @Throws(Exception::class)
    suspend fun uploadVideoFile(ipDownload: String, json : String): Boolean {
        return httpClient.post("http://"+ipDownload+":4080/api/file/upload" ) {
            contentType(ContentType.Application.Json)
            setBody(json)
        }
            .body()
    }
//    @Throws(Exception::class)
//    suspend fun loadVideoFile(url: String, dirPath: String, fileName: String){
//       // val outputFile = File(dirPath, fileName)
//
//        return httpClient.get(url){
//
//        }
//    }


    private val httpClient = HttpClient(){


        install(ContentNegotiation) {
            //json()
            json(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })

        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 60000
            socketTimeoutMillis = 60000
        }

        install(HttpRequestRetry) {
            maxRetries = 3

            retryOnExceptionIf { request, cause ->

                cause is RuntimeException
            }
            delayMillis { retry ->
                retry * 2000L
            } // retries in 3, 6, 9, etc. seconds
        }

    }

    companion object {
        const val ID_SOTR = "id_sotr"
        const val ID_CENTER = "id_center"
        const val ID_BRANCH = "ID_BRANCH"
        const val ID_BRANCH_NEW = "ID_BRANCH_NEW"
        const val ID_DOCTOR = "id_doctor"
        const val ID_SPEC = "id_spec"
        const val ID_SERVICE = "id_service"
        const val ID_USER = "id_user"
        const val ID_USER_NEW = "id_user_new"
        const val ID_FCM = "ID_FCM"
        const val USERNAME = "username"
        const val USERNAME2 = "USERNAME2"
        const val PASSWORD = "password"
        const val MESSAGE = "message"
        const val ADM_DATE = "date"
        const val ADM_TIME = "adm"
        const val AUTH = "Authorization"
        const val ID_ZAPISI = "id_zapisi"
        const val DATATODAY = "datatoday"
        const val DATE = "date"
        const val TIME = "time"
        const val DURATION = "duration"
        const val RATING = "RATING"
        const val ID_ROOM = "ID_ROOM"
        const val TYPE = "TYPE"
        const val ID_KL = "id_kl"
        const val ID_FILIAL = "id_filial"
        const val HASH = "hashkey"
        const val VERSION_CODE = "version_code"
        const val AMOUNT = "AMOUNT"
        const val QUANTITY = "quantity"
        const val DB_NAME = "db_name"
        const val FROM = "FROM"
        const val TO = "TO"
        const val INN = "INN"
        const val SPEC = "SPEC"

//        const val BASE_URL_LOCAL = "https://oneclick.tmweb.ru/medhelp_main/v1/"
//        const val BASE_URL_CENTER = "https://oneclick.tmweb.ru/medhelp_client/v1/"

        const val BASE_URL = "http://188.225.25.133/medhelp_client/v1/"
        const val API_KEY =
            "AAAA2UBtySo:APA91bGOxg0DNY9Ojz-BD0d4bUr-GukFBdvCtivWVjqZ8ppEHtl-BIwmINKD3R_"
    }
}