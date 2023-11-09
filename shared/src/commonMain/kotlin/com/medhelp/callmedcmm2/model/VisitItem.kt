package com.medhelp.callmed2.data.model.timetable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VisitList {
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("response")
    private List<VisitResponse> response = new ArrayList<>();

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

    public List<VisitResponse> getResponse() {
        return response;
    }

    public void setResponse(List<VisitResponse> response) {
        this.response = response;
    }
}
