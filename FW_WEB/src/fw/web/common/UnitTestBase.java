package fw.web.common;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

public class UnitTestBase {

	private ClassPathXmlApplicationContext context;
	
	private String springXmlpath;
	
	public UnitTestBase(){}
	
	public UnitTestBase(String springXmlpath){
		this.springXmlpath = springXmlpath;
	}
	
	//@Before
	public void before(){
		if(StringUtils.isEmpty(springXmlpath)){
			springXmlpath = "classpath*:application-*.xml";
		}
		try {
			context = new ClassPathXmlApplicationContext(springXmlpath.split("[.\\s]+"));
			context.start();
		} catch (BeansException e) {
			e.printStackTrace();			
		}
	}
	
	
	public void after(){
		
	}
	
	
	
	
}
