package com.rachana.firebaseworkshop;

public class  UserResponse{

    private String name;
    private String college;
    private String collegeId;
    private String latitude;
    private String longitude;
    private String result;

    public UserResponse() {
    }

    public UserResponse(String name, String college, String collegeId, String latitude, String longitude, String result) {
        this.name = name;
        this.college = college;
        this.collegeId = collegeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}


