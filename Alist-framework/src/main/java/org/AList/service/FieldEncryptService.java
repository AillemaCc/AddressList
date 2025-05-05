package org.AList.service;

/**
 * 数据加解密接口
 */
public interface FieldEncryptService {

    /**对数据进行加密*/
    String encrypt(String value);

    /**对数据进行解密*/
    String decrypt(String value);

    /**判断数据是否加密*/
    default boolean isEncrypt(String value) {
        return false;
    }
}
