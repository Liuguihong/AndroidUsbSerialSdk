package com.lgh.commonusbserialsdk.thread;

import com.lgh.commonusbserialsdk.listen.UsbSerialListener;
import com.lgh.commonusbserialsdk.utils.HexUtil;
import com.lgh.commonusbserialsdk.utils.LogUtil;
import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.nio.ByteBuffer;

/**
 * 描述：数据接收线程
 * 作者：liugh
 * 日期：2021/11/24
 * 版本：V1.0.0
 */
public class ReadThread extends BaseThread implements Runnable {
    private Thread mReadThread;
    private ByteBuffer mReadBuffer;

    public ReadThread(UsbSerialListener listener) {
        super(listener);
        mReadBuffer = ByteBuffer.allocate(CAPACITY);
    }

    @Override
    public void start(UsbSerialPort usbSerialPort) {
        super.start(usbSerialPort);
        if (mReadBuffer == null) {
            mReadBuffer = ByteBuffer.allocate(CAPACITY);
        }
        if (mReadThread == null) {
            mReadThread = new Thread(this);
            mReadThread.start();
        }
    }

    @Override
    public boolean isStart() {
        return mReadThread != null;
    }

    @Override
    public void stop() {
        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }
        if (mReadBuffer != null) {
            mReadBuffer.clear();
            mReadBuffer = null;
        }
    }

    @Override
    public void write(byte[] bytes) {

    }

    @Override
    public void run() {
        while (mReadThread != null
                && !mReadThread.isInterrupted()
                && mUsbSerialPort != null
                && mUsbSerialPort.isOpen()) {
            try {
                int length = mUsbSerialPort.read(mReadBuffer.array(), TIMEOUT);
                if (length > 0) {
                    byte[] bytes = new byte[length];
                    mReadBuffer.get(bytes, 0, length);
                    LogUtil.i("read-->" + HexUtil.bytesToHex(bytes));
                    if (mUsbSerialListener != null) {
                        mUsbSerialListener.onRead(bytes);
                    }
                    mReadBuffer.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
