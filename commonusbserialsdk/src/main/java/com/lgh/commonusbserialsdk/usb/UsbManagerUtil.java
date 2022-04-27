package com.lgh.commonusbserialsdk.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.lgh.commonusbserialsdk.listen.UsbConnectListener;
import com.lgh.commonusbserialsdk.utils.LogUtil;

import java.util.HashMap;

/**
 * 描述：usb插拔监听、连接工具类
 * 作者：liugh
 * 日期：2021/11/22
 * 版本：v1.0.0
 */
public class UsbManagerUtil implements IUsbManager {
    private static final String ACTION_USB_DEVICE_PERMISSION = "ACTION_USB_DEVICE_PERMISSION";
    private Context mContext;
    private UsbManager mUsbManager;
    private USBReceiver mUsbReceiver;
    private HashMap<UsbDevice, UsbDeviceConnection> mConnectionMap;
    private UsbConnectListener mConnectListener;
    private IDeviceFilter deviceFilter;

    public UsbManagerUtil(Context context) {
        this.mContext = context;
        this.mUsbManager = (UsbManager) context.getSystemService(context.USB_SERVICE);
        this.mConnectionMap = new HashMap<>();
    }

    /**
     * 注册usb插拔监听广播
     */
    @Override
    public void registerReceiver() {
        LogUtil.i("registerReceiver");
        if (mUsbReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            filter.addAction(ACTION_USB_DEVICE_PERMISSION);
            mUsbReceiver = new USBReceiver();
            mContext.registerReceiver(mUsbReceiver, filter);
        }
    }

    /**
     * 注销usb插拔监听广播
     */
    @Override
    public void unregisterReceiver() {
        LogUtil.i("unregisterReceiver");
        if (mUsbReceiver != null) {
            mContext.unregisterReceiver(mUsbReceiver);
            mUsbReceiver = null;
        }
        if (mConnectionMap != null) {
            mConnectionMap.clear();
            mConnectionMap = null;
        }
    }

    /**
     * USB设备权限检查
     *
     * @param usbDevice
     * @return
     */
    @Override
    public boolean hasPermission(UsbDevice usbDevice) {
        LogUtil.i("hasPermission");
        return mUsbManager.hasPermission(usbDevice);
    }

    /**
     * USB设备授权
     *
     * @param usbDevice
     */
    @Override
    public void requestPermission(UsbDevice usbDevice) {
        LogUtil.i("requestPermission");
        if (hasPermission(usbDevice)) {
            if (mConnectListener != null) {
                mConnectListener.onGranted(usbDevice, true);
            }
        } else {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_DEVICE_PERMISSION), 0);
            mUsbManager.requestPermission(usbDevice, pendingIntent);
        }
    }

    /**
     * USB设备连接
     *
     * @param usbDevice
     */
    @Override
    public void connectDevice(UsbDevice usbDevice) {
        LogUtil.i("connectDevice");
        closeDevice(usbDevice);
        UsbDeviceConnection connection = mUsbManager.openDevice(usbDevice);
        if (connection != null) {
            mConnectionMap.put(usbDevice, connection);
        }
        if (mConnectListener != null) {
            mConnectListener.onConnected(usbDevice, connection != null);
        }
    }

    /**
     * 断开USB设备连接
     *
     * @param usbDevice
     */
    @Override
    public void closeDevice(UsbDevice usbDevice) {
        LogUtil.i("closeDevice");
        UsbDeviceConnection connection = mConnectionMap.get(usbDevice);
        if (connection != null) {
            connection.close();
            mConnectionMap.remove(connection);
        }
    }

    /**
     * UsbDeviceConnection
     *
     * @param usbDevice
     * @return
     */
    @Override
    public UsbDeviceConnection getConnection(UsbDevice usbDevice) {
        return mConnectionMap.get(usbDevice);
    }

    /**
     * 查找指定、过滤的设备
     */
    @Override
    public void checkTargetDevice() {
        LogUtil.i("checkDevice");
        UsbDevice usbDevice = getTargetDevice();
        if (usbDevice != null && mConnectListener != null) {
            mConnectListener.onAttached(usbDevice);
        }
    }

    /**
     * 是否存在指定USB设备
     *
     * @return
     */
    @Override
    public boolean hasTargetDevice() {
        return getTargetDevice() != null;
    }

    /**
     * 获取指定USB设备
     *
     * @return
     */
    public UsbDevice getTargetDevice() {
        HashMap<String, UsbDevice> deviceMap = mUsbManager.getDeviceList();
        if (deviceMap != null) {
            for (UsbDevice usbDevice : deviceMap.values()) {
                if (isTargetDevice(usbDevice)) {
                    LogUtil.i("Find target device");
                    return usbDevice;
                }
            }
        }
        LogUtil.i("No target device");
        return null;
    }

    /**
     * 是否目标设备，是相机并且产品id和供应商id跟配置的一致
     *
     * @param usbDevice
     * @return
     */
    public boolean isTargetDevice(UsbDevice usbDevice) {
        if (deviceFilter != null) {
            return deviceFilter.isTargetDevice(usbDevice);
        }
        return false;
    }

    /**
     * 设置USB连接监听器
     *
     * @param listen
     */
    public void setUsbConnectListen(UsbConnectListener listen) {
        this.mConnectListener = listen;
    }

    /**
     * 设置设备过滤器
     *
     * @param deviceFilter
     * @return
     */
    public UsbManagerUtil setDeviceFilter(IDeviceFilter deviceFilter) {
        this.deviceFilter = deviceFilter;
        return this;
    }

    /**
     * usb插拔广播监听类
     */
    private class USBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            LogUtil.i("usbDevice-->" + usbDevice);
            if (!isTargetDevice(usbDevice) || mConnectListener == null) {
                return;
            }

            switch (intent.getAction()) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    LogUtil.i("onAttached");
                    mConnectListener.onAttached(usbDevice);
                    break;

                case ACTION_USB_DEVICE_PERMISSION:
                    boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    LogUtil.i("onGranted-->" + granted);
                    mConnectListener.onGranted(usbDevice, granted);
                    break;

                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    LogUtil.i("onDetached");
                    mConnectListener.onDetached(usbDevice);
                    break;

                default:
                    break;
            }
        }
    }

}
