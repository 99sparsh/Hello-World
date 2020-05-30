package utils;

import java.util.ArrayList;

public class Post {
    private String user;
    private String dp;
    private String content;
    private String timestamp;
    private String uid;
    private double latitude;
    private double longitude;
    private ArrayList<String> interests;

    public Post() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public Post(String user, String dp, String content, String timestamp, String uid, double latitude, double longitude, ArrayList<String> interests) {
        this.user = user;
        this.dp = dp;
        this.content = content;
        this.timestamp = timestamp;
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.interests = interests;
    }
}

