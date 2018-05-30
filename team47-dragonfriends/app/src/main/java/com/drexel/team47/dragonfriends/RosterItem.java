package com.drexel.team47.dragonfriends;

/**
 * Created by TriLe on 5/7/2018.
 */

public class RosterItem {
    private String studentName;
    private String studentEmail;
    private String uid;
    public RosterItem(String name, String email, String uid){
        System.out.println("Setting roster item...");
        this.studentName = name;
        this.studentEmail = email;
        this.uid = uid;
    }

    public String getStudentName(){
        return this.studentName;
    }

    public String getStudentEmail() {
        return this.studentEmail;
    }

    public String getUid() { return this.uid; }

    public String toString() {
        String str = "Name: " + this.studentName + "// Email: " + this.studentEmail;
        return str;
    }
}
