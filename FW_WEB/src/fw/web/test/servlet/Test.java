package fw.web.test.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import fw.web.Tool.Tool;
import fw.web.test.service.TestService;

@Controller
@Scope("singleton")
public class Test extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(Test.class);
	
	@Autowired
	private TestService testService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		HashMap<String, String> args = null;
		try {
			args = Tool.DumpRequestParam(request);
		} catch (Exception e) {
			Tool.GenerateJsonResult(request,response, false, "解析参数失败");
			return;
		}
		String result = "";
		
		String action = Tool.FilterNull(args.get("_action"));
		if(action.compareTo("test") == 0){
			try {
				result = testService.findAll();
			} catch (Exception e) {
				logger.error(Tool.GetStackTrace(e));
				result = "FAIL,操作异常";
			}
		}else{
			result = "FAIL,不支持的动作："+action;
		}
		
		// 结果输出
		if (result.startsWith("FAIL")) {
			result = Tool.GenJSONResult(false, result.substring(5));
		} else if (result.startsWith("OK")) {
			result = Tool.GenJSONResult(true, result.substring(3));
		} else if (result.startsWith("JSON")) {
			result = result.substring(5);
		}
		
		String cb = Tool.FilterNull(args.get("callback"));
		if (cb.length() > 0) {
			Tool.GenerateResultWithType(request,response, cb + "(" + result + ");",
					"text/javascript");
		} else {
			Tool.GenerateResultWithType(request,response, result, "application/json");
		}
	}

	public TestService getTestService() {
		return testService;
	}

	
	public void setTestService(TestService testService) {
		this.testService = testService;
	}
	
	
}
