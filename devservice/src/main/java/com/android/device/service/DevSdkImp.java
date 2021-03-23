package com.android.device.service;

import android.util.Log;

import com.android.devicecotrol.OnInitSdkListener;
import com.android.devicecotrol.OnReadListener;
import com.android.devicecotrol.OnWriteListener;
import com.android.library.ISerialDataListener;
import com.android.library.SerialManager;

/**
 * author : shengping.tian
 * time   : 2021/03/18
 * desc   : 与串口进行交互
 * version: 1.0
 */
public class DevSdkImp implements DevSdk {

    private static final String TAG = "DevSdkImp";

    private volatile boolean mInitSuccess = false;

    private volatile boolean mSdkInitializing = false;

    private volatile boolean mWorkThreadRun = false;

    private InitSdkThread mInitSdkThread;

    private WorkThread mWorkThread;

    private OnReadListener mOnReadListener;


    private final ISerialDataListener mSerialDataListener = new ISerialDataListener() {
        @Override
        public void onDataReceived(String s) {

        }
    };


    @Override
    public void init(OnInitSdkListener listener) {
        startInitSdk(listener);
    }

    private synchronized void startInitSdk(OnInitSdkListener listener) {
        if (mInitSuccess) {
            Log.i(TAG, " startInitSdk has init");
            listener.onResult(true);
            return;
        }
        if (mInitSdkThread == null || mInitSdkThread.getState() == Thread.State.TERMINATED) {
            Log.i(TAG, " start init sdk");
            mInitSdkThread = new InitSdkThread(listener);
            mInitSdkThread.start();
        }

    }

    @Override
    public void readData(OnReadListener listener) {
        if (!mInitSuccess) {
            Log.e(TAG, "readData -> mInitSuccess = false");
            return;
        }
        mOnReadListener = listener;
        if (mWorkThread == null) {
            startWorkThread();
        }
    }

    private void startWorkThread() {
        Log.e(TAG, "startWorkThread");
        mWorkThreadRun = true;
        mWorkThread = new WorkThread();
        mWorkThread.start();
    }

    @Override
    public void stopReadData() {
        mWorkThreadRun = false;
        mWorkThread = null;
        SerialManager.getInstance().unRegisterListener(mSerialDataListener);
    }

    @Override
    public void writeData(OnWriteListener listener) {
//        SerialManager.getInstance().write();
    }

    @Override
    public void stopWriteData() {

    }

    @Override
    public void destroy() {
        mInitSdkThread = null;
        stopReadData();
        SerialManager.getInstance().closeSerialPort();
    }


    /**
     * 初始化sdk线程
     */
    private class InitSdkThread extends Thread {
        private final OnInitSdkListener mListener;

        private InitSdkThread(OnInitSdkListener listener) {
            mListener = listener;
        }

        @Override
        public void run() {
            Log.i(TAG, " startInitSdk thread");
            mSdkInitializing = true;
            SerialManager.getInstance().openSerialPort("/dev/ttyS1", 9600, (b, s) -> {
                mSdkInitializing = false;
                mInitSuccess = b;
                mListener.onResult(mInitSuccess);
            });
        }
    }

    private class WorkThread extends Thread {
        @Override
        public void run() {
            if (mWorkThreadRun) {
                SerialManager.getInstance().registerListener(mSerialDataListener);
            }
        }
    }
}
