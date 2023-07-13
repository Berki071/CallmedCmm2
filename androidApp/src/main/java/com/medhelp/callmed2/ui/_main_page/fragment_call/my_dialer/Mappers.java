package com.medhelp.callmed2.ui._main_page.fragment_call.my_dialer;

import android.annotation.TargetApi;
import android.telecom.Call;

class MappersKt {
    @TargetApi(23)
    public static  GsmCall toGsmCall(Call receiver) {
        Call.Details dd= receiver.getDetails();

        String tmp =null;
        if(receiver.getDetails().getHandle()!=null){
            tmp=receiver.getDetails().getHandle().getSchemeSpecificPart();
        }

        if(tmp!=null)
            return new GsmCall(toGsmCallStatus(receiver.getState()),tmp);
        else
            return null;
    }

    private static final GsmCall.Status toGsmCallStatus(int receiver) {

        switch (receiver) {
            case Call.STATE_ACTIVE:
                return GsmCall.Status.ACTIVE;

            case Call.STATE_RINGING:
                return GsmCall.Status.RINGING;

            case Call.STATE_CONNECTING:
                return GsmCall.Status.CONNECTING;

            case Call.STATE_DIALING:
                return GsmCall.Status.DIALING;

            case Call.STATE_DISCONNECTED:
                return GsmCall.Status.DISCONNECTED;

            default:
                return GsmCall.Status.UNKNOWN;
        }
    }

}
