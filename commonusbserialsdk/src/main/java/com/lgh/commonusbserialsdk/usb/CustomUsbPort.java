package com.lgh.commonusbserialsdk.usb;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import com.hoho.android.usbserial.driver.CommonUsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialDriver;

import java.io.IOException;
import java.util.EnumSet;

/**
 * 描述：usb通信端口
 * 作者：liugh
 * 日期：2021/12/25
 * 版本：V1.0.0
 */
public class CustomUsbPort extends CommonUsbSerialPort {

    public CustomUsbPort(UsbDevice device, int portNumber) {
        super(device, portNumber);
    }

    @Override
    protected void openInt(UsbDeviceConnection connection) throws IOException {
        for (int i = 0; i < mDevice.getInterfaceCount(); i++) {
            UsbInterface usbIface = mDevice.getInterface(i);
            if (!mConnection.claimInterface(usbIface, true)) {
                throw new IOException("Could not claim data interface");
            }
        }

        UsbInterface dataIface = mDevice.getInterface(0);
        for (int i = 0; i < dataIface.getEndpointCount(); i++) {
            UsbEndpoint endpoint = dataIface.getEndpoint(i);
            if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                mReadEndpoint = endpoint;
            }
            if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                mWriteEndpoint = endpoint;
            }
        }
    }

    @Override
    protected void closeInt() {
        try {
            for (int i = 0; i < mDevice.getInterfaceCount(); i++) {
                mConnection.releaseInterface(mDevice.getInterface(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public UsbSerialDriver getDriver() {
        return null;
    }

    @Override
    public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
    }

    @Override
    public EnumSet<ControlLine> getControlLines() throws IOException {
        return null;
    }

    @Override
    public EnumSet<ControlLine> getSupportedControlLines() throws IOException {
        return null;
    }
}
