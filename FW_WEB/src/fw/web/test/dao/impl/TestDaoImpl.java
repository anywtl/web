package fw.web.test.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import fw.web.common.HibernateBaseTemplate;
import fw.web.test.bizobj.Test;
import fw.web.test.dao.TestDao;


@Repository(value="testDao")
public class TestDaoImpl extends HibernateBaseTemplate<Test> implements TestDao{

	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<Test> getAll(String hql) {
		List<Test> list =  (List<Test>) this.getHibernateTemplate().find(hql);
		if(!list.isEmpty()){
			return list;
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	public int u1(String hql) {
		Query query = this.getSession().createQuery(hql);
		int nCount = query.executeUpdate();
		return nCount;
	}
	
	@SuppressWarnings("deprecation")
	public int u2(String hql) {
		Query query = this.getSession().createQuery(hql);
		int nCount = query.executeUpdate();
		return nCount;
	}

	@SuppressWarnings("deprecation")
	public int add1(Test test) {
		this.getHibernateTemplate().save(test);
		return 0;
	}

}
