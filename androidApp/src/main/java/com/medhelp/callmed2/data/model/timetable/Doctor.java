package com.medhelp.callmed2.data.model.timetable;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;


public class Doctor {
    @SerializedName("id_doctor")
    private int idDoctor;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("id_spec")   //было инт
    private String idSpec;

    @SerializedName("stag")
    private String experience;

    @SerializedName("specialty")
    private String specialty;

    @SerializedName("dop_info")
    private String dop_info;

    @SerializedName("image_url")
    private String photo;


    public int getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdSpec() {
        return idSpec;
    }

    public void setIdSpec(String idSpec) {
        this.idSpec = idSpec;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getDop_info() {
        return dop_info;
    }

    public void setDop_info(String dop_info) {
        this.dop_info = dop_info;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

}
