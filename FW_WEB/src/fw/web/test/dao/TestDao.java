package fw.web.test.dao;

import java.util.List;

import fw.web.test.bizobj.Test;

public interface TestDao {

	public List<Test> getAll(String hql);
	
	public int add1(Test test);
	
	public int u1(String hql);
	
	public int u2(String hql);
}
