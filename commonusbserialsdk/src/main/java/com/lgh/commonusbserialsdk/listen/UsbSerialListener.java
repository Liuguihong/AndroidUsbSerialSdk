package com.lgh.commonusbserialsdk.listen;

/**
 * 描述：USB读写数据处理接口
 * 作者：liugh
 * 日期：2021/11/24
 * 版本：V1.0.0
 */
public interface UsbSerialListener {
    void onWrite(byte[] bytes);

    void onRead(byte[] bytes);
}
