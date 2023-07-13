package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TestAnalisListResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("response")
    private List<TestAnaliseListData> response = new ArrayList<>();

    @SerializedName("message")
    private String message;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<TestAnaliseListData> getResponse() {
        return response;
    }

    public void setResponse(List<TestAnaliseListData> response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
