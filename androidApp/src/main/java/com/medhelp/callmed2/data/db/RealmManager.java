//package com.medhelp.callmed2.data.db;//package com.medhelp.callmed2.data.db;
//
//import android.content.Context;
//
//import com.medhelp.callmed2.data.model.chat.Message;
//import com.medhelp.callmed2.data.model.chat.MessageFromServer;
//import com.medhelp.callmed2.utils.main.TimesUtils;
//
//import io.reactivex.Completable;
//import io.realm.Realm;
//import io.realm.RealmConfiguration;
//import io.realm.RealmList;
//
//public class RealmManager {
//
//    private Context context;
//    private RealmConfiguration config = new RealmConfig().getConfig();
//
//    public static final String ERROR_SEVERAL_ROOM="ERROR several rooms with one id";
//
//    private ConvertBase64 convertBase64;
//
//    public RealmManager(Context context) {
//        this.context = context;
//        convertBase64=new ConvertBase64();
//    }
//
////    public Completable saveInfoAboutDoc(List<InfoAboutKL> list) {
////        return Completable.fromAction(() ->
////                {
////                    Realm.init(context);
////                    Realm realm= Realm.getInstance(config);
////                    realm.beginTransaction();
////
////                    for(int i=0;i<list.size();i++)
////                    {
////                        list.get(i).migrationData();
////                        realm.insertOrUpdate(list.get(i));
////                    }
////
////                    realm.commitTransaction();
////
////                 /*   RealmResults<InfoAboutKL> resInf=realm.where(InfoAboutKL.class).findAll();
////                    List<InfoAboutKL> list2=realm.copyFromRealm(resInf);*/
////
////                    realm.close();
////                    Realm.compactRealm(config);
////                }
////        );
////    }
//
////    public Single<InfoAboutKL> getInfoAboutOneDoc(long idRoom)
////    {
////        Realm.init(context);
////        Realm realm=Realm.getInstance(config);
////
////        InfoAboutKL responseInfoAboutDoc = realm.where(InfoAboutKL.class).equalTo("idRoom",idRoom).findFirst();
////        responseInfoAboutDoc=realm.copyFromRealm(responseInfoAboutDoc);
////
////        realm.close();
////
////        return Single.just(responseInfoAboutDoc);
////    }
//
////    public Single<List<Room>> getAllActiveRooms() {
////
////        Realm.init(context);
////        Realm realm=Realm.getInstance(config);
////
////        RealmResults<Room>respnseRoom=realm
////                .where(Room.class)
////                .findAll();
////
////        List<Room> listRoom=realm.copyFromRealm(respnseRoom);
////
////        List<Room> newList=new ArrayList<>();
////
////        for(int i=0;i<listRoom.size();i++)
////        {
////            if(listRoom.get(i).getListMsg().size()>0)
////            {
////                newList.add(listRoom.get(i));
////            }
////        }
////
////        realm.close();
////
////        return Single.just(newList);
////    }
//
////    public Single<List<Message>> getAllMessageRoom(long idRoom) {
////        List<Message> tmp=getAllMessageRoomList(idRoom);
////
////        if(tmp!=null)
////            return Single.just(tmp);
////        else
////            return Single.error(new Throwable(ERROR_SEVERAL_ROOM));
////    }
//
////    public List<Message> getAllMessageRoomList(long idRoom)  {
////        Realm.init(context);
////        Realm realm=Realm.getInstance(config);
////
////        RealmResults<Room> responseR =realm.where(Room.class).equalTo("IdRoom",idRoom).findAll();
////
////        if(responseR.size()>1)
////        {
////            realm.close();
////            return null;
////        }
////
////        if(responseR.size()==0) {
////            InfoAboutKL responseI=realm.where(InfoAboutKL.class).equalTo("idRoom", idRoom).findFirst();
////
////            assert responseI!=null;
////
////            Room roo=new Room();
////            roo.setRoom(idRoom);
////            roo.setInfoAboutDoc(responseI);
////            roo.setListMsg(new RealmList<>());
////
////            realm.beginTransaction();
////            realm.copyToRealm(roo);
////            realm.commitTransaction();
////
////            realm.close();
////            return new RealmList<Message>();
////        }
////
////        Room r=realm.copyFromRealm(responseR.get(0));
////
////
////        clearingDates(r.getListMsg());
////        Collections.sort(r.getListMsg());
////        addDateItem(r.getListMsg(),realm);
////
////        realm.close();
////
////        return r.getListMsg();
////    }
//
////    public Single<ResponseFromSaveOurMessage> saveOurMsg(long idRoom, String msg , int type) {
////
////        Realm.init(context);
////        Realm realm=Realm.getInstance(config);
////        realm.beginTransaction();
////
////        Room r= realm.where(Room.class).equalTo("IdRoom", idRoom).findFirst();
////
////        assert r !=null;
////
////        Number n=realm.where(Message.class).max("id");
////        long id=n==null?1:n.longValue()+1;
////
////        Message message=new Message();
////        message.setId(id);
////        message.setMsg(msg);
////        message.setMyMsg(true);
////        message.setRead(false);
////        message.setTimeUtc(getCurrentUtcDate());
////        message.setType(type);
////
////        realm.insert(message);
////        r.getListMsg().add(message);
////
////        realm.commitTransaction();
////        realm.close();
////        Realm.compactRealm(config);
////
////        return Single.just(new ResponseFromSaveOurMessage(message.getId(),getAllMessageRoomList(idRoom)));
////    }
//
////    public Completable saveExternalMsg(List<MessageFromServer> list) {
////        return Completable.fromAction(()->{
////            Realm.init(context);
////            Realm realm=Realm.getInstance(config);
////            realm.beginTransaction();
////
////            for(MessageFromServer m : list)
////            {
////
////                if(m.getType().equals(MainUtils.IMAGE))
////                {
////                    m.setMsg(m.getMsg());
////                }
////
////                Number n=realm.where(Message.class).max("id");
////                long newId=n==null ? 1 : n.longValue()+1;
////
////                Message msg=new Message();
////                msg.setId(newId);
////                msg.setTimeUtc(getCurrentUtcDate());
////                msg.setType(MainUtils.convertTypeStringToInt(m.getType()));
////                msg.setMsg(m.getMsg());
////                msg.setMyMsg(false);
////
////                realm.insert(msg);
////
////                Room requesRoom=realm.where(Room.class).equalTo("IdRoom" , m.getIdRoom()) .findFirst();
////                requesRoom.getListMsg().add(msg);
////            }
////
////            realm.commitTransaction();
////            realm.close();
////            Realm.compactRealm(config);
////        });
////    }
//
//
//    private long getCurrentUtcDate()
//    {
//        return TimesUtils.localLongToUtcLong(System.currentTimeMillis());
//    }
//
//    // разделяющие даты в списке сообщений
//    private void addDateItem(RealmList<Message> msg,Realm realm)
//    {
//        long korZnach = 0;
//
//        if(msg==null || msg.size()==0)
//            return;
//
//        String date=getDateForMsgTitle(msg.get(0));
//        insertMessageItemWithDate(msg,0,date,realm, korZnach);
//        korZnach++;
//
//        for(int i=1;i<msg.size()-1;i++)
//        {
//            long d1 = TimesUtils.clipHHmmss(msg.get(i).getTimeUtc());
//            long d2 = TimesUtils.clipHHmmss(msg.get(i+1).getTimeUtc());
//
//            if(d1!=d2)
//            {
//                date=getDateForMsgTitle(msg.get(i+1));
//                insertMessageItemWithDate(msg,i+1,date,realm, korZnach);
//                korZnach++;
//                i++;
//            }
//        }
//    }
//
//    private void insertMessageItemWithDate(RealmList<Message> msg, int pozition, String value, Realm realm,long korZnach)
//    {
//        Number n=realm.where(Message.class).max("id");
//        long id=n==null?1:n.longValue()+1+korZnach;
//
//        Message msgNew=new Message();
//        msgNew.setType(Message.DATE);
//        msgNew.setMsg(value);
//        msgNew.setId(id);
//        msg.add(pozition,msgNew);
//    }
//
//    private String getDateForMsgTitle(Message msg)
//    {
//        return TimesUtils.clip_before_ddMMyyyyy(msg.getTimeUtc());
//    }
//
//    private void clearingDates(RealmList<Message> msg)
//    {
//        for(int i=0;i<msg.size();i++)
//        {
//            if(msg.get(i).getType()==Message.DATE)
//            {
//                msg.remove(i);
//                i--;
//            }
//        }
//    }
//
////    public Observable<List<MessageFromServer>> getAllNoReadMsg()
////    {
////        Realm.init(context);
////        Realm realm=Realm.getInstance(config);
////
////        RealmResults<Room> roomsResult=realm.where(Room.class).findAll();
////        List<MessageFromServer> dd =new ArrayList<>();
////
//// /*       if(roomsResult==null  ||  roomsResult.size()<=0)
////            return Observable.just(dd);*/
////
////        for(Room rRes : roomsResult)
////        {
////            Room r=realm.copyFromRealm(rRes);
////            for (int j=0;j<r.getListMsg().size();j++)
////            {
////                if( r.getListMsg().get(j).isMyMsg()  &&  !r.getListMsg().get(j).getRead())
////                {
////                    dd.add(new MessageFromServer(r.getRoom() , MainUtils.convertTypeIntToString(r.getListMsg().get(j).getType()) , r.getListMsg().get(j).getMsg()  , r.getListMsg().get(j).getId()));
////                }
////            }
////        }
////        realm.close();
////
////        return Observable.just(dd);
////    }
//
////    public Single<Boolean> testExistAllRoom(List<MessageFromServer> list) {
////        Realm.init(context);
////        Realm realm=Realm.getInstance(config);
////
////        for(MessageFromServer msg  :  list)
////        {
////            InfoAboutKL iad=realm.where(InfoAboutKL.class).equalTo("idRoom",msg.getIdRoom()).findFirst();
////            if(iad==null)
////                return Single.just(false);
////
////            Room r=realm.where(Room.class).equalTo("IdRoom",msg.getIdRoom()).findFirst();
////            if(r==null)
////            {
////                InfoAboutKL tmp2=realm.copyFromRealm(iad);
////                List<InfoAboutKL> tmp= new ArrayList<>();
////                tmp.add(tmp2);
////
////                Single<List<Message>> slm= getAllMessageRoom(iad.getIdRoom());
////                slm.subscribe();
////            }
////            r=realm.where(Room.class).equalTo("IdRoom",msg.getIdRoom()).findFirst();
////     //       Log.wtf("mLog","");
////        }
////
////
////        realm.close();
////        return Single.just(true);
////    }
//
//    public Completable updateOurMsgIsRead(MessageFromServer msgO) {
//        return Completable.fromAction(()->{
//            Realm.init(context);
//            Realm realm=Realm.getInstance(config);
//            realm.beginTransaction();
//
//            Message msgRes = realm.where(Message.class).equalTo("id", msgO.getId()).findFirst();
//            Message msg = realm.copyFromRealm(msgRes);
//            msg.setRead(true);
//
//            realm.insertOrUpdate(msg);
//
//            realm.commitTransaction();
//            realm.close();
//        });
//    }
//
//}