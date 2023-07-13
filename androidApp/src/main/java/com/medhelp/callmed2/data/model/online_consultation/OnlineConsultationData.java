package com.medhelp.callmed2.data.model.online_consultation;

import android.os.Parcel;
import android.os.Parcelable;

//public class OnlineConsultationData implements Parcelable {
////    public static final String SCENARIO_NON="non";
////    public static final String SCENARIO_VIDEO="video";
////    public static final String SCENARIO_INCOMING_WAIT="wait";
//
//    String executeTheScenario;
//    String patientId;
//    String patientName;
//    String docFotoUri;
//    String docName;
//    String docId;
//    String service;
//    String timeDateStart;
//    int duration;
//
//    public OnlineConsultationData(){}
//
//    private OnlineConsultationData(Parcel in) {
//        executeTheScenario = in.readString();
//        patientId = in.readString();
//        patientName = in.readString();
//        docFotoUri = in.readString();
//        docName = in.readString();
//        docId = in.readString();
//        service = in.readString();
//        timeDateStart = in.readString();
//        duration = in.readInt();
//    }
//
//    public static final Creator<OnlineConsultationData> CREATOR = new Creator<OnlineConsultationData>() {
//        @Override
//        public OnlineConsultationData createFromParcel(Parcel in) {
//            return new OnlineConsultationData(in);
//        }
//
//        @Override
//        public OnlineConsultationData[] newArray(int size) {
//            return new OnlineConsultationData[size];
//        }
//    };
//
//    public String getDocFotoUri() {
//        return docFotoUri;
//    }
//
//    public void setDocFotoUri(String docFotoUri) {
//        this.docFotoUri = docFotoUri;
//    }
//
//    public String getDocName() {
//        return docName;
//    }
//
//    public void setDocName(String docName) {
//        this.docName = docName;
//    }
//
//    public String getService() {
//        return service;
//    }
//
//    public void setService(String service) {
//        this.service = service;
//    }
//
//    public String getTimeDateStart() {
//        return timeDateStart;
//    }
//
//    public void setTimeDateStart(String timeDateStart) {
//        this.timeDateStart = timeDateStart;
//    }
//
//    public int getDuration() {
//        return duration;
//    }
//
//    public void setDuration(int duration) {
//        this.duration = duration;
//    }
//
//    public String getExecuteTheScenario() {
//        return executeTheScenario;
//    }
//
//    public void setExecuteTheScenario(String executeTheScenario) {
//        this.executeTheScenario = executeTheScenario;
//    }
//
//    public String getPatientId() {
//        return patientId;
//    }
//
//    public void setPatientId(String patientId) {
//        this.patientId = patientId;
//    }
//
//    public String getPatientName() {
//        return patientName;
//    }
//
//    public void setPatientName(String patientName) {
//        this.patientName = patientName;
//    }
//
//    public String getDocId() {
//        return docId;
//    }
//
//    public void setDocId(String docId) {
//        this.docId = docId;
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(executeTheScenario);
//        dest.writeString(patientId);
//        dest.writeString(patientName);
//        dest.writeString(docFotoUri);
//        dest.writeString(docName);
//        dest.writeString(docId);
//        dest.writeString(service);
//        dest.writeString(timeDateStart);
//        dest.writeInt(duration);
//    }
//}
