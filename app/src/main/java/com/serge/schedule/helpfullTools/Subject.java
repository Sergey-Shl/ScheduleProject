package com.serge.schedule.helpfullTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serge on 29.01.2017.
 */

public class Subject {
    private String subjectName;
    private int subjectNum;
    private Time startTime;
    private Time finishTime;
    private int classRoom;
    private String lecturer;
    private int weekParity; // Parity - 2, Odd- 1, Every week - 0, other - 3
    private String additionalInfo;
    private String weekType;
    private List<Integer> weekNum;

    public Subject() {
        subjectName = new String();
        startTime = new Time();
        finishTime = new Time();
        lecturer = new String();
    }

    public Subject(Time finishTime, int subjectNum, int classRoom, String lecturer, String subjectName, Time startTime) {
        this.finishTime = finishTime;
        this.classRoom = classRoom;
        this.lecturer = lecturer;
        this.subjectName = subjectName;
        this.startTime = startTime;
        this.subjectNum = subjectNum;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setSubjectNum(int subjectNum) {
        this.subjectNum = subjectNum;
    }

    public void setStartTime(int h, int m) {
        Time t = new Time(h, m);
        this.startTime = t;
    }

    public void setFinishTime(int h, int m) {
        Time t = new Time(h, m);
        this.finishTime = t;
    }

    public void setFinishTime(Time finishTime) {
        this.finishTime = finishTime;
    }

    public void setClassRoom(int classRoom) {
        this.classRoom = classRoom;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getFinishTime() {
        return finishTime;
    }

    public int getClassRoom() {
        return classRoom;
    }

    public String getLecturer() {
        return lecturer;
    }

    public int getWeekParity() {
        return weekParity;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getWeekType() {
        return weekType;
    }

    public void setWeekType(String weekType) {
        this.weekType = weekType;

        switch (weekType) {
            case "e":
                weekParity = 0;
                break;
            case "o":
                weekParity = 1;
                break;
            case "p":
                weekParity = 2;
                break;
            default:
                weekParity = 3;
                parseWeek();
                break;
        }
    }

    public void parseWeek() {
        String str = weekType;
        weekNum = new ArrayList<>(4);
        int i = 0;
        for (String retval : str.split(",")) {
            weekNum.add(Integer.parseInt(retval.trim()));
        }
    }

    public List<Integer> getWeekNum() {
        return weekNum;
    }
}
