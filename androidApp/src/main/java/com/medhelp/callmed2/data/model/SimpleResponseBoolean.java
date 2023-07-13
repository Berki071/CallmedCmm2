package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

public class SimpleResponseBoolean {
    @SerializedName("error")
    private boolean error;

    @SerializedName("Message")
    private String message;

    @SerializedName("response")
    private Boolean response ;


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
