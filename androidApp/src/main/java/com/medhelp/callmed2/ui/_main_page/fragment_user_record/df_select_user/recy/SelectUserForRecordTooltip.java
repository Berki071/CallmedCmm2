package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_user.recy;

import android.view.View;

import com.medhelp.callmed2.R;

import it.sephiroth.android.library.xtooltip.ClosePolicy;
import it.sephiroth.android.library.xtooltip.Tooltip;

public class SelectUserForRecordTooltip {
    public static void showTooltipPlus(View view) {
        if (view == null)
            return;

        Tooltip builder = new Tooltip.Builder(view.getContext())
                .anchor(view, 0, 0, false)
                .closePolicy(
                        new ClosePolicy(100)
                )
                .activateDelay(0)
                .text("Нажмите для создания нового пациента")
                .maxWidth(620)
                .showDuration(20000)
                .arrow(true)
                .styleId(R.style.ToolTipLayoutCustomStyle)
                .overlay(true) //мигающий круглешок
                .create();
        builder.show(view, Tooltip.Gravity.LEFT, false);
    }
}
