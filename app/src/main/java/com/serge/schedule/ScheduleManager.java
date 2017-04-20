package com.serge.schedule;

import com.serge.schedule.helpfullTools.ATTRIBUTE_CONSTS;
import com.serge.schedule.helpfullTools.Day;
import com.serge.schedule.helpfullTools.Subject;
import com.serge.schedule.helpfullTools.Time;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by serge on 29.01.2017.
 */

public class ScheduleManager {
    private List<Day> days;
    private Calendar firstStudyDay;
    private int weekNum;

    public int getNumOfDay() {
        int numDay = (GregorianCalendar.getInstance()).get(Calendar.DAY_OF_WEEK) - 1;
        if (numDay > 6)
            return 1;
        else if(numDay == 0)
            return 6;
        else
            return numDay;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }

    public Day getDay(int dayNumber) {
        return days.get(dayNumber);
    }

    public String getWeekParaty() {
        if (weekNum % 2 == 0)
            return "Четная неделя (" + weekNum + ")";
        else
            return "Нечетная неделя (" + weekNum + ")";
    }

    public int getCurrentWeekNum() {
        Calendar today = GregorianCalendar.getInstance();

        int currentWeek = today.get(Calendar.WEEK_OF_YEAR);
        int firstStudyWeek = firstStudyDay.get(Calendar.WEEK_OF_YEAR);
        int currentDay = today.get(Calendar.DAY_OF_WEEK);

        int weekPass = currentWeek - firstStudyWeek + 1;
        //if (currentDay == 1)
            //weekPass++;

        return weekPass;
    }

    public ScheduleManager() {
        days = new ArrayList<>();
    }

    public List<Map<String, Object>> getDays(int dayNumber) {
        ATTRIBUTE_CONSTS consts = new ATTRIBUTE_CONSTS();
        String[] attributes = consts.getFrom();

        ArrayList<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> m;
        Subject s;
        int subNum = 1;
        boolean invisibleSubject = false;
        boolean emptySubject = false;

        for (int i = 0; i < days.get(dayNumber).getNumOfSubjects(); i++) {
            m = new HashMap<String, Object>();
            s = days.get(dayNumber).getSubject(i);

            if (s.getSubjectName().isEmpty()) {
                continue;
            }

            if ((s.getWeekType().equals("e")) || (s.getWeekType().equals("o") && weekNum % 2 != 0) || (s.getWeekType().equals("p") && weekNum % 2 == 0)) {
                m.put(attributes[0], (subNum) + ". " + s.getSubjectName());
                if (s.getStartTime().getHours() != -1)
                    m.put(attributes[1], s.getStartTime().toString() + "-" + s.getFinishTime());
                else
                    m.put(attributes[1], "");
                if (s.getClassRoom() != -1)
                    m.put(attributes[2], s.getClassRoom());
                else
                    m.put(attributes[2], "");
                m.put(attributes[3], s.getLecturer());
                m.put(attributes[4], "Доп. информация: " + s.getAdditionalInfo());
                data.add(m);
                subNum++;
            } else if (s.getWeekNum() != null) {
                for (int dayOfweek = 0; dayOfweek < s.getWeekNum().size(); dayOfweek++) {
                    if (weekNum == s.getWeekNum().get(dayOfweek)) {
                        m.put(attributes[0], (subNum) + ". " + s.getSubjectName());

                        if (s.getStartTime().getHours() != -1)
                            m.put(attributes[1], s.getStartTime().toString() + "-" + s.getFinishTime());
                        else
                            m.put(attributes[1], "");
                        if (s.getClassRoom() != -1)
                            m.put(attributes[2], s.getClassRoom());
                        else
                            m.put(attributes[2], "");
                        m.put(attributes[3], s.getLecturer());
                        m.put(attributes[4], "Доп. информация: " + s.getAdditionalInfo());
                        data.add(m);
                        subNum++;
                    }
                }
            } else {
                continue;
            }
        }
        return data;
    }

    public void Parse(InputStream inputStream) throws XmlPullParserException {
        if (inputStream == null)
            return;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new InputStreamReader(inputStream));

        days.clear();
        Time lessonTime = new Time();
        Time pastTime = new Time();
        firstStudyDay = new GregorianCalendar();
        Day d;
        Subject s = new Subject();
        d = new Day();
        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("lessonTime")) {
                            lessonTime = new Time(Integer.parseInt(xpp.getAttributeValue(0)), Integer.parseInt(xpp.getAttributeValue(1)));
                        }

                        if (xpp.getName().equals("firstDay")) {
                            firstStudyDay.set(Integer.parseInt(xpp.getAttributeValue(0)),
                                    Integer.parseInt(xpp.getAttributeValue(1)) - 1,
                                    Integer.parseInt(xpp.getAttributeValue(2)));
                            weekNum = getCurrentWeekNum();
                        }

                        if (xpp.getName().equals("day")) {
                            d = new Day();
                            d.setName(xpp.getAttributeValue(0));
                        }

                        if (xpp.getName().equals("subject")) {
                            s = new Subject();
                            s.setSubjectName(xpp.getAttributeValue(0));
                            if (!xpp.getAttributeValue(1).isEmpty())
                                s.setSubjectNum(Integer.parseInt(xpp.getAttributeValue(1)));
                            else
                                s.setSubjectNum(-1);
                            if (!xpp.getAttributeValue(2).isEmpty() && !xpp.getAttributeValue(3).isEmpty()) {
                                s.setStartTime(Integer.parseInt(xpp.getAttributeValue(2)),
                                        Integer.parseInt(xpp.getAttributeValue(3)));
                                s.setFinishTime((new Time(s.getStartTime())).add(lessonTime));
                            } else
                                s.setStartTime(-1, -1);
                            if (!xpp.getAttributeValue(4).isEmpty())
                                s.setClassRoom(Integer.parseInt(xpp.getAttributeValue(4)));
                            else
                                s.setClassRoom(-1);
                            s.setLecturer(xpp.getAttributeValue(5));
                            s.setAdditionalInfo(xpp.getAttributeValue(6));
                            s.setWeekType(xpp.getAttributeValue(7));
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equals("subject")) {
                            d.addSubject(s);
                        }
                        if (xpp.getName().equals("day")) {
                            days.add(d);
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
}
