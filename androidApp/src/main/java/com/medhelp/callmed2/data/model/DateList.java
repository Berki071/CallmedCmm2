package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class DateList {

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("response")
    private DateResponse response;

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

    public DateResponse getResponse() {
        return response;
    }

    public void setResponse(DateResponse response) {
        this.response = response;
    }
}
