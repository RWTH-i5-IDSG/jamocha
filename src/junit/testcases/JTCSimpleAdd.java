package testcases;

import testframework.AbstractJamochaTest;

public class JTCSimpleAdd extends AbstractJamochaTest {

	public JTCSimpleAdd(String arg0) {
		super(arg0);
	}

	public void test() {
		executeTestEquals("(+ 2 2)", "4");
	}

}
