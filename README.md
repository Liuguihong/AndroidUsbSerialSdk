[**USB、USB转串口、串口通信的区别与实现**](https://blog.csdn.net/u011630465/article/details/124168432?spm=1001.2014.3001.5501)

**本库优势：**
1. 同时满足USB通信和USB转串口通信，免去维护多个SDK烦恼
2. 超简单的使用方式，超简洁的API
3. 支持全方位自定义扩展，与业务高度解耦，满足各种场景需求
4. 无需关注繁琐的USB插拔逻辑

github地址：[https://github.com/Liuguihong/AndroidUsbSerialSdk](https://github.com/Liuguihong/AndroidUsbSerialSdk)

#### 1.添加依赖
compile 'com.github.Liuguihong:AndroidUsbSerialSdk:1.0.0'
#### 2.创建CommonUsbSerialSdk对象
```java
CommonUsbSerialSdk mCommonUsbSerialSdk = new CommonUsbSerialSdk(this);
```
#### 3.添加配置（可选）
```java
mCommonUsbSerialSdk.setBaudRate(115200) // 波特率
        .setDataBits(UsbSerialPort.DATABITS_8) // 数据位
        .setStopBits(UsbSerialPort.STOPBITS_1) // 停止位
        .setParity(UsbSerialPort.PARITY_NONE) // 奇偶校验位
        .setForceGrant(true) // 强制授权，授权不成功重复弹出授权弹窗
        .setPortType(PortType.USB_TO_SERIAL) // 通信类型，可选择usb通信或者usb转串口通信
```
#### 4.设置USB设备过滤条件
```java
mCommonUsbSerialSdk.setDeviceFilter(new IDeviceFilter() {
    @Override
    public boolean isTargetDevice(UsbDevice usbDevice) {
        return usbDevice != null
                && usbDevice.getProductId() == 123
                && usbDevice.getVendorId() == 456;
    }
});
```
#### 5.设置读写数据回调
```java
mCommonUsbSerialSdk.setUsbSerialListener(new UsbSerialListener() { // 读写数据回调
    @Override
    public void onWrite(byte[] bytes) {

    }

    @Override
    public void onRead(byte[] bytes) {

    }
})
```
#### 6.通信
```java
mCommonUsbSerialSdk.write(null);
```
#### 参考
[https://github.com/mik3y/usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android)
