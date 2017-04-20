package com.serge.schedule.helpfullTools;

/**
 * Created by serge on 29.01.2017.
 */

public class Time {
    private int hours;
    private int minutes;

    public Time() {
        this.hours = 0;
        this.minutes = 0;
    }

    public Time(Time time) {
        this.hours = time.hours;
        this.minutes = time.minutes;
    }

    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public void add(int hourse, int minuts) {
        this.minutes += minuts;
        if (this.minutes > 59) {
            this.minutes -= 60;
            this.hours++;
            this.hours += hourse;
        }
        this.hours += hourse;
    }

    public Time add(Time time) {
        this.minutes += time.minutes;
        if (this.minutes > 59) {
            this.minutes -= 60;
            this.hours++;
        }
        this.hours += time.hours;
        return this;
    }

    @Override
    public String toString() {
        if (minutes < 10)
            return hours + ":0" + minutes;
        else
            return hours + ":" + minutes;
    }

    public int getHours() {
        return hours;
    }
}
