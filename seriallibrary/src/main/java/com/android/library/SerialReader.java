package com.android.library;

import android.os.SystemClock;

import com.blankj.utilcode.util.ConvertUtils;

import java.io.IOException;
import java.io.InputStream;

import static com.android.library.ExecutorUtils.executors;


public class SerialReader implements Runnable {

    private final InputStream mInputStream;
    private final ISerialDataListener mReceiver;
    private boolean mIsInterrupted = false;

    public SerialReader(InputStream inputStream, ISerialDataListener receiver) {
        mInputStream = inputStream;
        mReceiver = receiver;
    }

    @Override
    public void run() {
        InputStream is = mInputStream;
        int available;
        int first;
        try {
            while (!mIsInterrupted && is != null && (first = is.read()) != -1) {
                do {
                    available = is.available();
                    SystemClock.sleep(5);
                } while (available != is.available());
                available = is.available();
                byte[] bytes = new byte[available + 1];
                is.read(bytes, 1, available);
                bytes[0] = (byte) (first & 0xFF);
                report(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void report(final byte[] data) {
        executors.execute(() -> {
            String temp = ConvertUtils.bytes2HexString(data);
            mReceiver.onDataReceived(temp);
        });
    }
}
