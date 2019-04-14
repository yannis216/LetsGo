package com.example.android.letsgo.Classes;

import java.io.Serializable;

public class SocialModulInfo implements Serializable {
    private int doneCount;
    private long lastDoneTimestamp;
    private float rating;
    private float ratingNum;
    private long durationAvg;

    public SocialModulInfo() {
    }

    public int getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(int doneCount) {
        this.doneCount = doneCount;
    }

    public long getLastDoneTimestamp() {
        return lastDoneTimestamp;
    }

    public void setLastDoneTimestamp(long lastDoneTimestamp) {
        this.lastDoneTimestamp = lastDoneTimestamp;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRatingNum() {
        return ratingNum;
    }

    public void setRatingNum(float ratingNum) {
        this.ratingNum = ratingNum;
    }

    public long getDurationAvg() {
        return durationAvg;
    }

    public void setDurationAvg(long durationAvg) {
        this.durationAvg = durationAvg;
    }
}
