package kaopu.zhaoche.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

public class MD5 {
	static Logger logger = Logger.getLogger(MD5.class);
	
	/**
	 * 方法1
	 * 
	 * @param source
	 * @return 16进制整数字符串格式
	 */
	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			// MD5 的计算结果是一个 128 位的长度整数，
			byte tmp[] = md.digest();

			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
				// >>> 为逻辑右移（即无符号右移），将符号位一起右移

				// 取字节中低 4 位的数字转换
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (Exception e) {
			logger.error(e);
		}
		return s;
	}
	
	
	/**
	 * 方法2
	 * 将字符串MD5加密，返回MD5字符串
	 * @param source
	 * @return passwordMD5字符串
	 */
	public static String getMD5(String source) {
		String rsmd5 = getMD5(source.getBytes());
		return rsmd5;
	}
	
	private static byte md5_btTemp[] = new byte[10240];
	public static String getFileMD5(String path){
		File f = new File(path);
		if(!f.exists() || f.isDirectory()) return "";

		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		
		FileInputStream in = null;
		try{
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			int nLeft = (int) f.length();
			in = new FileInputStream(f);
			while(nLeft > 0){
				int nRead = in.read(md5_btTemp);
				md.update(md5_btTemp, 0, nRead);
				nLeft -= nRead;
			}
			in.close();
			in = null;

			// MD5 的计算结果是一个 128 位的长度整数，
			byte tmp[] = md.digest();

			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
				// >>> 为逻辑右移（即无符号右移），将符号位一起右移

				// 取字节中低 4 位的数字转换
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str); // 换后的结果转换为字符串
		}catch(Exception e){
			logger.error(e);
		}finally{
			if(in != null)
	      try {
	        in.close();
	        in = null;
        } catch (IOException e) {
        	logger.error(e);
        }
		}
		return s;
	}
}