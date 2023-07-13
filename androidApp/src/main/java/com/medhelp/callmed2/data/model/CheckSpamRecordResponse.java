package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

public class CheckSpamRecordResponse {
    @SerializedName("count1")
    private int count1;

    @SerializedName("count2")
    private int count2;

    public int getCount1() {
        return count1;
    }

    public void setCount1(int count1) {
        this.count1 = count1;
    }

    public int getCount2() {
        return count2;
    }

    public void setCount2(int count2) {
        this.count2 = count2;
    }
}
