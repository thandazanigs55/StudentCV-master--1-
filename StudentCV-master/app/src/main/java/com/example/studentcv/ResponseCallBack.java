package com.example.studentcv;

public interface ResponseCallBack {
    void onResponse(String response);
    void onError(Throwable throwable);
}