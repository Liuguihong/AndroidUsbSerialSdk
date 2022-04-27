package com.lgh.commonusbserialsdk;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.lgh.commonusbserialsdk.listen.UsbConnectListener;
import com.lgh.commonusbserialsdk.listen.UsbSerialListener;
import com.lgh.commonusbserialsdk.thread.BaseThread;
import com.lgh.commonusbserialsdk.thread.ReadThread;
import com.lgh.commonusbserialsdk.thread.WriteThread;
import com.lgh.commonusbserialsdk.usb.UsbManagerUtil;
import com.lgh.commonusbserialsdk.usb.CustomUsbPort;
import com.lgh.commonusbserialsdk.usb.IDeviceFilter;
import com.lgh.commonusbserialsdk.usb.PortType;
import com.lgh.commonusbserialsdk.utils.LogUtil;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;

/**
 * 描述：HetCommonUsbSerialSdk
 * 作者：liugh
 * 日期：2021/11/22
 * 版本：V1.0.0
 */
public class CommonUsbSerialSdk implements IUsbSerialSdk {
    protected UsbManagerUtil mUsbManager; // USB工具类
    protected UsbSerialPort mUsbSerialPort; // 串口通信工具类
    protected UsbConnectListener mUsbConnectListener; // USB连接监听
    protected UsbSerialListener mUsbSerialListener; // 读写数据回调
    protected BaseThread mWriteThread; // 数据下发线程
    protected BaseThread mReadThread; // 数据接收线程
    private int mPort = 0; // 通信端口，一般只有一个
    private int mBaudRate = 115200; // 波特率
    private int mDataBits = UsbSerialPort.DATABITS_8; // 数据位
    private int mStopBits = UsbSerialPort.STOPBITS_1; // 停止位
    private int mParity = UsbSerialPort.PARITY_NONE; // 奇偶校验位
    private boolean forceGrant; // 是否强制授权，点取消授权会重新弹出授权弹窗，直至授权成功
    private PortType mPortType = PortType.USB_TO_SERIAL; // 通信类型

    public CommonUsbSerialSdk(Context context) {
        this.mUsbManager = new UsbManagerUtil(context);
    }

    public UsbManagerUtil getUsbManager() {
        return mUsbManager;
    }

    @Override
    public boolean hasTargetDevice() {
        return mUsbManager.hasTargetDevice();
    }

    /**
     * 查找指定设备，适用于没有USB插拔的场景，如连上设备后再进入页面
     */
    @Override
    public void checkTargetDevice() {
        LogUtil.i("checkTargetDevice");
        mUsbManager.registerReceiver();
        mUsbManager.checkTargetDevice();
    }

