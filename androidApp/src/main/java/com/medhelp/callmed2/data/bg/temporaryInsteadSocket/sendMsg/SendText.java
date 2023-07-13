package com.medhelp.callmed2.data.bg.temporaryInsteadSocket.sendMsg;//package com.medhelp.callmed2.data.bg.temporaryInsteadSocket.sendMsg;
//
//
//import com.medhelp.callmed2.data.network.NetworkManager;
//
//public class SendText {
//   // private DataHelper dataHelper;
//    private MessageFromServer msg;
//    private SendMessageInterface listener;
//
//    NetworkManager networkManager;
//    private RealmManager realmManager;
//
//    public SendText(NetworkManager networkManager, RealmManager realmManager, MessageFromServer msg, SendMessageInterface listener) {
//        this.networkManager=networkManager;
//        this.realmManager=realmManager;
//        this.msg=msg;
//        this.listener=listener;
//
//
//        sendToServerOurMsg();
//    }
//
//    private void sendToServerOurMsg()
//    {
////        CompositeDisposable cd=new CompositeDisposable();
////        cd.add(networkManager
////                .sendOurMsgToServer(msg.getIdRoom(),msg.getMsg(), msg.getType())
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
////                    Timber.e(LoggingTree.getMessageForError(throwable,"SendText/sendToServerOurMsg"));
////                    if(listener!=null)
////                        listener.messageProcessingError(throwable , msg);
////                    cd.dispose();
////                })
////        );
//    }
//
//}
