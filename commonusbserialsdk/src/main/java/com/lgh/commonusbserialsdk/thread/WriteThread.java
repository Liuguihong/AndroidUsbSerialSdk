package com.lgh.commonusbserialsdk.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.lgh.commonusbserialsdk.listen.UsbSerialListener;
import com.lgh.commonusbserialsdk.utils.HexUtil;
import com.lgh.commonusbserialsdk.utils.LogUtil;
import com.hoho.android.usbserial.driver.UsbSerialPort;

/**
 * 描述：指令下发线程实现类
 * 作者：liugh
 * 日期：2021/11/23
 * 版本：V1.0.0
 */
public class WriteThread extends BaseThread {
    private HandlerThread mWriteThread;
    private Handler mWriteHandler;

    public WriteThread(UsbSerialListener listener) {
        super(listener);
    }

    @Override
    public void start(UsbSerialPort usbSerialPort) {
        super.start(usbSerialPort);
        if (mWriteThread == null) {
            mWriteThread = new HandlerThread("mWriteThread");
            mWriteThread.start();
        }
        if (mWriteHandler == null) {
            mWriteHandler = new Handler(mWriteThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        mUsbSerialPort.write(bytes, TIMEOUT);
                        LogUtil.i("write-->" + HexUtil.bytesToHex(bytes));
                        if (mUsbSerialListener != null) {
                            mUsbSerialListener.onWrite(bytes);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
    }

    @Override
    public boolean isStart() {
        return mWriteHandler != null && mWriteThread != null;
    }

    @Override
    public void stop() {
        if (mWriteThread != null) {
            mWriteThread.quit();
            mWriteThread = null;
        }
        if (mWriteHandler != null) {
            mWriteHandler.removeCallbacksAndMessages(null);
            mWriteHandler = null;
        }
    }

    @Override
    public void write(byte[] bytes) {
        if (mWriteHandler == null || mWriteThread == null) {
            return;
        }
        Message message = Message.obtain();
        message.obj = bytes;
        mWriteHandler.sendMessage(message);
    }

}
