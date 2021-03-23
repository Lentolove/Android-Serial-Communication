package com.android.library;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.android.library.ExecutorUtils.executors;

/**
 * author : shengping.tian
 * time   : 2021/03/10
 * desc   : 串口管理工具类
 * version: 1.0
 */
public class SerialManager implements ISerialDataListener {

    private volatile SerialPort mPort;

    private static final String TAG = "SerialManager";

    private SerialWriter mWriter;

    private List<ISerialDataListener> mListenerList = new ArrayList<>();

    private SerialManager() {

    }

    private static final SerialManager instance = new SerialManager();

    public static SerialManager getInstance() {
        return instance;
    }


    public void openSerialPort(String portName, int porterRate, IOpenSerialListener listener) {
        executors.execute(() -> {
            if (mPort == null) {
                Log.d(TAG, "first init open serial");
                try {
                    mPort = new SerialPort(new File(portName), porterRate, 0);
                    mWriter = new SerialWriter(mPort.getFileOutputStream());
                    executors.execute(new SerialReader(mPort.getFileInputStream(), this::onDataReceived));
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onResult(false, e.getMessage());
                }
            } else {
                Log.d(TAG, "serial has opened");
            }
            listener.onResult(true, "");
        });
    }

    public void closeSerialPort() {
        if (mPort != null) {
            Log.d(TAG, "close serial");
            mPort.closePort();
            mPort = null;
        } else {
            Log.d(TAG, "serial is has closed");
        }
    }

    public void write(String data){
        if(mPort==null){
            Log.e(TAG,"serial not opened,please check serial.");
            return;
        }
        if(mPort.getFileOutputStream() == null){
            Log.e(TAG,"serial outPutStream is null,please check seria.");
            return;
        }
        mWriter.write(data);
    }

    public synchronized void registerListener(ISerialDataListener listener){
        mListenerList.add(listener);
    }

    public synchronized void unRegisterListener(ISerialDataListener listener){
        mListenerList.remove(listener);
    }


    @Override
    public void onDataReceived(String data) {
        synchronized (this){
            for (ISerialDataListener listener : mListenerList) {
                listener.onDataReceived(data);
            }
        }
    }
}
