package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserList {
    @SerializedName("error")
    private boolean error;

    @SerializedName("response")
    private List<UserResponse> response = new ArrayList<>();

    @SerializedName("message")
    private String message;

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

    public List<UserResponse> getResponse() {
        return response;
    }

    public void setResponse(List<UserResponse> response) {
        this.response = response;
    }
}
