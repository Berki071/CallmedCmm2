package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

public class ClientBDResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("fio")
    private String fio;

    @SerializedName("sot")
    private String numberPhone;

    @SerializedName("statys")
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
