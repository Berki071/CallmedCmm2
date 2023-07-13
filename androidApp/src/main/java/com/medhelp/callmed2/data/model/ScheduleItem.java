package com.medhelp.callmed2.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.medhelp.callmed2.utils.main.MDate;

import java.util.ArrayList;
import java.util.List;

public class ScheduleItem implements Comparable, Parcelable {
    @SerializedName("id_doctor")
    private int idDoctor;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("is_work")
    private boolean isWork;

    @SerializedName("adm_day")
    private String admDay;

    @SerializedName("adm_time")
    private List<String> admTime = new ArrayList<>();

    @SerializedName("id_spec")
    private int id_spec;

    @SerializedName("foto")
    private String foto;

    FilialItem filialItem;
    long dateLong=0;

    public boolean isOpenListTime=false;


    protected ScheduleItem(Parcel in) {
        idDoctor = in.readInt();
        fullName = in.readString();
        isWork = in.readByte() != 0;
        admDay = in.readString();
        admTime = in.createStringArrayList();
        id_spec = in.readInt();
        foto = in.readString();
        dateLong = in.readLong();
        isOpenListTime = in.readByte() != 0;
    }

    public static final Creator<ScheduleItem> CREATOR = new Creator<ScheduleItem>() {
        @Override
        public ScheduleItem createFromParcel(Parcel in) {
            return new ScheduleItem(in);
        }

        @Override
        public ScheduleItem[] newArray(int size) {
            return new ScheduleItem[size];
        }
    };

    public int getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean work) {
        isWork = work;
    }

    public String getAdmDay() {
        return admDay;
    }

    public void setAdmDay(String admDay) {
        this.admDay = admDay;
    }

    public List<String> getAdmTime() {
        return admTime;
    }

    public void setAdmTime(List<String> admTime) {
        this.admTime = admTime;
    }

    public int getId_spec() {
        return id_spec;
    }

    public void setId_spec(int id_spec) {
        this.id_spec = id_spec;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public FilialItem getFilialItem() {
        return filialItem;
    }

    public void setFilialItem(FilialItem filialItem) {
        this.filialItem = filialItem;
    }

    public long getDateLong() {
        if(dateLong==0)
            dateToLongDate();
        return dateLong;
    }

    public void setDateLong(long dateLong) {
        this.dateLong = dateLong;
    }

    public boolean getOpenListTime() {
        return isOpenListTime;
    }

    public void setOpenListTime(boolean openListTime) {
        isOpenListTime = openListTime;
    }

    private void dateToLongDate()
    {
        setDateLong(MDate.stringToLong(getAdmDay(),MDate.DATE_FORMAT_ddMMyyyy));
    }

    @Override
    public int compareTo(Object o) {
        ScheduleItem tmp =(ScheduleItem)o;
        return (int)(getDateLong()-tmp.getDateLong());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idDoctor);
        dest.writeString(fullName);
        dest.writeByte((byte) (isWork ? 1 : 0));
        dest.writeString(admDay);
        dest.writeStringList(admTime);
        dest.writeInt(id_spec);
        dest.writeString(foto);
        dest.writeLong(dateLong);
        dest.writeByte((byte) (isOpenListTime ? 1 : 0));
    }
}
