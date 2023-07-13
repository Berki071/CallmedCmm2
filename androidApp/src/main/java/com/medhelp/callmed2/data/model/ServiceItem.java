package com.medhelp.callmed2.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ServiceItem implements Parcelable,Comparable {

    @SerializedName("id_service")
    private int id;

    @SerializedName("id_spec")
    private int idSpec;

    @SerializedName("admission")
    private int admission;

    @SerializedName("value")
    private String value;

    @SerializedName("title")
    private String title;

    @SerializedName("komment")
    private String hint;

    @SerializedName("poryadok")
    private int poryadok;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSpec() {
        return idSpec;
    }

    public void setIdSpec(int idSpec) {
        this.idSpec = idSpec;
    }

    public int getAdmission() {
        return admission;
    }

    public void setAdmission(int admission) {
        this.admission = admission;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getPoryadok() {
        return poryadok;
    }

    public void setPoryadok(int poryadok) {
        this.poryadok = poryadok;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.idSpec);
        dest.writeInt(this.admission);
        dest.writeString(this.value);
        dest.writeString(this.title);
        dest.writeString(this.hint);
        dest.writeInt(this.poryadok);
    }

    public ServiceItem() {
    }

    protected ServiceItem(Parcel in) {
        this.id = in.readInt();
        this.idSpec = in.readInt();
        this.admission = in.readInt();
        this.value = in.readString();
        this.title = in.readString();
        this.hint = in.readString();
        this.poryadok=in.readInt();
    }

    public static final Creator<ServiceItem> CREATOR = new Creator<ServiceItem>() {
        @Override
        public ServiceItem createFromParcel(Parcel source) {
            return new ServiceItem(source);
        }

        @Override
        public ServiceItem[] newArray(int size) {
            return new ServiceItem[size];
        }
    };

    @Override
    public int compareTo(@NonNull Object o) {
        return title.compareTo(((ServiceItem)o).getTitle());
    }
}