package fw.web.test.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fw.web.test.bizobj.Test;
import fw.web.test.dao.TestDao;
import fw.web.test.service.TestService;


@Service(value="testService")
public class TestServiceImpl implements TestService {

	static Logger logger = Logger.getLogger(TestServiceImpl.class);
	
	@Autowired
	private TestDao testDao;
	
	@Transactional(propagation=Propagation.REQUIRED)
	public String findAll() {
//		try {
//
//		} catch (Exception e) {
//			logger.error(Tool.GetStackTrace(e));
//			return "FAIL,测试异常请重试";
//		}
		
//		List<Test> t = testDao.getAll(" from Test ");
		Test tx = new Test();
		tx.setT1("T16");
		tx.setT2("T26");
		testDao.add1(tx);
		
		int t = testDao.u1(" UPDATE Test SET t1='T999' where id=1 ");
		logger.info(t);
//		int t2 = testDao.u2(" UPDATE Test SET t1=null where id=2 ");
//		logger.info(t2);
		return "OK,测试成功";
	}

	public TestDao getTestDao() {
		return testDao;
	}

	public void setTestDao(TestDao testDao) {
		this.testDao = testDao;
	}
	
}
