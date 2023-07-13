package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model;

import com.medhelp.callmed2.data.model.ScheduleItem;
import com.medhelp.callmed2.utils.main.MDate;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class DataRecyServiceRecord extends ExpandableGroup<ScheduleItem> implements Comparable {
    public DataRecyServiceRecord(String title, List<ScheduleItem> items) {
        super(title, items);
    }

    @Override
    public int compareTo(Object o) {
        long t1= MDate.stringToLong(getTitle(), MDate.DATE_FORMAT_ddMMyyyy);
        long t2= MDate.stringToLong(((DataRecyServiceRecord)o).getTitle(), MDate.DATE_FORMAT_ddMMyyyy);
        return (int)(t1-t2);
    }


}
