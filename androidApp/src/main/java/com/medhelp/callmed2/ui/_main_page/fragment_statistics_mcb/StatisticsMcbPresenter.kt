package com.medhelp.callmed2.ui._main_page.fragment_statistics_mcb

import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.LoadStatMkbResponse
import com.medhelp.callmed2.data.model.MkbItem
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class StatisticsMcbPresenter(val mainView: StatisticsMcbFragment) {
    var networkManager: NetworkManager

    init {
        val prefManager = PreferencesManager(mainView.requireContext())
        networkManager = NetworkManager(prefManager)
    }

    fun loadStatMkb(dateFrom: String, dateTo: String){
        mainView.showLoading()

        val cd = CompositeDisposable()
        cd.add(networkManager
            .loadStatMkb(dateFrom, dateTo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: LoadStatMkbResponse ->
                if (mainView == null) {
                    return@subscribe
                }

                if(response.response.size != 0 &&  (response.response.size > 1 || response.response[0] != null)) {
                    val newList = processingStatMcb(response.response)
                    mainView.initRecy(newList)
                }else{
                    mainView.initRecy(mutableListOf<MkbItem>())
                }

                mainView.hideLoading()
                cd.dispose()
            }) { throwable: Throwable? ->
                Timber.e(LoggingTree.getMessageForError(throwable, "StatisticsMcbPresenter/loadStatMkb"))
                if (mainView != null) {
                    mainView.initRecy(mutableListOf<MkbItem>())
                    mainView.hideLoading()
                    mainView.showError(mainView.resources.getString(R.string.api_default_error))
                }
                cd.dispose()
            })
    }

    private fun processingStatMcb(list: List<String>) : MutableList<MkbItem>{
        val newList = mutableListOf<MkbItem>()

        for(i in list){
            var tLatch = false

            for(j in newList){
                if(j.kodMkb == i){
                    tLatch = true
                    j.count++
                    break
                }
            }

            if(tLatch == false){
                newList.add(MkbItem(i))
            }
        }

        return newList
    }

}