package com.medhelp.callmed2.ui._main_page.fragment_call.receiver_call;

public interface CallReceiverListener {
    void ringing(String number);  //Состояние вызова устройства: Звонит. Новый звонок поступил и звонит или ждет. В последнем случае другой звонок уже активен
    void idle();   //Device call state: No activity.
    void offHook(String number); //Состояние вызова устройства: снято. Существует по крайней мере один вызов, который набирает номер, активен или находится в режиме ожидания, и никакие вызовы не звонят или не ожидают
    void endCall();  // ответ на вызываемое из приложение действие окончания звонка
}
