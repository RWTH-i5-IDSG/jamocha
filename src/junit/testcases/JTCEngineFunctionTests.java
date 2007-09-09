package testcases;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.rete.functions.Function;
import org.jamocha.rete.functions.FunctionDescription;

import testframework.AbstractJamochaTest;

public class JTCEngineFunctionTests extends AbstractJamochaTest {

	public JTCEngineFunctionTests(String arg0) {
		super(arg0);
	}

	@Override
	public void test() {
		List<Function> functions = new LinkedList<Function>(engine.getFunctionMemory().getAllFunctions());

		// Collection<Function> functions =
		// this.engine.getFunctionMemory().getAllFunctions();
		FunctionDescription descr;

		// traverse all functions and execute their example if possible.
		// only check for exceptions is done!
		// TODO: we might introduce expected result for examples to check here!
		for (Function currentFunction : functions) {
			descr = currentFunction.getDescription();
			if (descr.isResultAutoGeneratable()) {

				System.out.println("execute example for function:" + currentFunction);
				// execute:
				this.executeTestException(descr.getExample(), "Error in executing Function: " + currentFunction.toString());
				// cleanup engine:
				engine.clearAll();
			}
		}
	}

}
