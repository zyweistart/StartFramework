package start.application.commons.codec;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import start.application.core.config.ConstantConfig;

public class DES {
	
	public final static String ALGORITHM="DES";
	
	/**
	 * 获取文件密钥
	 */
	public static Key getKey(String keyFileName) throws IOException, ClassNotFoundException {
		InputStream is = null;
		ObjectInputStream ois = null;
		try {
			is = new FileInputStream(keyFileName);
			ois = new ObjectInputStream(is);
			return (Key) ois.readObject();
		} finally {
			if(ois != null) {
				try {
					ois.close();
				}finally{
					ois = null;
				}
			}
			if(is != null) {
				try {
					is.close();
				}finally{
					is = null;
				}
			}
		}
	}
	
	/**
	 * DES加密
	 */
	public static byte[] encrypt(String str, String keyFileName) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException, IOException, NoSuchAlgorithmException, NoSuchPaddingException{
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, getKey(keyFileName));
		return cipher.doFinal(str.getBytes(ConstantConfig.ENCODING));
	}
	
	/**
	 * DES解密
	 */
	public static String decrypt(byte[] bytes, String keyFileName) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, ClassNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, getKey(keyFileName));
		return new String(cipher.doFinal(bytes),ConstantConfig.ENCODING);
	}

}