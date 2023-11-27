package com.medhelp.callmed2.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CenterResponse {

    @SerializedName("id") private int id;
    @SerializedName("id_center") private int idCenter;
    @SerializedName("id_filial") private int idFilial;
    @SerializedName("city") private String city;
    @SerializedName("time_zone") private int timeZone;
    @SerializedName("title") private String title;
    @SerializedName("info") private String info;
    @SerializedName("logo") private String logo;
    @SerializedName("site") private String site;
    @SerializedName("phone") private String phone;
    @SerializedName("address") private String address;
    @SerializedName("time_otkaz") private int timeForDenial;
    @SerializedName("time_podtvergd") private int timeForConfirm;
    @SerializedName("komment_zapis") private String komment_zapis;
    @SerializedName("max_zapis") private int max_zapis;
    @SerializedName("vers_kl") private String vers_kl;
    @SerializedName("vers_sotr") private String vers_sotr;
    @SerializedName("db_name") private String db_name;
    @SerializedName("ip_download") private String ip_download;

    public String getVers_kl() {
        return vers_kl;
    }
    public void setVers_kl(String vers_kl) {
        this.vers_kl = vers_kl;
    }

    public String getVers_sotr() {
        return vers_sotr;
    }
    public void setVers_sotr(String vers_sotr) {
        this.vers_sotr = vers_sotr;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getIdCenter() {
        return idCenter;
    }
    public void setIdCenter(int idCenter) {
        this.idCenter = idCenter;
    }

    public int getIdFilial() {
        return idFilial;
    }
    public void setIdFilial(int idFilial) {
        this.idFilial = idFilial;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public int getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public int getTimeForDenial() {
        return timeForDenial;
    }
    public void setTimeForDenial(int timeForDenial) {
        this.timeForDenial = timeForDenial;
    }

    public int getTimeForConfirm() {
        return timeForConfirm;
    }
    public void setTimeForConfirm(int timeForConfirm) {
        this.timeForConfirm = timeForConfirm;
    }

    public String getKomment_zapis() {
        return komment_zapis;
    }
    public void setKomment_zapis(String komment_zapis) {
        this.komment_zapis = komment_zapis;
    }

    public int getMax_zapis() {
        return max_zapis;
    }
    public void setMax_zapis(int max_zapis) {
        this.max_zapis = max_zapis;
    }

    public String getDb_name() {
        return db_name;
    }
    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }

    public String getIp_download() {
        return ip_download;
    }
    public void setIp_download(String ip_download) {
        this.ip_download = ip_download;
    }

    public CenterResponse() {
    }
}
