package kaopu.zhaoche.Tool;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {
	
	public static String GetString(JSONObject j, String name){
		try {
			return j.getString(name);
		} catch (JSONException e) {
			return "";
		}
	}
	
	public static int GetInt(JSONObject j, String name, int deFpultValue){
		try {
			return j.getInt(name);
		} catch (JSONException e) {
			return deFpultValue;
		}
	}
	
	public static double GetDouble(JSONObject j, String name, double deFpultValue){
		try {
			return j.getDouble(name);
		} catch (JSONException e) {
			return deFpultValue;
		}
	}
}
