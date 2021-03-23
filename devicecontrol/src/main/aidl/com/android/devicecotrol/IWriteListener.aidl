package com.android.devicecotrol;

import com.android.devicecotrol.DataBean;

interface IWriteListener {

    void onResult(in DataBean bean);
}