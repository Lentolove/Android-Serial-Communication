package com.android.library;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SerialPort {


    /**
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    /**
     * 读数据
     */
    private FileInputStream mFileInputStream;

    /**
     * 写数据
     */
    private FileOutputStream mFileOutputStream;


    public SerialPort(File dev, int porterRate, int flags) throws IOException, SecurityException {
        mFd = open(dev.getAbsolutePath(), porterRate, flags);
        if (mFd == null) {
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    public FileInputStream getFileInputStream() {
        return mFileInputStream;
    }

    public FileOutputStream getFileOutputStream() {
        return mFileOutputStream;
    }

    public void closePort() {
        try {
            if (mFileInputStream != null) {
                mFileInputStream.close();
            }
            if (mFileOutputStream != null) {
                mFileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }


    /**
     * 打开串口
     * @param path       串口名称
     * @param porterRate 波特率
     * @param flags
     * @return
     */
    private native static FileDescriptor open(String path, int porterRate, int flags);

    /**
     * 关闭串口
     */
    private native void close();

    /**
     * 加载 so 库
     */
    static {
        System.loadLibrary("serial");
    }
}
