package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class LoadStatMkbResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("response")
    //private List<LoadStatMkbItem> response = new ArrayList<>();
    private List<String> response = new ArrayList<>();

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

    public List<String> getResponse() {
        return response;
    }
    public void setResponse(List<String> response) {
        this.response = response;
    }
}
