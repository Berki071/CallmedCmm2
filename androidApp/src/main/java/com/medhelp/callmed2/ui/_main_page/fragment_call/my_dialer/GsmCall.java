package com.medhelp.callmed2.ui._main_page.fragment_call.my_dialer;

public class GsmCall {

    Status status;
    String displayName;

    public GsmCall(Status status, String displayName)
    {
        this.status=status;
        this.displayName=displayName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public enum Status {
        CONNECTING,
        DIALING,
        RINGING,
        ACTIVE,
        DISCONNECTED,
        UNKNOWN;
    }
}