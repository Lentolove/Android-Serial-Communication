package com.android.devicecotrol;

import com.android.devicecotrol.IInitSdkListener;
import com.android.devicecotrol.IReadListener;
import com.android.devicecotrol.IWriteListener;

interface IDeviceControl {

     void initSdk(IInitSdkListener listener);

     void readData(IReadListener listener);

     void stopReadData();

     void writeData(IWriteListener listener);

     void stopWriteData();

     void destroySdk();

}