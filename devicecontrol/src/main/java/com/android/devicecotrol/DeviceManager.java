package com.android.devicecotrol;

import android.content.Context;
import android.util.Log;

import static com.android.devicecotrol.OperateConstants.OPERATION_FAILED;

/**
 * author : shengping.tian
 * time   : 2021/03/18
 * desc   : 设备管理类,提供给客户端调用
 * version: 1.0
 */
public class DeviceManager implements IDeviceCon {

    private static final String TAG = "DeviceManager";

    private DeviceController mDeviceController;

    private DeviceManager() {
    }

    private static class SingleHolder {
        private static final DeviceManager instance = new DeviceManager();
    }

    public static DeviceManager get() {
        return SingleHolder.instance;
    }

    /**
     * 提供给client初始化,同一个client不能重复初始化
     */
    public synchronized void initialize(Context context, OnInitSdkListener listener) {
        if (mDeviceController != null) {
            Log.i(TAG, " please do not repeat initialize");
            listener.onResult(false);
            return;
        }
        mDeviceController = new DeviceController(context, listener);
    }


    @Override
    public OperateConstants startRead() {
        Log.i(TAG, " startRead ");
        if (mDeviceController != null){
            return mDeviceController.startRead();
        }
        return OPERATION_FAILED;
    }

    @Override
    public OperateConstants stopRead() {
        Log.i(TAG, " stopRead ");
        if (mDeviceController != null){
            return mDeviceController.stopRead();
        }
        return OPERATION_FAILED;
    }

    @Override
    public OperateConstants startWrite() {
        Log.i(TAG, " startWrite ");
        if (mDeviceController != null){
            return mDeviceController.startWrite();
        }
        return OPERATION_FAILED;
    }

    @Override
    public OperateConstants stopWrite() {
        Log.i(TAG, " stopWrite ");
        if (mDeviceController != null){
            return mDeviceController.stopWrite();
        }
        return OPERATION_FAILED;
    }

    @Override
    public void addReadListener(OnReadListener listener) {
        Log.i(TAG, " addReadListener ");
        if (mDeviceController != null){
            mDeviceController.addReadListener(listener);
        }
    }

    @Override
    public void removeReadListener(OnReadListener listener) {
        Log.i(TAG, " removeReadListener ");
        if (mDeviceController != null){
            mDeviceController.removeReadListener(listener);
        }
    }

    @Override
    public void addWriteListener(OnWriteListener listener) {
        Log.i(TAG, " addWriteListener ");
        if (mDeviceController != null){
            mDeviceController.addWriteListener(listener);
        }
    }

    @Override
    public void removeWriteListener(OnWriteListener listener) {
        Log.i(TAG, " removeWriteListener ");
        if (mDeviceController != null){
            mDeviceController.removeWriteListener(listener);
        }
    }

    @Override
    public void destroy() {
        Log.i(TAG, " destroy ");
        if (mDeviceController != null) {
            mDeviceController.destroy();
            mDeviceController = null;
        }
    }
}
