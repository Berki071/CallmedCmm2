package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

public class NewSyncItem {
    @SerializedName("id_sync")
    private Integer id_sync;
    @SerializedName("id_kl")
    private Integer id_kl;
    @SerializedName("id_centr")
    private Integer id_centr;

    public Integer getId_sync() {
        return id_sync;
    }

    public void setId_sync(Integer id_sync) {
        this.id_sync = id_sync;
    }

    public Integer getId_kl() {
        return id_kl;
    }

    public void setId_kl(Integer id_kl) {
        this.id_kl = id_kl;
    }

    public Integer getId_centr() {
        return id_centr;
    }

    public void setId_centr(Integer id_centr) {
        this.id_centr = id_centr;
    }
}
