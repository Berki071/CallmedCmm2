package com.medhelp.callmed2.data.bg.temporaryInsteadSocket.sendMsg;//package com.medhelp.callmed2.data.bg.temporaryInsteadSocket.sendMsg;
//
//import com.medhelp.callmed2.data.db.RealmManager;
//import com.medhelp.callmed2.data.model.CenterResponse;
//import com.medhelp.callmed2.data.model.chat.MessageFromServer;
//import com.medhelp.callmed2.data.network.NetworkManager;
//import com.medhelp.callmed2.data.pref.PreferencesManager;
//import com.medhelp.callmed2.utils.timber_log.LoggingTree;
//import org.json.JSONException;
//import org.json.JSONObject;
//import java.io.File;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.schedulers.Schedulers;
//import timber.log.Timber;
//
//public class SendImageByService {
//
//    private MessageFromServer msg;
//    private CenterResponse centerResponse;
//    private ConvertBase64 convertBase64;
//    private SendMessageInterface listener;
//
//    private NetworkManager networkManager;
//    private PreferencesManager prefManager;
//    private RealmManager realmManager;
//
//    public SendImageByService(NetworkManager networkManager, PreferencesManager prefManager,RealmManager realmManager, MessageFromServer msg, SendMessageInterface listener) {
//        this.networkManager=networkManager;
//        this.prefManager=prefManager;
//        this.realmManager=realmManager;
//        this.msg=msg;
//        this.listener=listener;
//
//        convertBase64=new ConvertBase64();
//        centerResponse=prefManager.getCenterInfo();
//
//        convertImgToBase64();
//    }
//
//    private void convertImgToBase64() {
//        CompositeDisposable cd = new CompositeDisposable();
//        cd.add(convertBase64.fileToBase64(new File(msg.getMsg()))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(respons -> {
//                            send(createGson(respons));
//                            cd.dispose();
//                        }, throwable -> {
//                            Timber.e(LoggingTree.getMessageForError(throwable,"SendImageByService/convertImgToBase64 "));
//                            listener.messageProcessingError(throwable,msg);
//                            cd.dispose();
//                        }
//                )
//        );
//    }
//
//    private JSONObject createGson(String base64)
//    {
//        JSONObject jsonObject= new JSONObject();
//        try {
//            jsonObject.accumulate("FileName", clipNameWithoutExtension());
//            jsonObject.accumulate("FileFormat", clipToExtension());
//            jsonObject.accumulate("Base64Data", base64);
//
//            return jsonObject;
//        } catch (JSONException e) {
//            Timber.e(LoggingTree.getMessageForError(e,"SendImageByService/createGson "));
//            listener.messageProcessingError(e,msg);
//            return null;
//        }
//    }
//
//    private String clipNameWithoutExtension()
//    {
//        String path=msg.getMsg();
//        path=path.substring(path.lastIndexOf("/")+1 ,path.lastIndexOf("."));
//        return path;
//    }
//
//    private String clipToName()
//    {
//        String path=msg.getMsg();
//        path=path.substring(path.lastIndexOf("/")+1 ,path.length());
//        return path;
//    }
//
//    private String clipToExtension()
//    {
//        String path=msg.getMsg();
//        path= path.substring(path.lastIndexOf(".")+1 ,path.length());
//        return path;
//    }
//
//
//    private void send(JSONObject json) {
//        if(json==null)
//            return;
//
//        String ip = centerResponse.getLogo();
//        ip = ip.substring(0, ip.indexOf("/load?")+1) + "upload";
//
//        CompositeDisposable cd = new CompositeDisposable();
//        cd.add(networkManager
//                .sendImageToService(ip, json)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(response -> {
//                            if (response) {
//                                //sendToServerOurMsg();
//                            }
//                            else
//                            {
//                                listener.messageProcessingError(new Throwable("вовремя передачи изображения произошла ошибка ") , msg);
//                            }
//                            cd.dispose();
//                        }
//                        , throwable -> {
//                            Timber.e(LoggingTree.getMessageForError(throwable,"SendImageByService/send "));
//                            listener.messageProcessingError(throwable,msg);
//                            cd.dispose();
//                        }
//                )
//        );
//    }
//
////    private void sendToServerOurMsg()
////    {
////        CompositeDisposable cd=new CompositeDisposable();
////        cd.add(networkManager
////                .sendOurMsgToServer(msg.getIdRoom(),clipToName(), msg.getType())
////                .concatMapCompletable(resp->
////                {
////                    if(resp.getResponse())
////                    {
////                        return realmManager.updateOurMsgIsRead(msg);
////                    }
////                    else
////                        return null;
////                })
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(()->{
////                    if(listener!=null)
////                        listener.messageProcessingSuccessfulUpdate(msg);
////
////                    cd.dispose();
////                },throwable->{
////                    Timber.e(LoggingTree.getMessageForError(throwable,"SendImageByService/sendToServerOurMsg "));
////                    if(listener!=null)
////                        listener.messageProcessingError(throwable , msg);
////                    cd.dispose();
////                })
////        );
////    }
//
//}
