package testcases;

public class JTCSimpleAdd extends JamochaTest {

	public JTCSimpleAdd(String arg0) {
		super(arg0);
	}

	public void testSimpleAdd() {
		String result = this.executeCommandReturnLast("(+ 2 2)");
		assertEquals(result, "4");
	}

}
