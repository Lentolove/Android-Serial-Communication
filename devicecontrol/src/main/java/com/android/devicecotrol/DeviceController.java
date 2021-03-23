package com.android.devicecotrol;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.android.devicecotrol.OperateConstants.OPERATION_FAILED;
import static com.android.devicecotrol.OperateConstants.OPERATION_SUCCESS;
import static com.android.devicecotrol.OperateConstants.SDK_INIT_FAILED;
import static com.android.devicecotrol.OperateConstants.SERVICE_NOT_STARTED;

/**
 * author : shengping.tian
 * time   : 2021/03/18
 * desc   :
 * version: 1.0
 */
public class DeviceController implements IDeviceCon, ServiceConnection {

    private static final String TAG = "DeviceController";


    private Context mContext;

    //远程服务结果监听
    private IInitSdkListener mInitSdkListener;
    private IReadListener mIReadListener;
    private IWriteListener mWriteListener;
    //远程服务
    private IDeviceControl mDeviceService;

    //初始化监听
    private OnInitSdkListener mOnInitSdkListener;
    /**
     * 远程服务初始化结果
     */
    private volatile boolean mInitRemoteServiceResult = false;

    // Client 注册的读取数据监听接口map集合
    private final Map<String, OnReadListener> mReadMap = new ConcurrentHashMap<>();
    private final Map<String, OnWriteListener> mWriteMap = new ConcurrentHashMap<>();

    DeviceController(Context context, OnInitSdkListener listener) {
        this.mContext = context;
        this.mOnInitSdkListener = listener;
        //绑定服务
        bindRemoteService();
    }

    /**
     * 绑定远程 服务
     */
    private void bindRemoteService() {
        Intent intent = new Intent();
        intent.setAction("com.android.device.service.DeviceService");
        intent.setPackage("com.android.device.service");
        mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mDeviceService = IDeviceControl.Stub.asInterface(service);
        Log.i(TAG, " onServiceConnected ");
        mInitSdkListener = new InitSdkListenerImpl();
        mIReadListener = new ReadListenerImpl();
        mWriteListener = new WriteListenerImpl();
        //初始化
        try {
            mDeviceService.initSdk(mInitSdkListener);
        } catch (RemoteException | NullPointerException e) {
            mInitRemoteServiceResult = false;
            e.printStackTrace();
            Log.i(TAG, " initSdk failed: " + e.getMessage());
            if (mOnInitSdkListener != null) {
                mOnInitSdkListener.onResult(false);
                mOnInitSdkListener = null;
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, " onServiceDisconnected ");
        // TODO: 2021/3/18 destroy
        unBindService();
        //重新绑定
        bindRemoteService();
    }

    /**
     * 远程绑定的 client 可能死掉的问题
     *
     * @param name
     */
    @Override
    public void onBindingDied(ComponentName name) {
        if (name != null) {
            Log.i(TAG, " onBindingDied pkg =  " + name.getPackageName());
        }
    }

    @Override
    public void onNullBinding(ComponentName name) {
        if (name != null) {
            Log.i(TAG, " onNullBinding pkg =  " + name.getPackageName());
        }
    }

    private void unBindService() {
        mContext.unbindService(this);
        Log.i(TAG, " unBindService ");
    }


    @Override
    public OperateConstants startRead() {
        OperateConstants status = checkServiceStatus();
        if (status != OPERATION_SUCCESS) {
            return status;
        }
        try {
            mDeviceService.readData(mIReadListener);
        } catch (RemoteException e) {
            Log.i(TAG, " startRead exception :" + e.getMessage());
            e.printStackTrace();
            return OPERATION_FAILED;
        }
        return OPERATION_SUCCESS;
    }

    @Override
    public OperateConstants stopRead() {
        OperateConstants status = checkServiceStatus();
        if (status != OPERATION_SUCCESS) {
            return status;
        }
        try {
            mDeviceService.stopReadData();
        } catch (RemoteException e) {
            Log.i(TAG, " startRead exception :" + e.getMessage());
            e.printStackTrace();
            return OPERATION_FAILED;
        }
        return OPERATION_SUCCESS;
    }

    @Override
    public OperateConstants startWrite() {
        OperateConstants status = checkServiceStatus();
        if (status != OPERATION_SUCCESS) {
            return status;
        }
        try {
            mDeviceService.writeData(mWriteListener);
        } catch (RemoteException e) {
            Log.i(TAG, " startWrite exception :" + e.getMessage());
            e.printStackTrace();
            return OPERATION_FAILED;
        }
        return OPERATION_SUCCESS;
    }

    @Override
    public OperateConstants stopWrite() {
        OperateConstants status = checkServiceStatus();
        if (status != OPERATION_SUCCESS) {
            return status;
        }
        try {
            mDeviceService.stopWriteData();
        } catch (RemoteException e) {
            Log.i(TAG, " stopWrite exception :" + e.getMessage());
            e.printStackTrace();
            return OPERATION_FAILED;
        }
        return OPERATION_SUCCESS;
    }

    private OperateConstants checkServiceStatus() {
        if (mDeviceService == null) {
            Log.i(TAG, "checkServiceStatus mDeviceService == null, return SERVICE_NOT_STARTED");
            return SERVICE_NOT_STARTED;
        }
        if (!isInitSuccess()) {
            Log.i(TAG, "checkServiceStatus isInitSuccess = false, return SDK_INIT_FAILED");
            return SDK_INIT_FAILED;
        }
        return OPERATION_SUCCESS;
    }

    /**
     * 是否初始化成功
     */
    public boolean isInitSuccess() {
        return mInitRemoteServiceResult;
    }

    @Override
    public void addReadListener(OnReadListener listener) {
        String operationId = mContext.getPackageName() + listener.hashCode();
        mReadMap.put(operationId, listener);
    }

    @Override
    public void removeReadListener(OnReadListener listener) {
        mReadMap.remove(listener);
    }

    @Override
    public void addWriteListener(OnWriteListener listener) {
        String operationId = mContext.getPackageName() + listener.hashCode();
        mWriteMap.put(operationId, listener);
    }

    @Override
    public void removeWriteListener(OnWriteListener listener) {
        mWriteMap.remove(listener);
    }

    @Override
    public void destroy() {
        Log.i(TAG, " destroy ");
        mOnInitSdkListener = null;
        mInitRemoteServiceResult = false;
        mReadMap.clear();
        mWriteMap.clear();
        if (mDeviceService == null) {
            Log.i(TAG, " mDeviceService not started");
            return;
        }
        try {
            mDeviceService.destroySdk();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 远程服务初始化监听，初始化结果在这里回调
     */
    private class InitSdkListenerImpl extends IInitSdkListener.Stub {

        @Override
        public void onResult(boolean result) throws RemoteException {
            mInitRemoteServiceResult = result;
            if (mOnInitSdkListener != null) {
                mOnInitSdkListener.onResult(result);
            }
        }
    }

    private class ReadListenerImpl extends IReadListener.Stub {

        @Override
        public void onResult(DataBean databean) throws RemoteException {
            //将结果分发出去
            for (String operationId : mReadMap.keySet()) {
                mReadMap.get(operationId).onResult(databean);
            }
        }
    }

    private class WriteListenerImpl extends IWriteListener.Stub {

        @Override
        public void onResult(DataBean bean) throws RemoteException {
            for (String operationId : mWriteMap.keySet()) {
                mWriteMap.get(operationId).onResult(bean);
            }
        }
    }
}
