package com.serge.schedule.helpfullTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serge on 29.01.2017.
 */

public class Day {
    private List<Subject> subjects;
    private String name;

    public Day() {
        subjects = new ArrayList<>();
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addSubject(Subject s) {
        subjects.add(s);
    }

    public int getNumOfSubjects() {
        return subjects.size();
    }

    public Subject getSubject(int numSubject) {
        return subjects.get(numSubject);
    }
}
