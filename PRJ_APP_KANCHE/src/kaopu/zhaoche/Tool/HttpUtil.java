package kaopu.zhaoche.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import java.net.URLConnection;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;


public class HttpUtil {
	private static Logger logger = Logger.getLogger(HttpUtil.class);

	//常规的HTTP GET
	public static String Get(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error("发送GET请求出现异常！" + e);
			logger.error(Tool.GetStackTrace(e));
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				logger.error(Tool.GetStackTrace(e2));
			}
		}
		return result;
	}

	//常规的HTTP POST
	public static String Post(String url, Map<String, String> params) {
		URL u = null;
		HttpURLConnection con = null;

		// 构建请求参数
		StringBuffer sb = new StringBuffer();
		String param = "";
		if (params != null) {
			for (Entry<String, String> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
			param = sb.toString();
			param = param.substring(0, sb.length() - 1);
		}

		// 尝试发送请求
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("contentType", "UTF-8");
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
			osw.write(param);
			osw.flush();
			osw.close();
		} catch (Exception e) {
			logger.error(Tool.GetStackTrace(e));
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}

		// 读取返回内容
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
			}
		} catch (Exception e) {
			logger.error(Tool.GetStackTrace(e));
		}
		return buffer.toString();
	}
	
	//将二进制文件整个通过POST发送给服务器
	public static String PostFile(String urlString, String filePath) throws IOException {
		File fSrc = new File(filePath);
		if(!fSrc.exists()) return "FAIL,文件不存在";
		
		// 发起post请求
		URL connectURL = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "binary/octet-stream");
		OutputStream outStream = conn.getOutputStream();
		
		//读取文件并写入到HTTP POST中
		FileInputStream fileInputStream = new FileInputStream(fSrc);
		int bytesAvailable = fileInputStream.available();
		int maxBufferSize = 1024*10;
		int bufferSize = Math.min(bytesAvailable, maxBufferSize);
		byte[] buffer = new byte[bufferSize];

		//读取到缓存并写入
		int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		while (bytesRead > 0) {
			outStream.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		}
		outStream.flush();
		outStream.close();

		// 接收发起请求后由服务端返回的结果
		int read;
		StringBuffer inputb = new StringBuffer();
		InputStream is = conn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
		while ((read = inputStreamReader.read()) >= 0) {
			inputb.append((char) read);
		}
		return inputb.toString();
	}
	
	//将客户POST上来的二进制文件保存下来
	public static void SavePostDataToFile(HttpServletRequest request, String destPath) throws Exception{
		FileOutputStream fos = new FileOutputStream(destPath);
		ServletInputStream sis = request.getInputStream();
		int buffer_size = 10124*10;
		byte[] buf = new byte[buffer_size];
		while(true){
			int nRead = sis.read(buf);
			if(nRead < 0) break;
			fos.write(buf, 0, nRead);
		}
		fos.close();
		sis.close();
	}

	//常规的HTTP POST
	public static String PostString(String url, String content, String encoding) {
		return PostString(url, content, encoding, encoding);
	}

	//常规的HTTP POST
	public static String PostString(String url, String content, String encoding, String retEncoding) {
		URL u = null;
		HttpURLConnection con = null;

		// 尝试发送请求
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), encoding);
			osw.write(content);
			osw.flush();
			osw.close();
		} catch (Exception e) {
			logger.error(Tool.GetStackTrace(e));
		}
		if(con == null) return null;

		// 读取返回内容
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), retEncoding));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
			}
		} catch (Exception e) {
			logger.error(Tool.GetStackTrace(e));
			return null;
		}
		return buffer.toString();
	}
	
	public static String DownloadToFile(String urlPath, String path, String filename){
		URL url;
		try {
			//打开连接
			url = new URL(urlPath);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			//看远程文件是否存在
			if(con.getResponseCode() != 200) return "1-远程文件不存在(code="+con.getResponseCode()+")";
			
			//看看本地路径是否存在
			if(!Tool.FolderExist(path)) return "2-本地目录不存在";
			if(Tool.FileExist(path+File.separator+filename)){
				Tool.DeletePath(path+File.separator+filename);
			}
			
			//如果存在则下载之
			InputStream is = con.getInputStream();
			byte[] bs = new byte[1024*10];
			int len;
			OutputStream os = new FileOutputStream(path+File.separator+filename);
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			os.flush();
			os.close();
			is.close();
			return "0-下载成功";
		} catch (Exception e) {
			logger.error(Tool.GetStackTrace(e));
			return "9-下载报错:"+e.getMessage();
		}
	}

	public static String PostMethod(String url,String jsonData) throws IOException{
		String responseStr="";
		try
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(url);
			StringEntity input = new StringEntity(jsonData);
			input.setContentType("application/json");
			postRequest.setEntity(input);
			
			HttpResponse response = httpClient.execute(postRequest);
			if (response.getStatusLine().getStatusCode() != 200){
				throw new RuntimeException("Fpiled : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
				
			}
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				responseStr+=output;
			}
			httpClient.getConnectionManager().shutdown();
		}catch (MalformedURLException e){
			logger.error(Tool.GetStackTrace(e));
		}catch (IOException e) {
			logger.error(Tool.GetStackTrace(e));
		}
		return responseStr;
		
		
	}
	
	/**
	 * IP地址是否有效.
	 * 
	 * @param remoteAddr IP地址
	 * @return true代表IP地址有效，false代表IP地址无效
	 */
	private static boolean isEffective(String remoteAddr) {
		boolean isEffective = false;
		if ((null != remoteAddr) && (!"".equals(remoteAddr.trim()))
				&& (!"unknown".equalsIgnoreCase(remoteAddr.trim()))) {
			isEffective = true;
		}
		return isEffective;
	}
	
	/****
	 * 根据HTTP请求获取请求客户端的IP地址
	 * @param request
	 * @return
	 * @author WANG-TIANLONG
	 */
	public static String getIpAddr(HttpServletRequest request) {        
        try{
			String ip = request.getHeader("X-Forwarded-For");
			// 如果通过多级反向代理，X-Forwarded-For的值不止一个，而是一串用逗号分隔的IP值，此时取X-Forwarded-For中第一个非unknown的有效IP字符串
			if (isEffective(ip) && (ip.indexOf(",") > -1)) {
				String[] array = ip.split(",");
				for (String element : array) {
					if (isEffective(element)) {
						ip = element;
						break;
					}
				}
			}
			if (!isEffective(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (!isEffective(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (!isEffective(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (!isEffective(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (!isEffective(ip)) {
				ip = request.getRemoteAddr();
			}
			
			return ip;
		}catch(Exception e){
			logger.error(Tool.GetStackTrace(e));
			return "";
		}
    }
}