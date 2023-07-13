package com.medhelp.callmed2.data.model;

public class TestConnectedToServer {
    Boolean isConnect;
    String msgErr;

    public TestConnectedToServer(Boolean isConnect, String msgErr) {
        this.isConnect = isConnect;
        this.msgErr = msgErr;
    }

    public Boolean getConnect() {
        return isConnect;
    }

    public void setConnect(Boolean connect) {
        isConnect = connect;
    }

    public String getMsgErr() {
        return msgErr;
    }

    public void setMsgErr(String msgErr) {
        this.msgErr = msgErr;
    }
}
