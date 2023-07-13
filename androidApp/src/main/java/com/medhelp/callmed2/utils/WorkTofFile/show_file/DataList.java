package com.medhelp.callmed2.utils.WorkTofFile.show_file;

import java.util.ArrayList;
import java.util.List;

public class DataList {
    private List<String> name=new ArrayList<>();
    private int status;
    private List<LoadFileListener> listeners=new ArrayList<>();
    private String error;

    public DataList(String name, int status) {
        this.name.add(name);
        this.status = status;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;

        if(this.status==ShowFile2.FILE_COMPLETE  || this.status==ShowFile2.FILE_ERROR)
        {
            notifyListeners();
        }
    }

    public void setListener(LoadFileListener listener)
    {
        listeners.add(listener);
    }

    private void notifyListeners()
    {
        if(listeners.size()==0)
            return;

        for(LoadFileListener list  :  listeners)
        {
            list.statusChangedToComplete();
        }
    }

}
