package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model;

import com.medhelp.callmed2.data.model.ScheduleItem;
import com.medhelp.callmed2.data.model.ServiceItem;
import com.medhelp.callmed2.data.model.UserForRecordItem;

public class RecordData {
    ServiceItem serviceItem;
    ScheduleItem scheduleItem;
    String time;
    UserForRecordItem user;

    public ServiceItem getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public ScheduleItem getScheduleItem() {
        return scheduleItem;
    }

    public void setScheduleItem(ScheduleItem scheduleItem) {
        this.scheduleItem = scheduleItem;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public UserForRecordItem getUser() {
        return user;
    }

    public void setUser(UserForRecordItem user) {
        this.user = user;
    }
}
