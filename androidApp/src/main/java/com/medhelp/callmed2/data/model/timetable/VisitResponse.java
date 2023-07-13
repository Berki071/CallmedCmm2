package com.medhelp.callmed2.data.model.timetable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.medhelp.callmed2.utils.main.TimesUtils;

public class VisitResponse implements Comparable {
    @SerializedName("kab") private String  kab;
    @SerializedName("date") private String  date;
    @SerializedName("time") private String  time;
    @SerializedName("fam_kl") private String  fam_kl;
    @SerializedName("name_kl") private String  name_kl;
    @SerializedName("otch_kl") private String  otch_kl;
    @SerializedName("komment") private String  komment;
    @SerializedName("id_client") private int  id_client;
    @SerializedName("naim") private String  naim;
    @SerializedName("start") private String  start;
    @SerializedName("end") private String  end;

    public String getFullName()
    {
       return  fam_kl+" "+name_kl+" "+otch_kl;
    }

    public String getFam_kl() {
        return fam_kl;
    }
    public void setFam_kl(String fam_kl) {
        this.fam_kl = fam_kl;
    }

    public String getNaim() {
        return naim;
    }
    public void setNaim(String naim) {
        this.naim = naim;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getName_kl() {
        return name_kl;
    }
    public void setName_kl(String name_kl) {
        this.name_kl = name_kl;
    }

    public String getOtch_kl() {
        return otch_kl;
    }
    public void setOtch_kl(String otch_kl) {
        this.otch_kl = otch_kl;
    }

    public String getKomment() {
        return komment;
    }
    public void setKomment(String komment) {
        this.komment = komment;
    }

    public int getId_client() {
        return id_client;
    }
    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public String getKab() { return kab; }
    public void setKab(String kab) { this.kab = kab; }

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

    public VisitResponse() {
    }

    @Override
    public int compareTo(@NonNull Object o) {

        long dateCurrent= TimesUtils.stringToLong(date, TimesUtils.DATE_FORMAT_ddMMyyyy);
        long dateObj=TimesUtils.stringToLong(((VisitResponse)o).getDate(), TimesUtils.DATE_FORMAT_ddMMyyyy);

        if(dateCurrent>dateObj)
            return 1;
        else if(dateCurrent<dateObj)
            return -1;
        else
        {
            long timeCurrent=TimesUtils.stringToLong(time, TimesUtils.DATE_FORMAT_HHmm);
            long timeObj=TimesUtils.stringToLong(((VisitResponse)o).getTime(), TimesUtils.DATE_FORMAT_HHmm);

            return Long.compare(timeCurrent, timeObj);
        }
    }

}
