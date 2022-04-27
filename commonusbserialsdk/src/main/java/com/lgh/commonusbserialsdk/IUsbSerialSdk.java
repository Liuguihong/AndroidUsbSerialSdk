package com.lgh.commonusbserialsdk;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

/**
 * 描述：HetCommonUsbSerialSdk实现接口
 * 作者：liugh
 * 日期：2021/11/22
 * 版本：V1.0.0
 */
public interface IUsbSerialSdk {
    boolean hasTargetDevice();

    void checkTargetDevice();

    void checkDriver(UsbDevice usbDevice);

    void openSerialPort(UsbSerialDriver driver, UsbDevice usbDevice);

    void closeSerialPort();

    void startThread();

    void stopThread();

    void write(byte[] src);

    void release();
}
