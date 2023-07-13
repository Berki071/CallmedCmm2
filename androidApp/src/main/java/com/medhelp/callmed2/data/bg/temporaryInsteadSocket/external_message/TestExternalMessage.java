package com.medhelp.callmed2.data.bg.temporaryInsteadSocket.external_message;//package com.medhelp.callmed2.data.bg.temporaryInsteadSocket.external_message;
//
//import android.content.Context;
//import com.medhelp.callmed2.data.model.CenterResponse;
//import com.medhelp.callmed2.data.network.NetworkManager;
//import com.medhelp.callmed2.data.pref.PreferencesManager;
//import com.medhelp.callmed2.utils.WorkTofFile.show_file.LoadFile;
//import com.medhelp.callmed2.utils.WorkTofFile.show_file.ShowFile2;
//import com.medhelp.callmed2.utils.main.MainUtils;
//import com.medhelp.callmed2.utils.timber_log.LoggingTree;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.schedulers.Schedulers;
//import timber.log.Timber;
//
//public class TestExternalMessage {
//    private Context context;
//    private ExternalMessageListener listener;
//
//    private PreferencesManager prefManager;
//    private NetworkManager networkManager;
//    private RealmManager realmManager;
//
//    String ip;
//
//    public TestExternalMessage(Context context, PreferencesManager prefManager, NetworkManager networkManager,RealmManager realmManager, ExternalMessageListener listener)
//    {
//        this.context=context;
//        this.prefManager=prefManager;
//        this.networkManager=networkManager;
//        this.realmManager=realmManager;
//        this.listener = listener;
//
//        CenterResponse centerResponse=prefManager.getCenterInfo();
//        String ippp= centerResponse.getLogo();
//
//        if(ippp==null  || ippp.length()==0)
//        {
//            Timber.e(LoggingTree.getMessageForError(null,"TestExternalMessage/ ippp в CenterInfo/getLogo == null или пустое"));
//            return;
//        }
//
//        ip = ippp.substring(0, ippp.indexOf("path=")+5);
//
//        if(ip==null  || ip.length()==0)
//        {
//            Timber.e(LoggingTree.getMessageForError(null,"TestExternalMessage/ ip в CenterInfo/getLogo == null или пустое ippp="+ippp));
//            return;
//        }
//    }
//
//    public void test()
//    {
////        allItem=0;
////        verifiedItem=0;
////
////        int idUser = prefManager.getCurrentUserId();
////
////        List<MessageFromServer> tmpList = new ArrayList<>();
////
////        CompositeDisposable cd = new CompositeDisposable();
////        cd.add(networkManager
////                .getAllExternalMsg(idUser)
////                .concatMapSingle(res -> {
////                    if (res.getRespons().size() <= 0  ||  res.getRespons().get(0).getMsg()==null) {
////                        listener.latchExternalMsgTrue();
////                        cd.dispose();
////                        return null;
////                    }
////
////                    for (MessageFromServer mfs : res.getRespons()) {
////                        tmpList.add(mfs);
////                    }
////
////                    return realmManager.testExistAllRoom(res.getRespons());
////                })
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(response -> {
////
////                    if (response) {
////                        overwriteMsgIfTypeImage(tmpList);
////                    } else {
////                        getAllRoom(tmpList);
////                    }
////
////
////                    cd.dispose();
////                }, throwable -> {
////
////                    if(!(prefManager.getCurrentPassword()==null  || prefManager.getCurrentPassword().equals(""))) {
////                        //Timber.e(throwable,"TestExternalMessage/test ");
////                        String msg=throwable.getMessage();
////                        if(msg==null  || (!msg.contains("Failed to connect to") && !msg.contains("connect timed out")&& !msg.contains("connection abort")))
////                            Timber.e(LoggingTree.getMessageForError(throwable,"TestExternalMessage$test "));
////                    }
////
////                    listener.latchExternalMsgTrue();
////                    listener.saveExternalMsgError(throwable.getMessage());
////
////                    cd.dispose();
////                })
////        );
//    }
//
//
//    private void getAllRoom( List<MessageFromServer> list)
//    {
////        int idUser=prefManager.getCurrentUserId();
////
////        CompositeDisposable cd = new CompositeDisposable();
////        cd.add(networkManager
////                .getAllRoom(idUser)
////                .concatMapCompletable(res->
////                        realmManager.saveInfoAboutDoc(res.getRespons()))
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(() -> {
////                    creteRoom(list);
////                    cd.dispose();
////                }, throwable ->
////                {
////                    Timber.e(LoggingTree.getMessageForError(throwable,"TestExternalMessage/getAllRoom "));
////
////                    listener.saveExternalMsgError(throwable.getMessage());
////                    listener.latchExternalMsgTrue();
////
////                    cd.dispose();
////                })
////        );
//    }
//
//    private void creteRoom(List<MessageFromServer> list) {
////        CompositeDisposable cd = new CompositeDisposable();
////        cd.add(realmManager
////                .testExistAllRoom(list)
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(response -> {
////
////                            if (response) {
////                                overwriteMsgIfTypeImage(list);
////                            } else {
////
////                                listener.saveExternalMsgError("комната не создана, нет информации о докторе");
////                                listener.latchExternalMsgTrue();
////                            }
////
////                            cd.dispose();
////                        }, throwable -> {
////
////                            Timber.e(LoggingTree.getMessageForError(throwable,"TestExternalMessage/creteRoom "));
////
////                            listener.saveExternalMsgError(throwable.getMessage());
////                            listener.latchExternalMsgTrue();
////
////                            cd.dispose();
////                        }
////                )
////        );
//    }
//
//    int allItem=0;
//    int verifiedItem=0;
//    private void overwriteMsgIfTypeImage(List<MessageFromServer> list)
//    {
//        if(ip==null)
//            return;
//
//        allItem=list.size();
//        for (MessageFromServer tmp : list)
//        {
//            if(tmp.getType().equals(MainUtils.IMAGE))
//            {
//                new LoadFile(context, ShowFile2.TYPE_IMAGE, tmp.getMsg(), ip + tmp.getMsg(), prefManager.getAccessToken(), null, new LoadFile.LoadFileListener() {
//                    @Override
//                    public void success(List<File> img) {
//                        tmp.setMsg(img.get(0).getAbsolutePath());
//
//                        testForContinuation(list);
//                    }
//
//                    @Override
//                    public void error(String err) {
//                        list.remove(tmp);
//                        allItem--;
//
//                        testForContinuation(list);
//                    }
//                });
//            }
//            else
//            {
//                testForContinuation(list);
//            }
//        }
//    }
//
//    private void testForContinuation(List<MessageFromServer> list)
//    {
//        verifiedItem++;
//
//        if(allItem==verifiedItem)
//        {
//            saveExternalMsgToDB(list);
//        }
//    }
//
//    private void saveExternalMsgToDB( List<MessageFromServer> list)
//    {
//        //overwriteMsgIfTypeImage(list);
//
////        CompositeDisposable cd=new CompositeDisposable();
////        cd.add(realmManager
////                .saveExternalMsg(list)
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(()->{
////                    listener.saveExternalMsgRefresh();
////                    listener.showNotificationMessage(list);
////                    listener.latchExternalMsgTrue();
////
////                    cd.dispose();
////                },throwable -> {
////
////                    Timber.e(LoggingTree.getMessageForError(throwable,"TestExternalMessage/saveExternalMsgToDB"));
////
////                    listener.saveExternalMsgError(throwable.getMessage());
////                    listener.latchExternalMsgTrue();
////
////                    cd.dispose();
////                })
////        );
//    }
//
//
//
//
//    public interface ExternalMessageListener{
//        void latchExternalMsgTrue();
//        void showNotificationMessage(List<MessageFromServer> list);
//        void saveExternalMsgError(String msg);
//        void saveExternalMsgRefresh();
//    }
//
//}
