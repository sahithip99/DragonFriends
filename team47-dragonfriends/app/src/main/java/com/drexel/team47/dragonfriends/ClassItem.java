package com.drexel.team47.dragonfriends;

/**
 * Created by TriLe on 5/9/2018.
 */

public class ClassItem {
    private String crn;
    private String className;
    public ClassItem(String crn, String name){
        System.out.println("Setting roster item...");
        this.crn = crn;
        this.className = name;
    }
    public String getCrn(){
        return this.crn;
    }

    public String getClassName() {
        return this.className;
    }

    public String toString() {
        String str = "CRN: " + this.crn + "// ClassName: " + this.className;
        return str;
    }
}
