package com.kelybro.android.eventbus;

public class LikeCountMessage {

    private  int  countMessage;

    public LikeCountMessage(int countMessage) {
        this.countMessage = countMessage;
    }

    public int getCountMessage() {
        return countMessage;
    }
}
