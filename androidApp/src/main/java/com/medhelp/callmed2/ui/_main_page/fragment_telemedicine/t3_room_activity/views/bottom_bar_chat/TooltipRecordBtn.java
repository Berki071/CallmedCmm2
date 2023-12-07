package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.bottom_bar_chat;

import android.content.Context;
import android.view.View;
import com.medhelp.callmed2.R;
import it.sephiroth.android.library.xtooltip.Tooltip;
import it.sephiroth.android.library.xtooltip.ClosePolicy;

public class TooltipRecordBtn {
    void showTooltip(Context context, String msg, View view){
        Tooltip builder = new Tooltip.Builder(context)
                .anchor(view, -150, -23, false)
                .closePolicy(
                        new ClosePolicy (100)
                )
                .activateDelay(0)
                .text(msg)
                .maxWidth(450)
                .showDuration(2000)
                .arrow(false)
                .styleId(R.style.ToolTipCustomStyleDark)
                .overlay(false) //мигающий круглешок
                .create();

        builder.show(view, Tooltip.Gravity.TOP, false);
    }
}
