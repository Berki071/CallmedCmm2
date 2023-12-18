package com.medhelp.callmed2.data.model.timetable;

import com.google.gson.annotations.SerializedName;

public class SimpleBooleanList {
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("response")
    private Boolean response;

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

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }
}
