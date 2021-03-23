package com.android.devicecotrol;

/**
 * author : shengping.tian
 * time   : 2021/03/18
 * desc   : 设备接口定义
 * version: 1.0
 */
public interface IDeviceCon {

    OperateConstants startRead();

    OperateConstants stopRead();

    OperateConstants startWrite();

    OperateConstants stopWrite();

    void addReadListener(OnReadListener listener);

    void removeReadListener(OnReadListener listener);

    void addWriteListener(OnWriteListener listener);

    void removeWriteListener(OnWriteListener listener);

    void destroy();

}
