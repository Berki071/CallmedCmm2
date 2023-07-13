package com.medhelp.callmed2.data.model.timetable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TimetableDocNotificationList {
    @SerializedName("imgError")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("response")
    private List<TimetableDocNotificationResponse> response = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<TimetableDocNotificationResponse> getResponse() {
        return response;
    }

    public void setResponse(List<TimetableDocNotificationResponse> response) {
        this.response = response;
    }
}
