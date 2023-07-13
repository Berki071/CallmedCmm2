package com.medhelp.callmed2.data.model.timetable;

import com.google.gson.annotations.SerializedName;

public class AllRaspSotrItem {
    @SerializedName("data")
    private String data;
    @SerializedName("start")
    private String start;
    @SerializedName("end")
    private String end;
    @SerializedName("obed_start")
    private String obed_start;
    @SerializedName("obed_end")
    private String obed_end;
    @SerializedName("naim_filial")
    private String naim_filial;

    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }

    public String getStart() {
        return start;
    }
    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }
    public void setEnd(String end) {
        this.end = end;
    }

    public String getObed_start() {
        return obed_start;
    }
    public void setObed_start(String obed_start) {
        this.obed_start = obed_start;
    }

    public String getObed_end() {
        return obed_end;
    }
    public void setObed_end(String obed_end) {
        this.obed_end = obed_end;
    }

    public String getNaim_filial() {
        return naim_filial;
    }
    public void setNaim_filial(String naim_filial) {
        this.naim_filial = naim_filial;
    }
}
