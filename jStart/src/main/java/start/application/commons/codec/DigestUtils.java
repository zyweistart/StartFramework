package start.application.commons.codec;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import start.application.core.config.ConstantConfig;

public class DigestUtils {
	
	private final static Integer BUFFERHEX=1024*8;

	public static MessageDigest getSha1Digest() {
        return getDigest("SHA1");
    }
	
	public static MessageDigest getMD5Digest() {
        return getDigest("MD5");
    }
	
	public static MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
	
	public static String md5Hex(String input){
		return toStringHex(input,getMD5Digest());
	}
	
	public static String sha1Hex(String input){
		return toStringHex(input,getSha1Digest());
	}
	
	public static String sha1Hex(File file) {
		return toFileHex(file,getSha1Digest());
	}
	
	public static String md5Hex(File file) {
		return toFileHex(file,getMD5Digest());
	}
	
	private static String toStringHex(String input,MessageDigest md){
		try {
			return Hex.encode(md.digest(input.getBytes(ConstantConfig.ENCODING)));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private static String toFileHex(File file,MessageDigest md){
		BufferedInputStream bufferedInputStream = null;
		try {
			int len = -1;
			byte[] buffer = new byte[BUFFERHEX];
			bufferedInputStream = new BufferedInputStream(new FileInputStream(file), BUFFERHEX);
			while ((len = bufferedInputStream.read(buffer)) != -1) {
				md.update(buffer, 0, len);
			}
			buffer = null;
			return Hex.encode(md.digest());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			if(bufferedInputStream!=null){
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					throw new IllegalArgumentException(e);
				}finally{
					bufferedInputStream=null;
				}
			}
		}
	}
	
}
