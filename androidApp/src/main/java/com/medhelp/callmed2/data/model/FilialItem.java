package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

public class FilialItem {
    @SerializedName("id_filial")
    String id;
    @SerializedName("naim_filial")
    String name;
    @SerializedName("address")
    String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
