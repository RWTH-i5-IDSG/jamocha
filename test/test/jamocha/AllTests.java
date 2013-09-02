package test.jamocha;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ test.jamocha.engine.filter.FilterMockup.class,
		test.jamocha.engine.memory.javaimpl.MemoryHandlerTempTest.class })
public class AllTests {

}
