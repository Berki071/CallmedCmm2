package com.medhelp.callmed2.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class UserResponse/* implements Parcelable*/ {
    @SerializedName("id_doc_center")
    private int idUser;
    @SerializedName("id_center")
    private int idCenter;
    @SerializedName("username")
    private String username;

    @SerializedName("token")
    private String apiKey;
    @SerializedName("callcenter")
    private boolean showPartCallCenter;
    @SerializedName("chat")
    private boolean showPartMessenger;
    @SerializedName("rasp") //запись
    private boolean timetable;
    @SerializedName("vrach")  //расписание
    private boolean vrach;
    @SerializedName("sync")  //сканер док
    private boolean sync;

    public boolean isTimetable() {
        return timetable;
    }

    public void setTimetable(boolean timetable) {
        this.timetable = timetable;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdCenter() {
        return idCenter;
    }

    public void setIdCenter(int idCenter) {
        this.idCenter = idCenter;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isShowPartCallCenter() {
        return showPartCallCenter;
    }

    public void setShowPartCallCenter(boolean showPartCallCenter) {
        this.showPartCallCenter = showPartCallCenter;
    }

    public boolean isShowPartMessenger() {
        return showPartMessenger;
    }

    public void setShowPartMessenger(boolean showPartMessenger) {
        this.showPartMessenger = showPartMessenger;
    }

    public boolean isVrach() {
        return vrach;
    }
    public void setVrach(boolean vrach) {
        this.vrach = vrach;
    }

    public boolean isSync() {
        return sync;
    }
    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public UserResponse() {
    }


}