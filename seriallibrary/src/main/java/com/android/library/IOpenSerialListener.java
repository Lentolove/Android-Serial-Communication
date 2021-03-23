package com.android.library;

/**
 * author : shengping.tian
 * time   : 2021/03/10
 * desc   : 串口打开监听器
 * version: 1.0
 */
public interface IOpenSerialListener {

    void onResult(boolean success,String msg);
}
