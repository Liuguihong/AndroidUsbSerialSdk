package com.lgh.commonusbserialsdk.thread;

import com.lgh.commonusbserialsdk.listen.UsbSerialListener;
import com.hoho.android.usbserial.driver.UsbSerialPort;

/**
 * 描述：数据收发处理线程基类
 * 作者：liugh
 * 日期：2021/11/23
 * 版本：V1.0.0
 */
public abstract class BaseThread {
    protected static final int TIMEOUT = 100;
    protected static final int CAPACITY = 64;
    protected UsbSerialPort mUsbSerialPort;
    protected UsbSerialListener mUsbSerialListener;

    public BaseThread(UsbSerialListener listener) {
        this.mUsbSerialListener = listener;
    }

    public void start(UsbSerialPort usbSerialPort) {
        this.mUsbSerialPort = usbSerialPort;
    }

    public abstract boolean isStart();

    public abstract void stop();

    public abstract void write(byte[] bytes);

}
