package com.serge.schedule.list;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.serge.schedule.R;
/**
 * Created by Sergey on 23.02.2017.
 */

public class SubjectTypeManager {

    private ArrayList<SubjectItem> subjectItems;
    private Context context;

    public SubjectTypeManager(Context context) {
        subjectItems = new ArrayList<>(10);
        this.context = context;
    }

    private void Parse(InputStream inputStream) throws XmlPullParserException {
        if (inputStream == null)
            return;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new InputStreamReader(inputStream));

        SubjectItem s = new SubjectItem();
        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("subject")) {
                            s = new SubjectItem(xpp.getAttributeValue(0), xpp.getAttributeValue(1), xpp.getAttributeValue(2));
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equals("subject")) {
                            subjectItems.add(s);
                        }
                        break;
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Show()
    {
        InputStream object = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (!preferences.getBoolean("filesExists", false)) {
            object = context.getResources().openRawResource(R.raw.subjects_list);
        } else
        {
            File subjectListFile;
            subjectListFile = new File(context.getFilesDir(), "SubjectsList.xml");;
            try {
                object = new BufferedInputStream(new FileInputStream(subjectListFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            Parse(object);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        Activity activity = (Activity) context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //View view = layoutInflater.inflate(R.layout.activity_list, null);
        LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.list_item_layour);

        TextView textView1 = new TextView(context);
        textView1.setText("Экзамены");
        textView1.setTextSize(23);
        textView1.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(textView1);

        for (int i = 0; i < subjectItems.size(); i++) {
            if(!subjectItems.get(i).getType().equals("E"))
                continue;
            View item = layoutInflater.inflate(R.layout.list_item, linearLayout, false);
            TextView textView = (TextView) item.findViewById(R.id.sub_name);
            textView.setText(subjectItems.get(i).getName());
            textView.setTextSize(18);
            textView.setTypeface(null, Typeface.BOLD);
            TextView textView2 = (TextView) item.findViewById(R.id.teacher_name);
            textView2.setText(subjectItems.get(i).getTeacher());
            textView2.setTextSize(16);
            textView2.setTypeface(null, Typeface.ITALIC);
/*            TextView textView3 = (TextView) item.findViewById(R.id.sub_type);
            textView3.setText(subjectItems.get(i).getType());*/
            item.setPadding(20, 0, 0, 15);
            linearLayout.addView(item);
        }

        TextView textView4 = new TextView(context);
        textView4.setText("Зачеты");
        textView4.setTextSize(23);
        textView4.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(textView4);

        for (int i = 0; i < subjectItems.size(); i++) {
            if(subjectItems.get(i).getType().equals("E"))
                continue;
            View item = layoutInflater.inflate(R.layout.list_item, linearLayout, false);
            TextView textView = (TextView) item.findViewById(R.id.sub_name);
            textView.setText(subjectItems.get(i).getName());
            textView.setTextSize(18);
            textView.setTypeface(null, Typeface.BOLD);
            TextView textView2 = (TextView) item.findViewById(R.id.teacher_name);
            textView2.setText(subjectItems.get(i).getTeacher());
            textView2.setTextSize(16);
            textView2.setTypeface(null, Typeface.ITALIC);
            /*TextView textView3 = (TextView) item.findViewById(R.id.sub_type);
            textView3.setText(subjectItems.get(i).getType());*/
            item.setPadding(20, 0, 0, 15);
            linearLayout.addView(item);
        }

    }

    public ArrayList<SubjectItem> getSubjectItems() {
        return subjectItems;
    }
}
