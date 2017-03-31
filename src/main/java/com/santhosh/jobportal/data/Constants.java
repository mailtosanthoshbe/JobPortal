package com.santhosh.jobportal.data;

/**
 * Created by AUS8KOR on 3/26/2017.
 */

public class Constants {
    public static final int REQUEST_SIGNUP = 100;

    public static final int TYPE_JOBSEEKER = 0;
    public static final int TYPE_EMPLOYER = 1;
    public static final String TAG = "JobPortal";
    public static final int MODE_LIST = 500;
    public static final int MODE_DETAIL = 501;
    public static final int MODE_NOTI = 502;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    public interface JobStatus {
        int NEW = 0;
        int SUBMIT = NEW + 1;
        int SHORTLISTED = SUBMIT + 1;
        int QUIZ_SUBMIT = SHORTLISTED + 1;
        int CONFIRM = QUIZ_SUBMIT + 1;
    }

    public interface NotiOps {
        int SAVE = 0;
        int UPDATE = SAVE + 1;
        int REMOVE = UPDATE + 1;
        int GET = REMOVE + 1;
    }
}
