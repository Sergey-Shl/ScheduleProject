package com.serge.schedule.helpfullTools;

import com.serge.schedule.R;

import java.lang.reflect.Array;

/**
 * Created by serge on 30.01.2017.
 */

public class ATTRIBUTE_CONSTS {
    final String ATTRIBUTE_NAME_SUBJECT = "subject";
    final String ATTRIBUTE_NAME_TIME = "time";
    final String ATTRIBUTE_NAME_CLASSROOM = "classroom";
    final String ATTRIBUTE_NAME_LECTURER = "lecturer";
    final String ATTRIBUTE_NAME_INFO = "info";

    String[] from = {ATTRIBUTE_NAME_SUBJECT, ATTRIBUTE_NAME_TIME, ATTRIBUTE_NAME_CLASSROOM, ATTRIBUTE_NAME_LECTURER, ATTRIBUTE_NAME_INFO};

    int[] to = {R.id.item_text_view_subject, R.id.item_text_view_time, R.id.item_text_view_classroom, R.id.item_text_view_lecturer, R.id.item_text_view_info};


    public String[] getFrom() {
        return from;
    }

    public int[] getTo() {
        return to;
    }
}
