package com.example.entity;

import lombok.Getter;


@Getter
public class MyMessage {
    private String ask;

    public MyMessage() {

    }


    public void setAsk(String ask) {
        this.ask = ask;
    }
}
