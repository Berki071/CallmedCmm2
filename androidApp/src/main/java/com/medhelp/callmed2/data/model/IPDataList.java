package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IPDataList {

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("response")
    private List<IPDataResponse> response;

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

    public List<IPDataResponse> getResponse() {
        return response;
    }

    public void setResponse(List<IPDataResponse> response) {
        this.response = response;
    }
}
