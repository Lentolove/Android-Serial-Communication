package com.android.library;

import com.blankj.utilcode.util.ConvertUtils;

import java.io.IOException;
import java.io.OutputStream;

import static com.android.library.ExecutorUtils.executors;


public class SerialWriter {

    private final OutputStream mOutputStream;

    public SerialWriter(OutputStream outputStream){
        mOutputStream = outputStream;
    }

    public void write(String data) {
        executors.execute(() -> {
            try {
                byte[] temp = ConvertUtils.hexString2Bytes(data);
                mOutputStream.write(temp);
                mOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
