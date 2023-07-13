package com.medhelp.callmed2.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TestAnaliseListData implements Parcelable {
    @SerializedName("data_start")
    private String dateOfPass;

    @SerializedName("data_end")
    private String dateResult;

    @SerializedName("analiz")
    private String title;

    @SerializedName("statys")
    private String status;


    protected TestAnaliseListData(Parcel in) {
        dateOfPass = in.readString();
        dateResult = in.readString();
        title = in.readString();
        status = in.readString();
    }

    public static final Creator<TestAnaliseListData> CREATOR = new Creator<TestAnaliseListData>() {
        @Override
        public TestAnaliseListData createFromParcel(Parcel in) {
            return new TestAnaliseListData(in);
        }

        @Override
        public TestAnaliseListData[] newArray(int size) {
            return new TestAnaliseListData[size];
        }
    };

    public String getDateOfPass() {
        return dateOfPass;
    }

    public void setDateOfPass(String dateOfPass) {
        this.dateOfPass = dateOfPass;
    }

    public String getDateResult() {
        return dateResult;
    }

    public void setDateResult(String dateResult) {
        this.dateResult = dateResult;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateOfPass);
        dest.writeString(dateResult);
        dest.writeString(title);
        dest.writeString(status);
    }
}
