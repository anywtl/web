package kaopu.zhaoche.test.service.impl;

import java.util.List;

import javax.annotation.Resource;

import kaopu.zhaoche.Tool.Tool;
import kaopu.zhaoche.test.bizobj.Test;
import kaopu.zhaoche.test.dao.TestDao;
import kaopu.zhaoche.test.service.TestService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value="testService")
public class TestServiceImpl implements TestService {

	static Logger logger = Logger.getLogger(TestServiceImpl.class);
	
	@Resource
	private TestDao testDao;
	
	public String findAll() {
		try {
			List<Test> t = testDao.getAll(" from Test ");
			logger.info(t.size());
			return "OK,测试成功";
		} catch (Exception e) {
			logger.error(Tool.GetStackTrace(e));
			return "FAIL,测试异常请重试";
		}
	}

	public TestDao getTestDao() {
		return testDao;
	}

	public void setTestDao(TestDao testDao) {
		this.testDao = testDao;
	}
	
}
