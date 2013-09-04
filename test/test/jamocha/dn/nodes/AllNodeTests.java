package test.jamocha.dn.nodes;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AlphaNodeTest.class, BetaNodeTest.class,
		ObjectTypeNodeTest.class, TerminalNodeTest.class })
public class AllNodeTests {

}
