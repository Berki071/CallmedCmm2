package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.files


import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop.DataForRecyFiles
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import java.io.File

class ShowFilesMediaTmPresenter(val mainView: ShowFilesMediaTmFragment) {
    var prefManager: PreferencesManager = PreferencesManager(mainView.requireContext())

    var listFile: MutableList<File> = mutableListOf()
    var listImageWithDate: MutableList<DataForRecyFiles> = mutableListOf()

    fun getData() {
        listFile = mutableListOf()

        val prefix = T3RoomActivity.PREFIX_NAME_FILE
        val idCenter = prefManager.centerInfo!!.idCenter.toString()
        val idRoom = mainView.recItem.idRoom.toString()
        val searchStr = prefix+"_"+idCenter+"_"+idRoom

        val pathToCacheFolder: File = mainView.requireActivity().cacheDir
        val pathToFolderScanner = File(pathToCacheFolder, T3RoomActivity.FOLDER_TELEMEDICINE)

        if (!pathToFolderScanner.exists()) {
            mainView.initRecy(mutableListOf())
            return
        }

        val allFiles = pathToFolderScanner.listFiles()

        for(i in allFiles){
            if(i.name.contains(searchStr)){
                if(i.extension == "pdf"){
                    listFile.add(i)
                }
            }
        }

        listImageWithDate = processingData(listFile)
        mainView.initRecy(listImageWithDate)
    }

    fun processingData(listImage: MutableList<File>) : MutableList<DataForRecyFiles>{
        if(listImage.size == 0)
            return mutableListOf()

        var newListTmp = mutableListOf<DataForRecyFiles>()

        for(i in listImage){
            val tmpD= DataForRecyFiles(i)
            newListTmp.add(tmpD)
        }

        newListTmp.sortByDescending {it.dateL}

        var newListWithDate = mutableListOf<DataForRecyFiles>()
        newListWithDate.add(DataForRecyFiles(newListTmp[0].dateStr))
        for(i in newListTmp){
            if(newListWithDate.last().dateStr != i.dateStr ){
                newListWithDate.add(DataForRecyFiles(i.dateStr))
            }

            newListWithDate.add(i)
        }

        return newListWithDate
    }
}