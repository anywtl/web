package kaopu.zhaoche.test.dao.impl;

import java.util.List;

import kaopu.zhaoche.common.HibernateBaseTemplate;
import kaopu.zhaoche.test.bizobj.Test;
import kaopu.zhaoche.test.dao.TestDao;

import org.springframework.stereotype.Repository;

@Repository(value="testDao")
public class TestDaoImpl extends HibernateBaseTemplate<Test> implements TestDao{

	@SuppressWarnings("unchecked")
	public List<Test> getAll(String hql) {
		List<Test> list =  this.getHibernateTemplate().find(hql);
		if(!list.isEmpty()){
			return list;
		}
		return null;
	}

}
