package com.lgh.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.lgh.commonusbserialsdk.CommonUsbSerialSdk;
import com.lgh.commonusbserialsdk.listen.UsbSerialListener;
import com.lgh.commonusbserialsdk.usb.PortType;

public class MainActivity extends AppCompatActivity {
    private CommonUsbSerialSdk mCommonUsbSerialSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCommonUsbSerialSdk = new CommonUsbSerialSdk(this);
        mCommonUsbSerialSdk.setBaudRate(115200)
                .setDataBits(UsbSerialPort.DATABITS_8) // 数据位
                .setStopBits(UsbSerialPort.STOPBITS_1) // 停止位
                .setParity(UsbSerialPort.PARITY_NONE) // 奇偶校验位
                .setForceGrant(true) // 强制授权，授权不成功重复弹出授权弹窗
                .setPortType(PortType.USB_TO_SERIAL) // 通信类型
                .setDeviceFilter(usbDevice -> true) // 设置USB设备过滤条件
                .setUsbSerialListener(new UsbSerialListener() { // 读写数据回调
                    @Override
                    public void onWrite(byte[] bytes) {

                    }

                    @Override
                    public void onRead(byte[] bytes) {

                    }
                })
                .create();

        mCommonUsbSerialSdk.checkTargetDevice(); // 检查是否有目标设备
    }

    private void write() {
        mCommonUsbSerialSdk.write(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCommonUsbSerialSdk.release();
    }
}