package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

public class IPDataResponse implements Comparable {
    @SerializedName("ip")
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    @Override
    public int compareTo(Object o) {
        return ip.compareTo(((IPDataResponse)o).getIp());
    }
}
