package com.cdqf.plant_state;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密
 * Created by XinAiXiaoWen on 2017/4/21.
 */

public class MD5Utils {

    private static String codeEncryption = "qifengkeji2017-4:zmgx2.0";

    public static String getMD5() {
       return getMDTEncryption(codeEncryption);
    }

    public static String getMD5(String code) {
        code += codeEncryption;
        return getMDTEncryption(code);
    }

    private static String getMDTEncryption(String code){
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(code.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int i = b & 0xFF;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
