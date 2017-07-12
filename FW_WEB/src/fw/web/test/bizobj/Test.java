package fw.web.test.bizobj;

/**
 * Test entity. @author MyEclipse Persistence Tools
 */

public class Test implements java.io.Serializable {

	// Fields

	private Integer id;
	private String t1;
	private String t2;

	// Constructors

	/** default constructor */
	public Test() {
	}

	/** minimal constructor */
	public Test(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public Test(Integer id, String t1, String t2) {
		this.id = id;
		this.t1 = t1;
		this.t2 = t2;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getT1() {
		return this.t1;
	}

	public void setT1(String t1) {
		this.t1 = t1;
	}

	public String getT2() {
		return this.t2;
	}

	public void setT2(String t2) {
		this.t2 = t2;
	}

}