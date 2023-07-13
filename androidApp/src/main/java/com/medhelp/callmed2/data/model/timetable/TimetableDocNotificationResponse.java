package com.medhelp.callmed2.data.model.timetable;

import com.google.gson.annotations.SerializedName;

public class TimetableDocNotificationResponse {
    @SerializedName("start")
    String startWorkDay;
    @SerializedName("end")
    String endWorkDay;
    @SerializedName("kab")
    String cabinet;
    @SerializedName("naimcentr")
    String nameBranch;
    @SerializedName("idcentr")
    int idBranch;
    @SerializedName("obed_start")
    String startLunch;
    @SerializedName("obed_end")
    String endLunch;


    public String getStartWorkDay() {
        return startWorkDay;
    }

    public void setStartWorkDay(String startWorkDay) {
        this.startWorkDay = startWorkDay;
    }

    public String getEndWorkDay() {
        return endWorkDay;
    }

    public void setEndWorkDay(String endWorkDay) {
        this.endWorkDay = endWorkDay;
    }

    public String getCabinet() {
        return cabinet;
    }

    public void setCabinet(String cabinet) {
        this.cabinet = cabinet;
    }

    public String getNameBranch() {
        return nameBranch;
    }

    public void setNameBranch(String nameBranch) {
        this.nameBranch = nameBranch;
    }

    public int getIdBranch() {
        return idBranch;
    }

    public void setIdBranch(int idBranch) {
        this.idBranch = idBranch;
    }

    public String getStartLunch() {
        return startLunch;
    }

    public void setStartLunch(String startLunch) {
        this.startLunch = startLunch;
    }

    public String getEndLunch() {
        return endLunch;
    }

    public void setEndLunch(String endLunch) {
        this.endLunch = endLunch;
    }
}
