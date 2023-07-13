package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CheckSpamRecordList {
    @SerializedName("imgError")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("response")
    private List<CheckSpamRecordResponse> mResponses = new ArrayList<>();

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CheckSpamRecordResponse> getmResponses() {
        return mResponses;
    }

    public void setmResponses(List<CheckSpamRecordResponse> mResponses) {
        this.mResponses = mResponses;
    }
}
