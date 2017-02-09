package start.application.commons.codec;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	
	public static final String ALGORITHM = "AES";
	
	public static String fullAlg = "AES/CBC/PKCS5Padding";
	
	public static String encrypt(String key,String content,String charset) throws Exception {
		Cipher cipher = Cipher.getInstance(fullAlg);
		IvParameterSpec iv = new IvParameterSpec(initIv(fullAlg));
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.decode(key), ALGORITHM), iv);
		byte[] encryptBytes = cipher.doFinal(content.getBytes(charset));
		return Base64.encode(encryptBytes);
	}
	
	public String decrypt(String key,String content, String charset) throws Exception {
        //反序列化AES密钥
        SecretKeySpec keySpec = new SecretKeySpec(Base64.decode(key), ALGORITHM);
        //128bit全零的IV向量
        byte[] iv = new byte[16];
        for (int i = 0; i < iv.length; i++) {
            iv[i] = 0;
        }
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        //初始化加密器并加密
        Cipher deCipher = Cipher.getInstance(fullAlg);
        deCipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
        byte[] encryptedBytes = Base64.decode(content);
        byte[] bytes = deCipher.doFinal(encryptedBytes);
        return new String(bytes);
    }

	/**
	 * 初始向量的方法, 全部为0. 这里的写法适合于其它算法,针对AES算法的话,IV值一定是128位的(16字节).
	 */
	private static byte[] initIv(String fullAlg) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance(fullAlg);
		int blockSize = cipher.getBlockSize();
		byte[] iv = new byte[blockSize];
		for (int i = 0; i < blockSize; ++i) {
			iv[i] = 0;
		}
		return iv;
	}

}
