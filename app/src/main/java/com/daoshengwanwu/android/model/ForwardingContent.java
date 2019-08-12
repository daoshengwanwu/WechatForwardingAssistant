package com.daoshengwanwu.android.model;


import java.util.UUID;


public class ForwardingContent {
    private UUID mId = UUID.randomUUID();
    private String mContent = "";


    public ForwardingContent(String content) {
        mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public UUID getId() {
        return mId;
    }
}
