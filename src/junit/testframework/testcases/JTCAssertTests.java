package testframework.testcases;

import testframework.AbstractJamochaTest;

public class JTCAssertTests extends AbstractJamochaTest {

	public JTCAssertTests(String arg0) {
		super(arg0);
	}
	
	private String getWurstTemplate(){
		StringBuilder templates = new StringBuilder();
		templates.append("(deftemplate wurst (slot name (type STRING)) (multislot zutaten) (slot gewicht (type INTEGER)) (slot laenge (type INTEGER) (default 25))(slot hersteller (type STRING)))");
		return templates.toString();
	}

	@Override
	public void test() {
		this.setName("UnOrdered Facts");
		//template:
		executeTestEquals(getWurstTemplate(), "true");
		//facts:
		executeTestEquals("(assert (wurst ))", "f-1");
		executeTestEquals("(assert (wurst (name Pferd)))", "f-2");
		executeTestEquals("(assert (wurst (name (str-cat Schabracken Tapier))))", "f-3");
	}
	
	public void test2() {
		this.setName("Ordered Facts");
		//ordered fact:
		executeTestEquals("(assert (wurst Pferd 25 Maika))", "f-1");
	}
	
	public void test3() {
		this.setName("Ordered Facts with function call");
		//ordered fact with function call
		executeTestEquals("(assert (wurst (str-cat Schabracken Tapier) MetzgerPlum))", "f-1");
	}

}
