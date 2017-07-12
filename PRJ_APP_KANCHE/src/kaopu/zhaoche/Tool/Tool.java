package kaopu.zhaoche.Tool;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Tool {
	protected static Logger logger = Logger.getLogger(Tool.class);
	
	public static SimpleDateFormat date_format(int type){
		if(type == 1) return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(type == 2) return new SimpleDateFormat("yyyyMMdd");
		if(type == 3) return new SimpleDateFormat("yyyyMMddHHmmss");
		if(type == 4) return new SimpleDateFormat("yyyy-MM-dd");
		if(type == 5) return new SimpleDateFormat("yyMMdd");
		if(type == 6) return new SimpleDateFormat("yy-MM-dd HH:mm");
		if(type == 7) return new SimpleDateFormat("yyMMdd");
		if(type == 8) return new SimpleDateFormat("yyyy-M-d");
		if(type == 9) return new SimpleDateFormat("yyyy/MM/dd");
		if(type == 10) return new SimpleDateFormat("MM-dd HH:mm");
		if(type == 11) return new SimpleDateFormat("MMdd");
		if(type == 12) return new SimpleDateFormat("yyMM");
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public static int CheckPage(long total, int page, int limit){
		if(total > page*limit) return page;
		if(total%limit == 0) return (int) (total/limit - 1);
		return (int) (total/limit+1);
	}
	
	public static HashMap<String, String> DumpRequestCookie(HttpServletRequest request) {
		HashMap<String, String> map = new HashMap<String, String>(); 
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0){
	        for(Cookie c :cookies ){
	        	map.put(c.getName(), c.getValue());
	        }
        }
        return map;
	}
	
    public static void AddCookie(HttpServletResponse resp, String name, String value, int seconds, String domain){
    	Cookie cookie = new Cookie(name, value);
    	cookie.setMaxAge(seconds);
    	cookie.setDomain(domain);
    	resp.addCookie(cookie);
    }
    
    public static void RemoveCookie(HttpServletResponse resp, String name, String value, String domain){
    	Cookie cookie = new Cookie(name, value);
    	cookie.setMaxAge(0);
    	//cookie.setPath("/");
    	cookie.setDomain(domain);
    	resp.addCookie(cookie);
    }
	
	//随机挑选出若干的项
	public static ArrayList<Integer> RandomChoose(int total, double ratio){
		ArrayList<Integer> result = new ArrayList<Integer>();
		ArrayList<Integer> left = new ArrayList<Integer>();
		if(total == 0) return result;
		if(ratio == 0.0) return result;
		for(int i = 0; i < total; i++){
			left.add(i);
		}
		int count;
		double tmp = total*ratio;
		if(tmp < 1.0) count = 1;
		count = (int) tmp;
		if(count >= total) return left;
		Random random = new Random();
		while(count>0){
			int choice = random.nextInt(left.size());
			result.add(left.remove(choice));
			count--;
		}
		return result;
	}
	
	public static int DayDiff(Timestamp tStart, Timestamp tEnd){
		Timestamp start = Tool.ParseTime(date_format(4).format(tStart)+" 00:00:00");
		Timestamp end = Tool.ParseTime(date_format(4).format(tEnd)+" 00:00:00");
		
		int result = (int) ((end.getTime()-start.getTime())/(24*3600*1000L));
		return result;
	}
	
	public static double DoubleRound2(double v){
		BigDecimal bg = new BigDecimal(v);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static void PrintArgs(HashMap<String, String> args){
		Iterator<String> itr = args.keySet().iterator();
		while(itr.hasNext()){
			String key = itr.next();
			String value = args.get(key);
			logger.info(key+"="+value);
		}
	}
	
	
	public static String ParseSort(String v){
        if(v == null || v.length() == 0) return "";

        StringBuffer sb = new StringBuffer();
        try{
            JSONArray arr = new JSONArray(v);
            for(int i = 0; i < arr.length(); i++){
                JSONObject j = arr.getJSONObject(i);
                String property = j.getString("property");
                String direction = j.getString("direction");
                sb.append(property + " " + direction + ",");
            }
        }catch(Exception e){
            logger.error(Tool.GetStackTrace(e));
        }
        String result = sb.toString().trim();
        if(result.endsWith(",")) result = result.substring(0, result.length()-1);
        if(result.length() == 0) return "";
        return " ORDER BY "+result;
    }
	
	public static String PreparePath(String parent, String child){
		String dest = parent + File.separator + child;
		Tool.EnsureFolderExist(dest);
		return dest;
	}
	
	public static String FormatDate(Timestamp t){
		if(t == null) return "";
		return date_format(4).format(t);
	}
	
	public static String FormatTime(Timestamp t){
		if(t == null) return "";
		return date_format(1).format(t);
	}

	//获取与起始时间若干月的月末时间
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-01 00:00:00");
	public static Timestamp MonthEndOffseet(Timestamp tStart, int months){
		Calendar c = Calendar.getInstance();
		c.setTime(tStart);
		c.add(Calendar.MONTH, months+1);
		
		Timestamp t = new Timestamp(c.getTimeInMillis());
		String next2Month = df.format(t);
		Timestamp tNext2Month = Tool.ParseTime(next2Month);
		return new Timestamp(tNext2Month.getTime()-1);
	}
	
	public static String CreateChildFolder(String path, String child){
		if(!path.endsWith(File.separator)) path += File.separator;
		path += child;
		Tool.EnsureFolderExist(path);
		return path;
	}
	
	public static boolean FolderExist(String path) {
		File f = new File(path);
		if (f.exists() && f.isDirectory())
			return true;
		return false;
	}

	public static String ByteBufferToString(ByteBuffer buf) {
		buf.flip();
		if (buf.limit() == 0)
			return "";

		byte[] btTmp = new byte[buf.limit()];
		for (int i = 0; i < btTmp.length; i++) {
			btTmp[i] = buf.get();
		}
		String msg = new String(btTmp);
		return msg;
	}

	public static String ByteToHexString(byte[] src) {
		StringBuffer sb = new StringBuffer(src.length * 2);
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				sb.append(0);
			}
			sb.append(hv);
		}
		return sb.toString();
	}

	public static String GetStackTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}

	public static Timestamp GetLastMonthTime() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		return new Timestamp(cal.getTime().getTime());
	}

	public static Timestamp CurrentTimeOffset(Long offset) {
		return new Timestamp((new Date()).getTime() + offset);
	}

	public static Timestamp CurrentTime() {
		return new Timestamp((new Date()).getTime());
	}

	public static int GetWeekDay(Timestamp t) {
		Date d = new Date(t.getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w <= 0)
			w = 7;
		return w;
	}

	public static int GetHour(Timestamp t) {
		Date d = new Date(t.getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		return h;
	}

	public static String FilterOnlyOct(String src) {
		int nCount = src.length();
		String result = "";
		for (int i = 0; i < nCount; i++) {
			char c = src.charAt(i);
			if (c <= '9' && c >= '0') {
				result += c;
			}
		}
		return result;
	}

	public static boolean IsHexNum(String src) {
		int nCount = src.length();
		if (nCount == 0)
			return false;
		for (int i = 0; i < nCount; i++) {
			char tmp = src.charAt(i);
			if ((tmp < '0' || tmp > '9') && (tmp < 'a' && tmp > 'f') && (tmp < 'A' && tmp > 'F'))
				return false;
		}
		return true;
	}

	public static boolean IsOctNum(String src) {
		int nCount = src.length();
		for (int i = 0; i < nCount; i++) {
			char tmp = src.charAt(i);
			if ((tmp < '0' || tmp > '9')) {
				logger.info(src + ":" + i + "," + tmp);
				return false;
			}
		}
		return true;
	}

	public static Integer GetCurrencyFromSQLServer(ResultSet rs, String col) {
		try {
			return (int) (100 * ParseFloat(rs.getString(col)));
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public static Float ParseFloat(String s) {
		if (s == null)
			return (float) 0.0;
		int nPos = s.indexOf(".");
		if (nPos == -1 && nPos > s.length() - 3)
			return (float) 0.0;
		String tmp = s.substring(0, nPos + 2);
		try {
			Float result = new Float(tmp);
			return result;
		} catch (Exception e) {
			logger.error(e);
		}
		return (float) 0.0;
	}

	public static boolean FileExist(String path) {
		File f = new File(path);
		if (f.exists())
			return true;
		return false;
	}

	public static void Sleep(int milSeconds) {
		try {
			Thread.sleep(milSeconds);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static boolean RunBat(String path) {
		try {
			Runtime.getRuntime().exec(String.format("CMD /C START %s", path));
			return true;
		} catch (IOException e) {
			logger.error(e);
			return false;
		}
	}

	public static boolean IsJPEGFile(String path) {
		File f = new File(path);
		if (!f.exists() || !f.isFile())
			return false;

		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(f));
			byte[] buf = new byte[3];
			if (dis.read(buf) == 3) {
				if (buf[0] == (byte) 0xFF && buf[1] == (byte) 0xD8 && buf[2] == (byte) 0xFF) {
					dis.close();
					return true;
				}
			}
			dis.close();
		} catch (Exception e) {
			logger.error(e);
		}

		return false;
	}

	public static HashMap<String, String> DumpRequestParam(HttpServletRequest request, String tmpPath) throws Exception {
		return DumpRequestParam(request, "UTF-8", tmpPath);
	}

	public static HashMap<String, String> DumpRequestParam(HttpServletRequest request) {
		try {
			return DumpRequestParam(request, "UTF-8", null);
		} catch (Exception e) {
			logger.error(Tool.GetStackTrace(e));
			return new HashMap<String, String>();
		}
	}
	
	public static String getRemoteIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}
	
	private static void MapPut(HashMap<String, String> args, String name, String value){
		if(!args.containsKey(name)){
			args.put(name, value);
		}else{
			args.put(name, args.get(name)+","+value);
		}
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> DumpRequestParam(HttpServletRequest request, String encoding, String tmpPath) throws Exception {
		//tmpPath = MgSysConf.GetConfig("upload_temp");
		tmpPath = "upload_temp";
		Tool.EnsureFolderExist(tmpPath);

		HashMap<String, String> args = new HashMap<String, String>();
		//args.put("_remote_ip", getRemoteIP(request));
		args.put("_remote_ip", HttpUtil.getIpAddr(request));
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			// 导入POST参数
			Enumeration names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String p_name = (String) names.nextElement();
				StringBuffer p_value = new StringBuffer();
				String[] p_values = request.getParameterValues(p_name);
				if (p_values.length == 1) {
					p_value.append(p_values[0]);
				} else if (p_values.length > 1) {
					for (int i = 0; i < p_values.length; i++) {
						p_value.append(p_values[i]);
						if (i != p_values.length - 1) {
							p_value.append(",");
						}
					}
				} else {
					continue;
				}
				args.put(p_name, p_value.toString());
			}
		} else {
			// 如果有上传的文件
			DiskFileItemFactory Fpctory = new DiskFileItemFactory(1024 * 1024 * 10, new File(tmpPath));
			ServletFileUpload upload = new ServletFileUpload(Fpctory);
			upload.setHeaderEncoding(encoding);
			try {
				List<DiskFileItem> items = upload.parseRequest(request);
				for (int i = 0; i < items.size(); i++) {
					DiskFileItem item = items.get(i);

					String p_name = item.getFieldName();
					String p_value = item.getString(encoding);
					String origFilename = null;
					if (!item.isFormField()) {
						p_value = item.getStoreLocation().getPath();
						origFilename = item.getName();
						if (origFilename != null && origFilename.length() > 0) {
							origFilename = FilenameUtils.getName(origFilename);
							p_value += ".dat";
							File fBuf = new File(p_value);
							item.write(fBuf);
						} else {
							continue;
						}
					}
					MapPut(args, p_name, p_value);
					if (origFilename != null) {
						MapPut(args, p_name + "_orignal_filename", origFilename);
					}
				}
			} catch (FileUploadException e) {
				logger.error(e);
			}
		}
		return args;
	}

	@SuppressWarnings("unchecked")
	public static String GenJSONResult(boolean success, String comment, List tasks, Long total) {
		try {
			JSONObject j = new JSONObject();
			j.put("result", success ? 1 : 0);
			j.put("comment", comment);
			if (tasks == null || tasks.size() == 0) {
				j.put("totalProperty", 0);
			} else {
				if (total != null) {
					j.put("totalProperty", total);
				} else {
					j.put("totalProperty", tasks.size());
				}
				j.put("count", tasks.size());
				ArrayList<JSONObject> datas = new ArrayList<JSONObject>();
				for (int i = 0; i < tasks.size(); i++) {
					JSONObject tmp = new JSONObject();
					Object d = tasks.get(i);
					tmp.put("_seq", String.format("%d", i + 1));
					Method m[] = d.getClass().getDeclaredMethods();
					for (int k = 0; k < m.length; k++) {
						String name = m[k].getName();
						if (name.startsWith("get")) {
							Object value = m[k].invoke(d);
							if (value != null) {
								tmp.put(name.substring(3), m[k].invoke(d));
							} else {
								tmp.put(name.substring(3), "");
							}
						}
					}
					datas.add(tmp);
				}
				j.put("data", datas);
			}
			return j.toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static String GenJSONResult(boolean result, String comment, ArrayList<String> data) {
		try {
			JSONObject j = new JSONObject();
			if (result) {
				j.put("result", "SUCCESS");
			} else {
				j.put("result", "FAIL");
			}
			j.put("success", result);
			j.put("comment", comment);
			if(data != null){
				int nCount = data.size() / 2;
				for (int i = 0; i < nCount; i++) {
					j.put(data.get(i * 2), data.get(i * 2 + 1));
				}
			}
			return j.toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static void GenerateResult(HttpServletResponse response, String content) throws ServletException, IOException {
		// 生成文本
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			out.print(content);
		} catch (Exception e) {
			logger.error("生成JSON时报错", e);
			out.print("生成结果时失败");
		}
	}

	public static void GenerateResultWithType(HttpServletRequest request,HttpServletResponse response, String content, String type) throws ServletException, IOException {
		String v = request.getHeader("User-agent").toLowerCase();
		if(v.indexOf("msie") != -1 || v.indexOf("rv:11") > -1){
			response.setContentType("text/html;charset=UTF-8");
		}else{
			// 生成文本
			response.setContentType(type+";charset=UTF-8");
		}
		PrintWriter out = response.getWriter();

		try {
			out.print(content);
		} catch (Exception e) {
			logger.error("生成JSON时报错", e);
			out.print("生成结果时失败");
		}
	}

	public static void GenerateHtmlResult(HttpServletResponse response, String content) throws ServletException, IOException {
		GenerateHtmlResult(response, content, "UTF-8");
	}

	public static void GenerateHtmlResult(HttpServletResponse response, String content, String encoding) throws ServletException, IOException {
		// 生成文本
		response.setContentType("text/html;charset=" + encoding);
		PrintWriter out = response.getWriter();

		try {
			out.print(content);
		} catch (Exception e) {
			logger.error("生成JSON时报错", e);
			out.print("生成结果时失败");
		}
	}

	public static void GenerateXmlResult(HttpServletResponse response, String content) throws ServletException, IOException {
		// 生成文本
		response.setContentType("application/xml;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			out.print(content);
		} catch (Exception e) {
			logger.error("生成JSON时报错", e);
			out.print("生成结果时失败");
		}
	}

	public static void GenerateJsonResult(HttpServletRequest request,HttpServletResponse response, boolean result, String comment) throws ServletException, IOException {
		String v = request.getHeader("User-agent").toLowerCase();
		if(v.indexOf("msie") != -1 || v.indexOf("rv:11") > -1){
			response.setContentType("text/html;charset=UTF-8");
		}else{
			// 生成文本
			response.setContentType("application/json;charset=UTF-8");
		}
		PrintWriter out = response.getWriter();

		try {
			out.println(Tool.GenJSONResult(result, comment));
		} catch (Exception e) {
			logger.error("生成JSON时报错", e);
			out.println("生成结果时失败");
		}
	}

	public static String GenJSONResult(boolean result, String comment) {
		try {
			JSONObject j = new JSONObject();
			if (result) {
				j.put("result", "SUCCESS");
			} else {
				j.put("result", "FAIL");
			}
			j.put("success", result);
			j.put("comment", comment);

			return j.toString();
		} catch (Exception e) {
			logger.info(Tool.GetStackTrace(e));
			return "";
		}
	}

	public static Timestamp ParseTime(String value) {
		Timestamp result = null;
		try {
			Date d = date_format(1).parse(value);
			return new Timestamp(d.getTime());
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	public static Timestamp ParseTimeExcel(String value) {
		Timestamp result = null;
		try {
			Date d = date_format(8).parse(value);
			return new Timestamp(d.getTime());
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	public static Timestamp ParseTimeSlash(String value) {
		Timestamp result = null;
		try {
			Date d = date_format(9).parse(value);
			return new Timestamp(d.getTime());
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	public static Timestamp ParseTimeShort(String value) {
		Timestamp result = null;
		try {
			Date d = date_format(4).parse(value);
			return new Timestamp(d.getTime());
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	public static long ParseLong(String v) {
		if (v == null || v.length() == 0)
			return 0;
		try {
			long tmp = Long.parseLong(v);
			return tmp;
		} catch (Exception e) {
			return 0L;
		}
	}

	public static int ParseInt(String v) {
		if (v == null || v.length() == 0)
			return 0;
		try {
			int tmp = Integer.parseInt(v);
			return tmp;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static double parseDouble(String v, double defaultValue) {
		if (v == null || v.length() == 0) return defaultValue;
		try {
			return Double.parseDouble(v);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static double parseDouble(String v) {
		return parseDouble(v, 0.0);
	}

	public static void DeletePath(String path) {
		if (path == null || path.length() == 0)
			return;
		File fPath = new File(path);
		if (fPath.isFile()) {
			fPath.delete();
			return;
		}
		File[] all = fPath.listFiles();
		if (all == null)
			return;
		for (int i = 0; i < all.length; i++) {
			Tool.DeletePath(all[i].getPath());
		}
		fPath.delete();
	}

	public static String GetHalfHourStr(Date d) {
		java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("mm");
		int nMin = Integer.parseInt(fmt.format(d));
		fmt = new java.text.SimpleDateFormat("HH");
		int nHour = Integer.parseInt(fmt.format(d));
		if (nMin < 30) {
			return String.format("%02d", nHour);
		} else {
			return String.format("%02d.5", nHour);
		}
	}

	public static String copyFile(String source, String dest) {
		String result = null;
		// 类型相同
		File readFile = new File(source);
		File writeFile = new File(dest);

		// 判断源文件是否存在
		if (readFile.exists() && readFile.isFile()) {
			// 如果目标文件已经存在，询问是否覆盖
			if (writeFile.exists()) {
				// 将目标文件进行备份
				writeFile.delete();
			}
			FileInputStream readStream = null;
			FileOutputStream writeStream = null;
			try {
				readStream = new FileInputStream(readFile);
				writeStream = new FileOutputStream(writeFile);
				byte[] btTemp = new byte[1024 * 1024];
				while (readStream.available() > 0) {
					int len = readStream.read(btTemp);
					writeStream.write(btTemp, 0, len);
				}
			} catch (IOException ioErr) {
				result = "源文件" + source + "读取错误！请检查文件";
			} catch (Exception err) {
				result = "文件" + source + "拷贝错误！请检查目标文件" + dest + "内容是否正确！";
			} finally {
				try {
					if (readStream != null)
						readStream.close();
					if (writeStream != null)
						writeStream.close();
				} catch (IOException ioErr) {
					result = "拷贝文件" + source + "到" + dest + "失败，未知原因。";
				}
			}
		}
		return result;
	}

	public static String moveFile(String source, String dest) {
		String result = null;
		result = copyFile(source, dest);
		if (result == null) {
			try {
				File file = new File(source);
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
				result = "删除源文件失败";
			}
		} else {
			logger.error(result);
		}
		return result;
	}

	public static void ReplaceStringInFileUTF8(File file, ArrayList<String> replace) throws Exception {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = reader.readLine(); // 读取第一行
		while (line != null) { // 如果 line 为空说明读完了
			buffer.append(line); // 将读到的内容添加到 buffer 中
			buffer.append("\r\n"); // 添加换行符
			line = reader.readLine();
		}
		reader.close();
		String result = buffer.toString();

		for (int i = 0; i < replace.size() / 2; i++) {
			String to = replace.get(i * 2 + 1);
			if (to == null)
				to = "";
			result = result.replace(replace.get(i * 2), to);
		}
		java.io.BufferedWriter writer = null;
		java.io.FileOutputStream writerStream = new java.io.FileOutputStream(file);
		writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(writerStream, "utf-8"));
		writer.write(result);
		writer.close();
	}

	public static void WriteStringToFile(String content, String path) throws Exception {
		WriteStringToFile(content, path, null);
	}

	public static void WriteStringToFile(String content, String path, String encoding) throws Exception {
		java.io.BufferedWriter writer = null;
		java.io.FileOutputStream writerStream = new java.io.FileOutputStream(new File(path));
		if (encoding != null) {
			writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(writerStream, encoding));
		} else {
			writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(writerStream));
		}
		writer.write(content);
		writer.close();
	}

	public static void ReplaceStringInFile(File file, ArrayList<String> replace) throws Exception {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = reader.readLine(); // 读取第一行
		while (line != null) { // 如果 line 为空说明读完了
			buffer.append(line); // 将读到的内容添加到 buffer 中
			buffer.append("\r\n"); // 添加换行符
			line = reader.readLine();
		}
		reader.close();
		String result = buffer.toString();
		for (int i = 0; i < replace.size() / 2; i++) {
			String to = replace.get(i * 2 + 1);
			if (to == null)
				to = "";
			result = result.replace(replace.get(i * 2), to);
		}
		java.io.BufferedWriter writer = null;
		java.io.FileOutputStream writerStream = new java.io.FileOutputStream(file);
		writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(writerStream));
		writer.write(result);
		writer.close();
	}

	public static void EnsureFolderExist(String path) {
		try {
			File fTemp = new File(path);
			if (fTemp.exists() && !fTemp.isDirectory())
				fTemp.delete();
			if (!fTemp.exists())
				fTemp.mkdir();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static double EARTH_RADIUS = 6378137;// 地球半径(米)

	public static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	// 计算结果单位为米
	public static double GetDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cosh(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s);
		return s;
	}
	
	public static String GetDirection(double lat1, double lng1, double lat2, double lng2) {
		String result = "";
		if(lat1>lat2){
			result += "东"; 
		}else{
			result += "西";
		}
		if(lng1>lng2){
			result += "南"; 
		}else{
			result += "北";
		}
		return result;
	}

	public static HashMap<Long, String> action_result_msg = new HashMap<Long, String>();
	public static HashMap<Long, Integer> action_result_code = new HashMap<Long, Integer>();

	public static void SetActionResult(long result_id, boolean bSuccess, String msg) {
		Tool.action_result_code.put(result_id, bSuccess ? 1 : 0);
		Tool.action_result_msg.put(result_id, msg);
	}

	public static int FilterNullInteger(Integer v) {
		if (v == null)
			return 0;
		return v.intValue();
	}

	public static String FilterNull(Object v) {
		if (v == null) return "";
		if(v.getClass().getName().indexOf("Timestamp") > 0) return Tool.date_format(1).format(v);
		return v.toString().trim();
	}

	public static String FilterNullTimestamp(Timestamp v) {
		if (v == null)
			return "";
		return Tool.date_format(1).format(v);
	}
	
	public static String FilterNullDate(Timestamp v) {
		if (v == null) return "";
		return Tool.date_format(4).format(v);
	}

	// 分解每个参数，并trim
	public static String[] SplitString(String value) {
		String[] result = {};
		if (value == null || value.trim().length() == 0) {
			return result;
		}
		result = value.split(",");
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim();
		}
		return result;
	}

	// 检查字符串非空
	public static boolean CheckStringNotNullOrEmpty(String value) {
		if (value == null || value.trim().length() == 0) {
			return false;
		}
		return true;
	}

	// 检查若干个字符串不同时为空
	public static boolean CheckMultiStringNotAllNullOrEmpty(String... values) {
		for (String value : values) {
			if (CheckStringNotNullOrEmpty(value))
				return true;
		}
		return false;
	}

	// 生成随机码
	public static String RandomString(int nCount) {
		if (nCount <= 0)
			return "";
		StringBuffer buf = new StringBuffer();
		String[] config = { "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "K", "M", "N", "P", "R", "S", "T", "U", "V", "W",
				"X", "Y", "Z" };
		Random r = new Random();
		for (int i = 0; i < nCount; i++) {
			buf.append(config[r.nextInt(config.length - 1)]);
		}
		return buf.toString();
	}

	// 生成随机码
	public static String RandomStringDigit(int nCount) {
		if (nCount <= 0)
			return "";
		StringBuffer buf = new StringBuffer();
		String[] config = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		Random r = new Random();
		for (int i = 0; i < nCount; i++) {
			buf.append(config[r.nextInt(config.length - 1)]);
		}
		return buf.toString();
	}

	public static String ReadFileContentUtf8(String filename) {
		try {
			return Tool.FilterNull(ReadFileContent(filename, "UTF-8"));
		} catch (Exception e) {
			logger.error(Tool.GetStackTrace(e));
			return "";
		}
	}

	public static String ReadFileContent(String filename, String encoding){
		try{
			FileInputStream fis = new FileInputStream(filename);
			StringBuffer result = new StringBuffer();
			DataInputStream in = new DataInputStream(fis);
			BufferedReader d = null;
			if (encoding == null) {
				d = new BufferedReader(new InputStreamReader(in));
			} else {
				d = new BufferedReader(new InputStreamReader(in, encoding));
			}
			while (true) {
				String temp = d.readLine();
				if (temp == null)
					break;
				result.append(temp + "\r\n");
			}
			d.close();
			in.close();
			fis.close();
			return result.toString();
		}catch(Exception e){
			logger.error(Tool.GetStackTrace(e));
			return null;
		}
	}

	public static String GetHQLWhereFree(String name, String c_id) {
		if (name.compareTo("AppZhanqi") == 0) {
			return String.format("contractId='%s'", c_id);
		} else if (name.compareTo("AppHuanka") == 0) {
			return String.format("contractId='%s'", c_id);
		} else if (name.compareTo("AppXubao") == 0) {
			return String.format("contractId='%s'", c_id);
		} else if (name.compareTo("Fpq") == 0) {
			return "";
		} else if (name.compareTo("AppLipei") == 0) {
			return String.format("contractId='%s'", c_id);
		} else if (name.compareTo("DevMsg") == 0) {
			return String.format("contractId='%s'", c_id);
		} else {
			return " 1=2 ";
		}
	}
	
	// 计算两个时间之间相差的月份
	public static int CalcMonthDiff(Timestamp tStart, Timestamp tEnd) {
		Calendar cEnd = Calendar.getInstance();
		cEnd.setTime(tEnd);
		Calendar cStart = Calendar.getInstance();
		cStart.setTime(tStart);
		int nMonth = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH);
		return nMonth;
	}

	// 计算两个时间之间相差的天数
	public static int CalcDayDiff(Timestamp tStart, Timestamp tEnd) {
		return (int) ((tEnd.getTime() - tStart.getTime()) / (1000L * 3600 * 24));
	}

	// 把文件转成base64字符串
	public static String encodeBase64File(String path) throws Exception {
		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return new BASE64Encoder().encode(buffer);

	}

	// 把一个base64字符串转成一个文件
	public static void decoderBase64File(String base64Code, String targetPath) throws Exception {
		byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
		FileOutputStream out = new FileOutputStream(targetPath);
		out.write(buffer);
		out.close();
	}

	
	//将某个文件强制转换成UTF-8格式
	public static void ConvertFileToUTF8(String path, String checkWord){
		try {
			String content = Tool.ReadFileContent(path, "UTF-8");
			if(content.indexOf(checkWord) >= 0 ) return;
			content = Tool.ReadFileContent(path, "GBK");
			if(content.indexOf(checkWord) >= 0 ){
				Tool.WriteStringToFile(content, path, "UTF-8");
				logger.info(String.format("已转换文件为UTF-8文件:%s", path));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
