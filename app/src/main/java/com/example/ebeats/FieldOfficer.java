package com.example.ebeats;

public class FieldOfficer {

    private String uid;
    private String name;

    public FieldOfficer(String user, String n){
        uid = user;
        name = n;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
