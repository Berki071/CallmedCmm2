package com.android.internal.telephony;

public interface ITelephony {
    //package обязательно должен быть такой
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}