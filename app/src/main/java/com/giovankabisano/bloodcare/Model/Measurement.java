package com.giovankabisano.bloodcare.Model;

public class Measurement {
    public int heartRate;
    public int sistolic;
    public int diastolic;
public long timestamp;

    public Measurement(){

    }
    public Measurement(int heartRate, int sistolic, int diastolic, long timestamp){
        this.heartRate = heartRate;
        this.sistolic = sistolic;
        this.diastolic = diastolic;
        this.timestamp= timestamp;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public int getSistolic() {
        return sistolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
