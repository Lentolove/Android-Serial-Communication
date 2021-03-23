package com.android.device.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.devicecotrol.IDeviceCon;
import com.android.devicecotrol.IDeviceControl;
import com.android.devicecotrol.IInitSdkListener;
import com.android.devicecotrol.IReadListener;
import com.android.devicecotrol.IWriteListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * author : shengping.tian
 * time   : 2021/03/18
 * desc   :
 * version: 1.0
 */
public class DevService extends Service {

    private static final String TAG = "DevService";

    private final DevBinder devBinder = new DevBinder();

    private DevSdkImp mDevSdkImp;

    private final Map<String,IInitSdkListener> mClientInitSdkListenerMap = new ConcurrentHashMap<>();

    private final Map<String,IReadListener> mReadListenerMap = new ConcurrentHashMap<>();

    private final Map<String,IWriteListener> mWriteListenerMap = new ConcurrentHashMap<>();

    private final IInitSdkListener iInitSdkListener = new IInitSdkListener() {
        @Override
        public void onResult(boolean result) throws RemoteException {

        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };



    @Override
    public IBinder onBind(Intent intent) {
        return devBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDevSdkImp = new DevSdkImp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG," onDestroy ");
        clearAllCallbackMap();
        mDevSdkImp.stopReadData();
        mDevSdkImp.stopWriteData();
        mDevSdkImp = null;
    }

    private void clearAllCallbackMap() {
        mClientInitSdkListenerMap.clear();
        mReadListenerMap.clear();
        mWriteListenerMap.clear();
    }

    private class DevBinder extends IDeviceControl.Stub{

        @Override
        public void initSdk(IInitSdkListener listener) throws RemoteException {
            String callPid = String.valueOf(Binder.getCallingPid());
            mClientInitSdkListenerMap.put(callPid,listener);
            
        }

        @Override
        public void readData(IReadListener listener) throws RemoteException {

        }

        @Override
        public void stopReadData() throws RemoteException {

        }

        @Override
        public void writeData(IWriteListener listener) throws RemoteException {

        }

        @Override
        public void stopWriteData() throws RemoteException {

        }

        @Override
        public void destroySdk() throws RemoteException {

        }
    }

}
