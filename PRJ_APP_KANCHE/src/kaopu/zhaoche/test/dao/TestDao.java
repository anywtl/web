package kaopu.zhaoche.test.dao;

import java.util.List;

import kaopu.zhaoche.test.bizobj.Test;

public interface TestDao {

	public List<Test> getAll(String hql);
}
