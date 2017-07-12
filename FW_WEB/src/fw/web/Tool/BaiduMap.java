package fw.web.Tool;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaiduMap {
	static Logger logger = Logger.getLogger(BaiduMap.class);
	static String baidu_ak = "MFTT0aE2Efwndv86KVbgAWiW";				//应用编号
	static String baidu_sk = "gkFnYF8pTz7zwUg4GadpFPjyCmPkjvu9";	//对应应用的安全码
	static String baidu_url_conv = "http://api.map.baidu.com/geoconv/v1/";
	static String baidu_url_geo = "http://api.map.baidu.com/geocoder/v2/";

	public static void main(String[] args) {
		try {
			double lat = 22.577552;
			double lng = 114.041040;
			Double[] baiduPos  = ConvertGPS(lat, lng);
			ConvertToAddress(baiduPos[0], baiduPos[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 原始：22.57755200	114.04104000	转换后：	22.58056726	114.05269740
	//	进行坐标转换
	// http://api.map.baidu.com/geoconv/v1/?coords=114.21892734521,29.575429778924&from=1&to=5&ak=你的密钥
	@SuppressWarnings("unchecked")
	public static Double[] ConvertGPS(Double gpsLat, Double gpsLng){
		Double[] result = {gpsLat, gpsLng};
		
		try{
			//生成sn，读取百度
			String coord = String.format("%f,%f", gpsLng, gpsLat);
			Map paramsMap = new LinkedHashMap<String, String>();
			paramsMap.put("coords", coord);
			paramsMap.put("from", "1");
			paramsMap.put("to", "5");
			paramsMap.put("output", "json");
			paramsMap.put("ak", baidu_ak);
			String paramsStr = BaiduSnCalculator.toQueryString(paramsMap);
			String wholeStr = new String("/geoconv/v1/?" + paramsStr + baidu_sk);
			String tempStr = URLEncoder.encode(wholeStr, "UTF-8");
			String sn = BaiduSnCalculator.MD5(tempStr);
			String param = String.format("%s&sn=%s", paramsStr, sn);
			String tmp = HttpUtil.Get(baidu_url_conv, param);
			if(tmp == null || tmp.length() == 0) return result;
			
			//使用JSON解析
			//成功：{"status":0,"result":[{"x":114.23075627686,"y":29.579087871023}]}
			//失败：{"status":211,"message":"APP SN鏍￠獙澶辫触"}
			JSONObject j = new JSONObject(tmp);
			String status = Tool.FilterNull(j.get("status"));
			if(status.compareTo("0") == 0){
				JSONArray jCoord = j.getJSONArray("result");
				if(jCoord != null && jCoord.length() > 0){
					JSONObject jTmp = (JSONObject) jCoord.get(0);
					Double lat = jTmp.getDouble("y");
					Double lng = jTmp.getDouble("x");
					result[0] = lat;
					result[1] = lng;
				}else{
					logger.error("格式有误："+tmp);
				}
			}else{
				logger.info(baidu_url_conv + "?" +param);
				logger.info("返回失败: "+tmp);
			}
		}catch(Exception e){
			logger.error("解析百度返回值错误:"+e.getMessage());
			logger.error(Tool.GetStackTrace(e));
		}
		
		return result;
	}
	
	//	逆向地址解析
	// http://api.map.baidu.com/geocoder/v2/?ak=E4805d16520de693a3fe707cdc962045&callback=renderReverse&location=39.983424,116.322987&output=json&pois=1
	@SuppressWarnings("unchecked")
	public static String[] ConvertToAddress(Double baiduLat, Double baiduLng) throws Exception{
		String[] result = {"", "", "", ""};			//省，市，区，地址
		
		//生成sn，读取百度
		String coord = String.format("%f,%f", baiduLat, baiduLng);
		Map paramsMap = new LinkedHashMap<String, String>();
		paramsMap.put("coordtype", "bd09ll");
		paramsMap.put("location", coord);
		paramsMap.put("pois", "0");
		paramsMap.put("output", "json");
		paramsMap.put("ak", baidu_ak);
		String paramsStr = BaiduSnCalculator.toQueryString(paramsMap);
		String wholeStr = new String("/geocoder/v2/?" + paramsStr + baidu_sk);
		String tempStr = URLEncoder.encode(wholeStr, "UTF-8");
		String sn = BaiduSnCalculator.MD5(tempStr);
		String param = String.format("%s&sn=%s", paramsStr, sn);
		String tmp = HttpUtil.Get(baidu_url_geo, param);
		if(tmp == null || tmp.length() == 0) return result;
		
		//使用JSON解析
		//成功：{"status":0,"result":[{"x":114.23075627686,"y":29.579087871023}]}
		//失败：{"status":211,"message":"APP SN校验失败"}
		try{
			JSONObject j = new JSONObject(tmp);
			String status = Tool.FilterNull(j.get("status"));
			if(status.compareTo("0") == 0){
				JSONObject jResult = j.getJSONObject("result");
				if(jResult != null){
					result[3] = Tool.FilterNull(jResult.get("formatted_address"));
					JSONObject jDetail = jResult.getJSONObject("addressComponent");
					if(jDetail != null){
						result[0] = Tool.FilterNull(jDetail.get("province"));
						result[1] = Tool.FilterNull(jDetail.get("city"));
						result[2] = Tool.FilterNull(jDetail.get("district"));
					}else{
						logger.error("格式有误："+tmp);
					}
				}else{
					logger.error("格式有误："+tmp);
				}
			}else{
				logger.info(baidu_url_geo + "?" +param);
				logger.error("返回失败: "+tmp);
			}
		}catch(Exception e){
			logger.error(e);
			e.printStackTrace();
		}
		
		return result;
	}
}
