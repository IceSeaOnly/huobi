package site.binghai.coin.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by binghai on 2017/9/9.
 *
 * @ MoGuJie
 */
public class MD5 {
    /**
     * @param plainText 明文
     * @return 32位密文
     */
    public static String encryption(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    public static String shortMd5(String text) {
        return encryption(text).substring(0, 6);
    }

    /**
     * 生成随机的短密码
     */
    public static String sortPass() {
        return MD5.encryption(System.currentTimeMillis() + "").substring(17);
    }
}
