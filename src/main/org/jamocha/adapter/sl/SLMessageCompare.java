package org.jamocha.adapter.sl;

import java.io.Serializable;

import org.jamocha.adapter.sl.configurations.ContentSLConfiguration;
import org.jamocha.adapter.sl.configurations.SLCompileType;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.sl.ParseException;
import org.jamocha.parser.sl.SLParser;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

public class SLMessageCompare implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "SL-message-compare";

	public FunctionDescription getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return NAME;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		JamochaValue result = JamochaValue.FALSE;
		if (params != null && params.length == 2) {
			String message1 = params[0].getValue(engine).getStringValue();
			String message2 = params[1].getValue(engine).getStringValue();

			try {
				ContentSLConfiguration contentConf1 = SLParser.parse(message1);
				ContentSLConfiguration contentConf2 = SLParser.parse(message2);
				message1 = contentConf1
						.compile(SLCompileType.ACTION_AND_ASSERT);
				message2 = contentConf2
						.compile(SLCompileType.ACTION_AND_ASSERT);
				if (message1.equalsIgnoreCase(message2)) {
					return JamochaValue.TRUE;
				}
			} catch (ParseException e) {
				throw new EvaluationException(e);
			}

		} else {
			throw new IllegalParameterException(1);
		}
		return result;
	}
}
