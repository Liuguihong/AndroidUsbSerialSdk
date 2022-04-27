package com.lgh.commonusbserialsdk.utils;

import android.text.TextUtils;

/**
 * 描述：数值处理、转换相关工具类
 * 作者：liugh
 * 日期：2021/11/27
 * 版本：V1.0.0
 */
public class HexUtil {
    /**
     * 计算CRC16校验码
     *
     * @param bytes
     * @return
     */
    public static byte[] getCRC16(byte[] bytes) {
        String crc16Hex = getCRC16Hex(bytes);
        return hexToBytes(crc16Hex);
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes
     * @return
     */
    public static String getCRC16Hex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        for (int i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (int j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }

    /**
     * int转2个字节bytes
     *
     * @param value
     * @return
     */
    public static byte[] intToBytes(int value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((value >> 8) & 0xFF);
        bytes[1] = (byte) (value & 0xFF);
        return bytes;
    }

    /**
     * 2字节转int
     *
     * @param b0
     * @param b1
     * @return
     */
    public static int bytes2Int(byte b0, byte b1) {
        int result = 0;
        result = b0 & 0xff;
        result = result << 8 | b1 & 0xff;
        return result;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
            if (i != bytes.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * 16进制字符串转bytes
     *
     * @param hex
     * @return
     */
    public static byte[] hexToBytes(String hex) {
        if (TextUtils.isEmpty(hex)) {
            return null;
        }
        hex = hex.length() % 2 != 0 ? "0" + hex : hex;
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int value = Integer.parseInt(hex.substring(index, index + 2), 16);
            bytes[i] = (byte) value;
        }
        return bytes;
    }

    /**
     * 去除不可见字符
     *
     * @param array
     * @return
     */
    public static String dumpHexString(byte[] array) {
        return dumpHexString(array, 0, array.length);
    }

    /**
     * 去除不可见字符
     *
     * @param array
     * @param offset
     * @param length
     * @return
     */
    public static String dumpHexString(byte[] array, int offset, int length) {
        StringBuilder result = new StringBuilder();
        for (int i = offset; i < offset + length; i++) {
            if (array[i] >= ' ' && array[i] <= '~') {
                result.append(new String(array, i, 1));
            }
        }
        return result.toString();
    }
}
