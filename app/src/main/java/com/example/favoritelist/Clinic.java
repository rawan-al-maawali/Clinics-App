package com.example.favoritelist;

import java.io.Serializable;


//implements Serializable!
public class Clinic implements Serializable {
    private String clinicName;
    private String clinicLocation;
    private String clinicImage;
    private double latitude;
    private double longitude;
    private String clinicDesc;
    private double sessionPrice;
    private String clinicType;

//This is so Firebase can function properly


    public Clinic() {
    }

    public Clinic(String clinicName, String clinicLocation, String clinicImage, double latitude, double longitude, String clinicDesc, double sessionPrice, String clinicType) {
        this.clinicName = clinicName;
        this.clinicLocation = clinicLocation;
        this.clinicImage = clinicImage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.clinicDesc = clinicDesc;
        this.sessionPrice = sessionPrice;
        this.clinicType = clinicType;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getClinicLocation() {
        return clinicLocation;
    }

    public String getClinicImage() {
        return clinicImage;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getClinicDesc() {
        return clinicDesc;
    }

    public double getSessionPrice() {
        return sessionPrice;
    }

    public String getClinicType() {
        return clinicType;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public void setClinicLocation(String clinicLocation) {
        this.clinicLocation = clinicLocation;
    }

    public void setClinicImage(String clinicImage) {
        this.clinicImage = clinicImage;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setClinicDesc(String clinicDesc) {
        this.clinicDesc = clinicDesc;
    }

    public void setSessionPrice(double sessionPrice) {
        this.sessionPrice = sessionPrice;
    }

    public void setClinicType(String clinicType) {
        this.clinicType = clinicType;
    }
}