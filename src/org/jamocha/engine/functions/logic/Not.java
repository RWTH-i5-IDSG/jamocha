/**
 * 
 */
package org.jamocha.engine.functions.logic;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.functions.AbstractFunction;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

/**
 * @author Christoph Terwelp
 *
 */
public class Not extends AbstractFunction {
	
	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "Returns the boolean inverse of the parameter";
		}

		public String getExample() {
			return "(not false)";
		}

		public Object getExpectedResult() {
			return true;
		}

		public int getParameterCount() {
			return 1;
		}

		public String getParameterDescription(int parameter) {
			return "Boolean value to invert";
		}

		public String getParameterName(int parameter) {
			return "value";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			return JamochaType.BOOLEANS;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public boolean isResultAutoGeneratable() {
			return true;
		}
	}
	
	public static final FunctionDescription DESCRIPTION = new Description();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "not";

	/* (non-Javadoc)
	 * @see org.jamocha.engine.functions.AbstractFunction#executeFunction(org.jamocha.engine.Engine, org.jamocha.engine.Parameter[])
	 */
	@Override
	public JamochaValue executeFunction(Engine engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = null;
		if (params != null && params.length == 1) {
			JamochaValue value = params[0].getValue(engine);
			if (!value.getType().equals(JamochaType.BOOLEAN)) {
				value = value.implicitCast(JamochaType.BOOLEAN);
			}
			result = JamochaValue.newBoolean(!value.getBooleanValue());
			return result;
		}
		throw new IllegalParameterException(1);
	}

	/* (non-Javadoc)
	 * @see org.jamocha.engine.functions.AbstractFunction#getDescription()
	 */
	@Override
	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see org.jamocha.engine.functions.AbstractFunction#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

}
