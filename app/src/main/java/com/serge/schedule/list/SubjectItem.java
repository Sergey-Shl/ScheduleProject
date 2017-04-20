package com.serge.schedule.list;

/**
 * Created by Sergey on 23.02.2017.
 */

public class SubjectItem {
    private String name;
    private String teacher;
    private String type;

    public SubjectItem() {
        this.name = "Name";
        this.teacher = "Teacher";
        this.type = "Exam";
    }

    public SubjectItem(String name, String teacher, String type) {
        this.name = name;
        this.teacher = teacher;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
