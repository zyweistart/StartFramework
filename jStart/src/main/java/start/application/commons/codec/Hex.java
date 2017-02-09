package start.application.commons.codec;

public class Hex {

	/**
	 * 字节数组转十进制
	 */
	public static String encode(byte[] b) {
		String str = "";
		String stmp = "";
		int length = b.length;
		for (int n = 0; n < length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				str = str + "0" + stmp;
			} else {
				str = str + stmp;
			}
			if (n < length - 1) {
				str = str + "";
			}
		}
		return str.toLowerCase();
	}
	
}
