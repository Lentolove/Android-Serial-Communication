package com.android.devicecotrol;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author : shengping.tian
 * time   : 2021/03/18
 * desc   : AIDL 数据传递 bean
 * version: 1.0
 */
public class DataBean implements Parcelable {

    private String data;

    private int length;

    public DataBean(String data) {
        this.data = data;
    }


    protected DataBean(Parcel in) {
        data = in.readString();
        length = in.readInt();
    }

    public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
        @Override
        public DataBean createFromParcel(Parcel in) {
            return new DataBean(in);
        }

        @Override
        public DataBean[] newArray(int size) {
            return new DataBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeInt(length);
    }
}
