package com.android.serialcomdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.library.IOpenSerialListener;
import com.android.library.ISerialDataListener;
import com.android.library.SerialManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private boolean serialIsOpen = false;

    private TextView tv_show;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_show = findViewById(R.id.tv_result);
        input = findViewById(R.id.ed_send);
    }

    /**
     * 获取串口数据
     */
    private ISerialDataListener mListener = new ISerialDataListener() {
        @Override
        public void onDataReceived(String data) {
            // TODO: 2021/3/10 这里获取串口数据
            runOnUiThread(() -> {
                tv_show.setText(data);
            });
        }
    };
    /**
     * 打开串口
     */
    private void openSerial() {
        SerialManager.getInstance().openSerialPort("/dev/ttyS1", 9600, new IOpenSerialListener() {
            @Override
            public void onResult(boolean success, String msg) {
                serialIsOpen = success;
                if (success) {
                    Log.i(TAG, "open serial success ");
                    SerialManager.getInstance().registerListener(mListener);
                } else {
                    Log.e(TAG, "open serial failed " + msg);
                }
            }
        });
    }


    public void open(View view) {
        if (!serialIsOpen) {
            openSerial();
        } else {
            Toast.makeText(this, "serial has opened", Toast.LENGTH_SHORT).show();
        }
    }

    public void send(View view) {
        if (serialIsOpen) {
            String data = input.getText().toString().trim();
            SerialManager.getInstance().write(data);
        } else {
            Toast.makeText(this, "serial not opened", Toast.LENGTH_SHORT).show();
        }
    }

    public void close(View view) {
        if (serialIsOpen) {
            SerialManager.getInstance().closeSerialPort();
        } else {
            Toast.makeText(this, "serial not opened", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SerialManager.getInstance().unRegisterListener(mListener);
    }
}