package com.medhelp.callmed2.data.model.timetable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AllRaspSotrResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("response")
    private List<AllRaspSotrItem> response = new ArrayList<>();

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

    public List<AllRaspSotrItem> getResponse() {
        return response;
    }
    public void setResponse(List<AllRaspSotrItem> response) {
        this.response = response;
    }
}
