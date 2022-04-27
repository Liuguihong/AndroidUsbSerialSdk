package com.lgh.commonusbserialsdk.usb;

import android.hardware.usb.UsbDevice;

/**
 * 描述：判断是否目标设备
 * 作者：liugh
 * 日期：2021/11/22
 * 版本：V1.0.0
 */
public interface IDeviceFilter {
    boolean isTargetDevice(UsbDevice usbDevice);
}
