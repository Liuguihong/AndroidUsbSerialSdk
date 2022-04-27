package com.lgh.commonusbserialsdk.usb;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

/**
 * 描述：UsbManager实现接口
 * 作者：liugh
 * 日期：2021/11/22
 * 版本：v1.0.0
 */
public interface IUsbManager {
    void registerReceiver();

    void unregisterReceiver();

    boolean hasPermission(UsbDevice usbDevice);

    void requestPermission(UsbDevice usbDevice);

    void connectDevice(UsbDevice usbDevice);

    void closeDevice(UsbDevice usbDevice);

    UsbDeviceConnection getConnection(UsbDevice usbDevice);

    void checkTargetDevice();

    boolean hasTargetDevice();
}
