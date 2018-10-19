package com.baibutao.app.waibao.yun.android.biz.bean;

import java.io.Serializable;

public class TmpHistoryBean implements Serializable {

    private String startTime;
    private String endTime;
    private String distance;

    private String tempMin;
    private String tempMax;

    private String humiMin;
    private String humiMax;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getHumiMin() {
        return humiMin;
    }

    public void setHumiMin(String humiMin) {
        this.humiMin = humiMin;
    }

    public String getHumiMax() {
        return humiMax;
    }

    public void setHumiMax(String humiMax) {
        this.humiMax = humiMax;
    }
}

