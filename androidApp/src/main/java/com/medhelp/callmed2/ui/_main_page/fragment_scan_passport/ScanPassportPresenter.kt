package com.medhelp.callmed2.ui._main_page.fragment_scan_passport

import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.WorkTofFile.convert_Base64.ProcessingFileB64AndImg
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import com.medhelp.callmedcmm2.model.pasport_recognize.RecognizeTextResponse
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.IOException


class ScanPassportPresenter(val mainView: ScanPassportFragment) {
    val FOLDER_SCAN_PASS = "scan_pass"
    val preferencesManager = PreferencesManager(mainView.requireActivity())
    val networkManager = NetworkManagerCompatibleKMM()
    var convertBase64: ProcessingFileB64AndImg = ProcessingFileB64AndImg()

    var iamToken: String? = null

    fun requestToken() {
        Different.showLoadingDialog(mainView.requireContext())

        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.getIAMTokenFromOurServer()
            }
                .onSuccess { response ->
                    iamToken = response.response[0].recognize_token
                    Different.hideLoadingDialog()
                }
                .onFailure { error ->
                    Timber.tag("my").w(LoggingTree.getMessageForError(error, "ScanPassportPresenter/requestToken"))
                    Different.hideLoadingDialog()
                }
        }
    }

    fun deleteAllFilesInCash() {
        val pathToCacheFolder: File = mainView.requireActivity().getCacheDir()
        val pathToFolderScanner: File = File(pathToCacheFolder, FOLDER_SCAN_PASS)
        if (!pathToFolderScanner.exists()) {
            return
        }

        val listFiles = pathToFolderScanner.listFiles()
        if (listFiles.isNotEmpty()) {
            for (i in listFiles) {
                i.delete()
            }
        }
    }

    fun getNewUriForPhoto(): Uri? {
        val pathToCacheFolder: File = mainView.requireActivity().getCacheDir()
        val pathToFolderScanner: File = File(pathToCacheFolder, FOLDER_SCAN_PASS)
        if (!pathToFolderScanner.exists()) {
            pathToFolderScanner.mkdirs()
        }

        val nameNewFile = "/scan_passport_" + System.currentTimeMillis() + ".jpg"

        val newFile = File(pathToFolderScanner, nameNewFile)

        try {
            newFile.createNewFile()
            return FileProvider.getUriForFile(mainView.requireContext(), "com.medhelp.callmed2.fileprovider", newFile)

        } catch (e: IOException) {
            Timber.e(LoggingTree.getMessageForError(e, "ScannerDocPresenter/getNewUriForPhoto "))
        }

        return null
    }


    private fun getBase64ForUriAndPossiblyCrash(uri: Uri): String? {

        try {
            val bytes = mainView.requireContext().contentResolver.openInputStream(uri)?.readBytes()

            return Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (error: IOException) {
            error.printStackTrace() // This exception always occurs
        }

        return null
    }


    var counterPathsInSending = 0
    fun sendImagesToSRM(path1: String?, path2: String?){
        //https://cloud.yandex.ru/docs/vision/quickstart
        //val oAuthTokenYandex = "y0_AgAAAAAekSFJAATuwQAAAADvffhWG5FLWziiRNm0bOTw2QRA_CK6wYc"
        val xFolderId = "b1gnuegihk7aoa3c54kb"

        if(iamToken == null){
            Different.showAlertInfo(mainView.activity, "Ошибка", "Нет токена!")
            return
        }

        Different.showLoadingDialog(mainView.requireContext())
        if(path1 != null){
            counterPathsInSending++
            sendImageB64ToProcessingInYandex(iamToken!!, xFolderId, path1, TypeImgForRecognize.Passport)
        }
        if (path2 != null) {
            counterPathsInSending++
            sendImageB64ToProcessingInYandex(iamToken!!, xFolderId, path2, TypeImgForRecognize.Page)
        }
    }
    fun createJsomWithImgForRecognize(b64String: String, type: TypeImgForRecognize) : JSONObject {
        val lanC = JSONArray()
        lanC.put("ru")

        val noti = JSONObject()
        noti.put("mimeType", "JPEG")
        noti.put("languageCodes", lanC)

        if(type == TypeImgForRecognize.Passport)
            noti.put("model", "passport")
        else
            noti.put("model", "page")

        noti.put("content", b64String)

        return noti
    }
    fun sendImageB64ToProcessingInYandex(iamToken: String, xFolderId: String, path: String, type: TypeImgForRecognize){



        mainView.lifecycleScope.launch {
            val b64String: String? = getBase64ForUriAndPossiblyCrash(Uri.parse(path))
            val jsonObjectForSend = createJsomWithImgForRecognize(b64String!!, type)

            kotlin.runCatching {
                networkManager.recognizeTextInYandex(iamToken, xFolderId, jsonObjectForSend.toString())
            }
                .onSuccess {
                    if (type == TypeImgForRecognize.Passport) {
                        val json = createPassportDataJson(it)
                        if (!json.isEmpty()) {
                            sendPassportDataToServer(json,type)
                        } else {
                            counterPathsInSending--
                            if(counterPathsInSending<=0) {
                                Different.showAlertInfo(mainView.activity, "Ошибка", "Ошибка при обработке, попробуйте еще раз")
                                Different.hideLoadingDialog()
                            }
                        }
                    } else if (type == TypeImgForRecognize.Page) {
                        val tmpStr = createPageDataJson(it)
                        if (!tmpStr.isEmpty()) {
                            sendPassportDataToServer(tmpStr,type)
                            //Different.hideLoadingDialog()
                        } else {
                            counterPathsInSending--
                            if(counterPathsInSending<=0) {
                                Different.showAlertInfo(mainView.activity, "Ошибка", "Ошибка при обработке, попробуйте еще раз")
                                Different.hideLoadingDialog()
                            }
                        }
                    } else {
                        counterPathsInSending--
                        if(counterPathsInSending<=0) {
                            Different.showAlertInfo(mainView.activity, "Ошибка", "Ошибка при обработке, попробуйте еще раз")
                            Different.hideLoadingDialog()
                        }
                    }

                }.onFailure {
                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "ScanPassportPresenter/sendImageB64ToProcessingInYandex"))
                    Different.hideLoadingDialog()
                }
        }
    }
    fun createPassportDataJson(obj: RecognizeTextResponse) : String {
        if(obj.result == null || obj.result!!.textAnnotation == null ){
            return ""
        }

        var citizenship: String = ""
        var expiration_date: String = ""
        var gender: String = ""
        var issue_date: String = ""
        var issued_by: String = ""
        var subdivision: String = ""
        var surname: String = ""
        var name: String = ""
        var middle_name: String = ""
        var birth_date: String = ""
        var birth_place: String = ""
        var number: String = ""

        for(i in obj.result!!.textAnnotation!!.entities){
            when(i.name){
                "citizenship" -> citizenship = i.text!!
                "expiration_date" -> expiration_date = i.text!!
                "gender" -> gender = i.text!!
                "issue_date" -> issue_date = i.text!!
                "issued_by" -> issued_by = i.text!!
                "subdivision" -> subdivision = i.text!!
                "surname" -> surname = i.text!!
                "name" -> name = i.text!!
                "middle_name" -> middle_name = i.text!!
                "birth_date" -> birth_date = i.text!!
                "birth_place" -> birth_place = i.text!!
                "number" -> number = i.text!!
            }
        }

        val json = JSONObject()
        json.put("citizenship", citizenship)
        json.put("expiration_date", expiration_date)
        json.put("gender", gender)
        json.put("issue_date", issue_date)
        json.put("issued_by", issued_by)
        json.put("subdivision", subdivision)
        json.put("surname", surname)
        json.put("name", name)
        json.put("middle_name", middle_name)
        json.put("birth_date", birth_date)
        json.put("birth_place", birth_place)
        json.put("number", number)

        return json.toString()
    }
    fun createPageDataJson(obj: RecognizeTextResponse) : String {
        if(obj.result == null || obj.result!!.textAnnotation == null || obj.result!!.textAnnotation!!.blocks.isEmpty()){
            return ""
        }

        var listStr : MutableList<String> = mutableListOf()

        for(i in obj.result!!.textAnnotation!!.blocks){
            for(j in i.lines){
                j.text?.let {
                    listStr.add(it)
                }
            }
        }

        if(listStr.isEmpty())
            return ""

        var city = ""
        var cityIndex = -1
        var street = ""
        var streetIndex = -1
        var numHouse = ""
        var numHouseIndex = -1
        var issuedBy = ""

        for(i in 0..<listStr.size){
            if(city.isEmpty() && (listStr[i].lowercase().contains("пункт") || listStr[i].lowercase().contains("гор") || listStr[i].lowercase().contains("нункт") || listStr[i].lowercase().contains("кункт"))){
                val tmp = city.isEmpty()
                city = listStr[i]
                cityIndex = i
            }else if(street.isEmpty() && (listStr[i].lowercase().contains("улица") || listStr[i].lowercase().contains("ул."))){
                street = listStr[i]
                streetIndex = i
            }else if(numHouse.isEmpty() && (listStr[i].lowercase().contains("дом") || listStr[i].lowercase().contains("кв:") || listStr[i].lowercase().contains("корп:"))){
                numHouse = listStr[i]
                numHouseIndex = i
            }
        }

        if(streetIndex == -1 && cityIndex != -1){
            if(!listStr[cityIndex+1].lowercase().contains("р-н")){
                streetIndex = cityIndex+1
                street = listStr[cityIndex+1]
            }
        }
        if(numHouseIndex == -1 && streetIndex != -1){
            numHouseIndex = streetIndex+1
            numHouse = listStr[streetIndex+1]
        }

        if(numHouseIndex != -1) {
            for (i in numHouseIndex + 1..<listStr.size-1) {
                issuedBy += "${listStr[i]} "
            }
        }

        val tmpCity = city.indexOf("гор", ignoreCase = true)
        if(tmpCity != -1){
            city = city.substring(tmpCity + "гор".length + 2, city.length)
        }
        val tmpStreet = street.indexOf("Улица:", ignoreCase = true)
        if(tmpStreet != -1){
            street = street.substring(tmpStreet + "Улица:".length + 1 , street.length)
        }




        val json = JSONObject()
        json.put("place", city)
        json.put("street", street)
        json.put("allAddressNumbers", numHouse)
        if(numHouse.isNotEmpty()){
            var houseNumber = numHouse.substring(numHouse.indexOf("Дом :")+"Дом :".length +1, numHouse.indexOf("Корп:")-1).trim()
            var korpusNumber = numHouse.substring(numHouse.indexOf("Корп:")+"Корп:".length +1, numHouse.indexOf("Кв:")-1).trim()
            var apartmentNumber = numHouse.substring(numHouse.indexOf("Кв:")+"Кв:".length +1, numHouse.length).trim()

            json.put("houseNumber", houseNumber)
            json.put("korpusNumber", korpusNumber)
            json.put("apartmentNumber", apartmentNumber)
        }
        json.put("issuedBy", issuedBy)

        return /*listStr.toString() + "\n\n" + */json.toString()
    }
    fun sendPassportDataToServer(json: String, type: TypeImgForRecognize){

        val typeForRequest : String = if(type == TypeImgForRecognize.Passport) "passport1" else "passport2"

        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.sendPassportDataToServer(json, typeForRequest, preferencesManager.accessToken!!, preferencesManager.centerInfo!!.db_name!!, preferencesManager.currentUserId.toString())
            }
                .onSuccess {
                    counterPathsInSending--
                    if(counterPathsInSending<=0) {
                        mainView.clearImg()
                        Different.hideLoadingDialog()
                        Different.showAlertInfo(mainView.activity, "", "Данные отправлены успешно")
                    }
                }.onFailure {
                    counterPathsInSending--
                    if(counterPathsInSending<=0) {
                        Different.showAlertInfo(mainView.activity, "Ошибка", "Ошибка при отправки в МИС")
                        Timber.tag("my").w(LoggingTree.getMessageForError(it, "ScanPassportPresenter/sendPassportDataToServer"))
                        Different.hideLoadingDialog()
                    }
                }
        }
    }

    enum class TypeImgForRecognize{
        Passport, Page
    }

}