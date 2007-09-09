package testcases;

import testframework.AbstractJamochaTest;

public class JTCDeftemplate extends AbstractJamochaTest {

	public JTCDeftemplate(String arg0) {
		super(arg0);
	}

	public void test() {
		executeTestEquals("(deftemplate wurst(slot name)(slot size))", "true");
	}

}
