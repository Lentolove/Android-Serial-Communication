package com.android.device.service;

import com.android.devicecotrol.OnInitSdkListener;
import com.android.devicecotrol.OnReadListener;
import com.android.devicecotrol.OnWriteListener;

/**
 * author : shengping.tian
 * time   : 2021/03/18
 * desc   :
 * version: 1.0
 */
public interface DevSdk {

    void init(OnInitSdkListener listener);

    void readData(OnReadListener listener);

    void stopReadData();

    void writeData(OnWriteListener listener);

    void stopWriteData();

    void destroy();

}
