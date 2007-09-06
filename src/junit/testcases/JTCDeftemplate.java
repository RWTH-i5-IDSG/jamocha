package testcases;

public class JTCDeftemplate extends JamochaTest {

	public JTCDeftemplate(String arg0) {
		super(arg0);
	}

	public void testDeftemplate() {
		String result = this.executeCommandReturnLast("(deftemplate wurst(slot name)(slot size))");
		assertEquals("true", result);
	}

}
