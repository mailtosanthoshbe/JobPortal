package com.santhosh.jobportal.data;

/**
 * Created by AUS8KOR on 3/28/2017.
 */

public class Account {
    private int userId;
    private int nEmpType;
    private String mName;
    private String mCompany;
    private String mAddress;

    public Account(int id, String name, int type, String company, String address) {
        userId = id;
        nEmpType = type;
        mName = name;
        mCompany = company;
        mAddress = address;
    }

    public int getUserId() {
        return userId;
    }

    public int getEmployerType() {
        return nEmpType;
    }

    public String getName() {
        return mName;
    }

    public String getCompany() {
        return mCompany;
    }

    public String getAddress() {
        return mAddress;
    }
}
