package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SpecialtyList {

    @SerializedName("imgError")
    private boolean error;

    @SerializedName("response")
    private List<CategoryResponse> spec = new ArrayList<>();

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

    public List<CategoryResponse> getSpec() {
        return spec;
    }

    public void setSpec(List<CategoryResponse> spec) {
        this.spec = spec;
    }
}