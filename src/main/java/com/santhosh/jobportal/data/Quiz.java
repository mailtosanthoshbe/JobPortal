package com.santhosh.jobportal.data;

/**
 * Created by AUS8KOR on 3/30/2017.
 */

public class Quiz {
    private String mId;
    private String mQuestion;
    private String mAnswer;
    private String[] mOptions;

    public Quiz(String id, String question, String answer, String[] options) {
        mId = id;
        mQuestion = question;
        mAnswer = answer;
        mOptions = options;
    }

    public String getId() {
        return mId;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public String[] getOptions() {
        return mOptions;
    }


}
