package com.example.shorturl.Utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希算法
 */
public class StrHashUtil {

    // 常量声明
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = BASE62.length();
    private static final int SHORT_URL_LENGTH = 6;

    /**
     * SHA256 + base62 哈希算法生成8位URL
     * @param str 原始URL
     * @return 生成的短URL
     */
    public static String StrHash(String str){
        StringBuilder base62str = new StringBuilder();
        String res;
        try {
            // 获取SHA-256哈希算法实例
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(str.getBytes());
            // 将哈希值转换为十进制字符串
            BigInteger hashValue = new BigInteger(1, digest);
            // 将十进制转换为base62编码
            base62str.append(toBase62(hashValue));
            // 截取前6位
            res = base62str.toString().substring(0, SHORT_URL_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("哈希算法不可用", e);
        }
        return res;
    }

    /**
     * 将十进制转换为base62编码
     * @param value 十进制值
     * @return base62编码
     */
    private static String toBase62(BigInteger value){
        StringBuilder res = new StringBuilder();
        while(value.compareTo(BigInteger.ZERO) > 0){
            BigInteger[] divideAndRemainder = value.divideAndRemainder(BigInteger.valueOf(BASE));
            value = divideAndRemainder[0];
            int remainder = divideAndRemainder[1].intValue();
            res.append(BASE62.charAt(remainder));
        }
        // base62编码逆序，需要反转
        return res.reverse().toString();
    }
}