    /**
     * 检查串口驱动
     *
     * @param usbDevice
     */
    public void checkDriver(UsbDevice usbDevice) {
        LogUtil.i("checkDriver");
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice);
        if (mUsbConnectListener != null) {
            mUsbConnectListener.onDriverFound(usbDevice, driver);
        }
    }

    /**
     * 打开通信端口
     *
     * @param driver
     */
    @Override
    public void openSerialPort(UsbSerialDriver driver, UsbDevice usbDevice) {
        LogUtil.i("openSerialPort");
        try {
            if (mPortType == PortType.USB) { // USB通信，使用USB专用通信端口
                mUsbSerialPort = new CustomUsbPort(usbDevice, 0);
            } else if (mPortType == PortType.USB_TO_SERIAL) { // USB转串口通信，匹配对应驱动
                mUsbSerialPort = driver.getPorts().get(mPort);
            }
            mUsbSerialPort.open(mUsbManager.getConnection(usbDevice));
            mUsbSerialPort.setParameters(mBaudRate, mDataBits, mStopBits, mParity);
            if (mUsbConnectListener != null) {
                mUsbConnectListener.onDeviceOpened(usbDevice, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mUsbConnectListener != null) {
                mUsbConnectListener.onDeviceOpened(usbDevice, false);
            }
        }
    }

    /**
     * 关闭串口
     */
    @Override
    public void closeSerialPort() {
        LogUtil.i("closeSerialPort");
        try {
            if (mUsbSerialPort != null) {
                mUsbSerialPort.close();
                mUsbSerialPort = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopThread();
    }

    /**
     * 启动数据接收线程，写线程在发送第一条指令时再判断启动
     */
    @Override
    public void startThread() {
        if (mReadThread != null && !mReadThread.isStart()) {
            LogUtil.i("start read thread");
            mReadThread.start(mUsbSerialPort);
        }
        if (mWriteThread != null && !mWriteThread.isStart()) {
            LogUtil.i("start write thread");
            mWriteThread.start(mUsbSerialPort);
        }
    }

    /**
     * 停止读写线程
     */
    @Override
    public void stopThread() {
        LogUtil.i("stop thread");
        if (mReadThread != null) {
            mReadThread.stop();
        }
        if (mWriteThread != null) {
            mWriteThread.stop();
        }
    }

    /**
     * 发送指令
     *
     * @param src
     */
    @Override
    public void write(byte[] bytes) {
        if (mUsbSerialPort == null) {
            LogUtil.e("UsbSerialPort can not be null!");
            checkTargetDevice();
            return;
        }
        if (bytes == null) {
            LogUtil.e("bytes can not be null!");
            return;
        }
        startThread();
        if (mWriteThread != null) {
            mWriteThread.write(bytes);
        } else {
            LogUtil.e("WriteThread can not be null!");
        }
    }

    /**
     * 关闭串口设备
     */
    @Override
    public void release() {
        LogUtil.i("release");
        closeSerialPort();
        mUsbManager.unregisterReceiver();
    }

    /**
     * 设置USB连接监听器
     *
     * @param listener
     * @return
     */
    public CommonUsbSerialSdk setUsbConnectListener(UsbConnectListener listener) {
        this.mUsbConnectListener = listener;
        mUsbManager.setUsbConnectListen(listener);
        return this;
    }

    /**
     * 串口数据收发回调
     *
     * @param listener
     * @return
     */
    public CommonUsbSerialSdk setUsbSerialListener(UsbSerialListener listener) {
        this.mUsbSerialListener = listener;
        return this;
    }

    /**
     * 设置数据下发线程，不设置则使用默认
     *
     * @param writeThread
     * @return
     */
    public CommonUsbSerialSdk setWriteThread(BaseThread writeThread) {
        this.mWriteThread = writeThread;
        return this;
    }

    /**
     * 设置数据接收线程，不设置则使用默认
     *
     * @param readThread
     * @return
     */
    public CommonUsbSerialSdk setReadThread(BaseThread readThread) {
        this.mReadThread = readThread;
        return this;
    }

    /**
     * 设备过滤
     *
     * @param filter
     * @return
     */
    public CommonUsbSerialSdk setDeviceFilter(IDeviceFilter filter) {
        mUsbManager.setDeviceFilter(filter);
        return this;
    }

    /**
     * 通信端口，一般只有一个
     *
     * @param port
     * @return
     */
    public CommonUsbSerialSdk setPort(int port) {
        this.mPort = port;
        return this;
    }

    /**
     * 波特率
     *
     * @param baudRate
     * @return
     */
    public CommonUsbSerialSdk setBaudRate(int baudRate) {
        this.mBaudRate = baudRate;
        return this;
    }

    /**
     * 数据位
     *
     * @param dataBits
     * @return
     */
    public CommonUsbSerialSdk setDataBits(int dataBits) {
        this.mDataBits = dataBits;
        return this;
    }

    /**
     * 停止位
     *
     * @param stopBits
     * @return
     */
    public CommonUsbSerialSdk setStopBits(int stopBits) {
        this.mStopBits = stopBits;
        return this;
    }

    /**
     * 奇偶检验位
     *
     * @param parity
     * @return
     */
    public CommonUsbSerialSdk setParity(@UsbSerialPort.Parity int parity) {
        this.mParity = parity;
        return this;
    }

    /**
     * 是否强制授权
     *
     * @param forceGrant
     * @return
     */
    public CommonUsbSerialSdk setForceGrant(boolean forceGrant) {
        this.forceGrant = forceGrant;
        return this;
    }

    /**
     * 设置通信类型，USB通信或者USB转串口通信
     *
     * @param portType
     * @return
     */
    public CommonUsbSerialSdk setPortType(PortType portType) {
        this.mPortType = portType;
        return this;
    }

    /**
     * 非必需，调用该方法即使用默认流程，不调用则需自己处理相关流程
     *
     * @return
     */
    public CommonUsbSerialSdk create() {
        if (mUsbConnectListener == null) {
            mUsbConnectListener = getDefaultUsbConnectListener();
            mUsbManager.setUsbConnectListen(mUsbConnectListener);
        }
        if (mWriteThread == null) {
            mWriteThread = new WriteThread(mUsbSerialListener);
        }
        if (mReadThread == null) {
            mReadThread = new ReadThread(mUsbSerialListener);
        }
        return this;
    }

    /**
     * 默认listener，默认使用通用连接流程
     *
     * @return
     */
    protected UsbConnectListener getDefaultUsbConnectListener() {
        return new DefaultUsbConnectListener();
    }

    /**
     * 默认listener，默认使用通用连接流程
     * 1.查找设备 2.授权 3.连接 4.检查驱动 5.打开串口 6.开启数据接收线程
     */
    protected class DefaultUsbConnectListener implements UsbConnectListener {
        @Override
        public void onAttached(UsbDevice usbDevice) {
            mUsbManager.requestPermission(usbDevice);
        }

        @Override
        public void onGranted(UsbDevice usbDevice, boolean granted) {
            if (granted) {
                mUsbManager.connectDevice(usbDevice);
            } else {
                if (forceGrant) {
                    mUsbManager.requestPermission(usbDevice);
                }
            }
        }

        @Override
        public void onConnected(UsbDevice usbDevice, boolean connected) {
            if (connected) {
                if (mPortType == PortType.USB) { // USB通信，连接成功直接打开串口，不需要驱动
                    openSerialPort(null, usbDevice);
                } else if (mPortType == PortType.USB_TO_SERIAL) { // USB转串口通信，连接成功检查驱动
                    checkDriver(usbDevice);
                }
            }
        }

        @Override
        public void onDriverFound(UsbDevice usbDevice, UsbSerialDriver driver) {
            if (driver != null) {
                openSerialPort(driver, usbDevice);
            }
        }

        @Override
        public void onDeviceOpened(UsbDevice usbDevice, boolean success) {
        }

        @Override
        public void onDetached(UsbDevice usbDevice) {
            closeSerialPort();
        }
    }

}
