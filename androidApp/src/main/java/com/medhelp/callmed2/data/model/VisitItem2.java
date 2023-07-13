package com.medhelp.callmed2.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.medhelp.callmed2.data.model.timetable.VisitResponse;
import com.medhelp.callmed2.utils.Different;
import com.medhelp.callmed2.utils.main.TimesUtils;

public class VisitItem2 implements Comparable, Parcelable {
    @SerializedName("id_zapisi") private int id_zapisi;
    @SerializedName("date") private String  date;
    @SerializedName("time") private String  time;
    @SerializedName("id_client") private int userId;
    @SerializedName("fam_kl") private String userSurnameEncode;
    @SerializedName("kf") private String userSurnameKey;
    @SerializedName("name_kl") private String userName;
    @SerializedName("otch_kl") private String userPatronymic;
    @SerializedName("komment") private String comments;
    @SerializedName("naim") private String serviceName;
    @SerializedName("naim_filial") private String branchName;
    @SerializedName("fio_sotr") private String docAllName;
    @SerializedName("dlit") private int durationService;

    String idDoc;
    long timeMils =0;
    String executeTheScenario;
    String userSurname;
    private int durationSec;

    protected VisitItem2(Parcel in) {
        id_zapisi = in.readInt();
        date = in.readString();
        time = in.readString();
        userId = in.readInt();
        userSurnameEncode = in.readString();
        userSurnameKey = in.readString();
        userName = in.readString();
        userPatronymic = in.readString();
        comments = in.readString();
        serviceName = in.readString();
        branchName = in.readString();
        docAllName = in.readString();
        durationService = in.readInt();
        idDoc = in.readString();
        timeMils = in.readLong();
        executeTheScenario = in.readString();
        userSurname = in.readString();
        durationSec = in.readInt();
    }

    public VisitItem2() {
        durationSec=-1;
    }

    public static final Creator<VisitItem2> CREATOR = new Creator<VisitItem2>() {
        @Override
        public VisitItem2 createFromParcel(Parcel in) {
            return new VisitItem2(in);
        }

        @Override
        public VisitItem2[] newArray(int size) {
            return new VisitItem2[size];
        }
    };

    public String getFullNameUser()
    {
        return  getUserSurname() +" "+ userName +" "+ userPatronymic;
    }

    public String getUserSurnameEncode() {
        return userSurnameEncode;
    }
    public void setUserSurnameEncode(String userSurnameEncode) {
        this.userSurnameEncode = userSurnameEncode;
    }

    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPatronymic() {
        return userPatronymic;
    }
    public void setUserPatronymic(String userPatronymic) {
        this.userPatronymic = userPatronymic;
    }

    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserSurnameKey() {
        return userSurnameKey;
    }
    public void setUserSurnameKey(String userSurnameKey) {
        this.userSurnameKey = userSurnameKey;
    }

    public String getBranchName() {
        return branchName;
    }
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getDocAllName() {
        return docAllName;
    }
    public void setDocAllName(String docAllName) {
        this.docAllName = docAllName;
    }

    public String getIdDoc() {
        return idDoc;
    }
    public void setIdDoc(String idDoc) {
        this.idDoc = idDoc;
    }

    public int getId_zapisi() {
        return id_zapisi;
    }
    public void setId_zapisi(int id_zapisi) {
        this.id_zapisi = id_zapisi;
    }

    public String getExecuteTheScenario() {
        return executeTheScenario;
    }
    public void setExecuteTheScenario(String executeTheScenario) {
        this.executeTheScenario = executeTheScenario;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }
    public String getUserSurname()
    {
        if(userSurname==null)
            userSurname= Different.encodeDecodeWord(userSurnameEncode,userSurnameKey);

        return userSurname;
    }

    public String getAllNameUser()
    {
        return getUserSurname()+" "+getUserName()+" "+getUserPatronymic();
    }

    public void setTimeMils(long timeMils) {
        this.timeMils = timeMils;
    }
    public long getTimeMills(){
        if(timeMils ==0)
            return TimesUtils.stringToLong((getTime()+" "+getDate()),TimesUtils.DATE_FORMAT_HHmm_ddMMyyyy);
        else
            return timeMils;
    }

    @Override
    public int compareTo(@NonNull Object o) {

        long dateCurrent= TimesUtils.stringToLong(date, TimesUtils.DATE_FORMAT_ddMMyyyy);
        long dateObj=TimesUtils.stringToLong(((VisitResponse)o).getDate(), TimesUtils.DATE_FORMAT_ddMMyyyy);

        if(dateCurrent>dateObj)
            return 1;
        else if(dateCurrent<dateObj)
            return -1;
        else
        {
            long timeCurrent=TimesUtils.stringToLong(time, TimesUtils.DATE_FORMAT_HHmm);
            long timeObj=TimesUtils.stringToLong(((VisitResponse)o).getTime(), TimesUtils.DATE_FORMAT_HHmm);

            return Long.compare(timeCurrent, timeObj);
        }
    }

    public int getDurationSec() {
        if(durationSec==-1)
            durationSec=durationService*60;
        return durationSec;
    }

    public void setDurationSec(int durationSec) {
        this.durationSec = durationSec;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_zapisi);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeInt(userId);
        dest.writeString(userSurnameEncode);
        dest.writeString(userSurnameKey);
        dest.writeString(userName);
        dest.writeString(userPatronymic);
        dest.writeString(comments);
        dest.writeString(serviceName);
        dest.writeString(branchName);
        dest.writeString(docAllName);
        dest.writeInt(durationService);
        dest.writeString(idDoc);
        dest.writeLong(timeMils);
        dest.writeString(executeTheScenario);
        dest.writeString(userSurname);
        dest.writeInt(durationSec);
    }

    public String getTimeStartAndEnd()
    {
        long timeEnd=getTimeMills();;
        timeEnd+=getDurationSec()*1000;

        String str="c "+ getTime();
        str+=" до "+TimesUtils.longToString(timeEnd,TimesUtils.DATE_FORMAT_HHmm);

        return str;
    }
}
