package com.lgh.commonusbserialsdk.listen;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

/**
 * 描述：USB连接监听接口
 * 作者：liugh
 * 日期：2021/11/22
 * 版本：v1.0.0
 */
public interface UsbConnectListener {
    /**
     * 设备插入
     *
     * @param usbDevice
     */
    void onAttached(UsbDevice usbDevice);

    /**
     * USB设备授权回调
     *
     * @param usbDevice
     * @param granted   是否授权成功
     */
    void onGranted(UsbDevice usbDevice, boolean granted);

    /**
     * 设备连接状态
     *
     * @param usbDevice
     * @param connected
     */
    void onConnected(UsbDevice usbDevice, boolean connected);

    /**
     * 串口驱动检查
     *
     * @param usbDevice
     * @param driver    是否找到相关驱动
     */
    void onDriverFound(UsbDevice usbDevice, UsbSerialDriver driver);

    /**
     * 串口打开状态
     *
     * @param usbDevice
     * @param success
     */
    void onDeviceOpened(UsbDevice usbDevice, boolean success);

    /**
     * 设备拔出
     *
     * @param usbDevice
     */
    void onDetached(UsbDevice usbDevice);
}
