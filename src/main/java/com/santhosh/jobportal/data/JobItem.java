package com.santhosh.jobportal.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AUS8KOR on 3/27/2017.
 */

public class JobItem implements Parcelable {
    private int mId;
    private String mCompany;
    private String mTitle;
    private String mLocation;
    private String mExperience;
    private String mSkills;
    private long mDate;
    private String mJobDescription;
    private int nOwnerId;
    private String mStatus;
    private String mAppliers;

    public JobItem(int id, String title, String org, String exp, String loc, String skills,
                   long date, String description, int ownerId, String appliers, String status) {
        mId = id;
        mTitle = title;
        mCompany = org;
        mLocation = loc;
        mExperience = exp;
        mSkills = skills;
        mDate = date;
        mJobDescription = description;
        nOwnerId = ownerId;
        mStatus = status;
        mAppliers = appliers;
        //TODO: will add the descrption
    }

    private JobItem(Parcel in) {
        mId = in.readInt();
        mCompany = in.readString();
        mTitle = in.readString();
        mLocation = in.readString();
        mExperience = in.readString();
        mSkills = in.readString();
        mDate = in.readLong();
        mJobDescription = in.readString();
        mStatus = in.readString();
        mAppliers = in.readString();
        nOwnerId = in.readInt();
    }

    public static final Creator<JobItem> CREATOR = new Creator<JobItem>() {
        @Override
        public JobItem createFromParcel(Parcel in) {
            return new JobItem(in);
        }

        @Override
        public JobItem[] newArray(int size) {
            return new JobItem[size];
        }
    };

    public void setCompany(String mCompany) {
        this.mCompany = mCompany;
    }

    public void setExperience(String mExperience) {
        this.mExperience = mExperience;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public void setSkills(String mSkills) {
        this.mSkills = mSkills;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getId() {
        return mId;
    }

    public String getCompany() {
        return mCompany;
    }

    public String getExperience() {
        return mExperience;
    }

    public String getLocation() {
        return mLocation;
    }

    public long getDate() {
        return mDate;
    }

    public String getSkills() {
        return mSkills;
    }

    public int getOwnerId() {
        return nOwnerId;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        this.mStatus = status;
    }

    public String getJobDescription() {
        return mJobDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mCompany);
        dest.writeString(mTitle);
        dest.writeString(mLocation);
        dest.writeString(mExperience);
        dest.writeString(mSkills);
        dest.writeLong(mDate);
        dest.writeString(mJobDescription);
        dest.writeString(mStatus);
        dest.writeString(mAppliers);
        dest.writeInt(nOwnerId);
    }

    public String getApplicants() {
        return mAppliers;
    }
}
