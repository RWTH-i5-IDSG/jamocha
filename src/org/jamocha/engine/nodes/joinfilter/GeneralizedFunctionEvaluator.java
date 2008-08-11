/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.engine.nodes.joinfilter;

import java.util.List;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.parser.EvaluationException;

public class GeneralizedFunctionEvaluator implements GeneralizedJoinFilter {

	protected Parameter[] parameters;
	protected Function function;
	protected Engine engine;

	// TODO: maybe it is better to store a Signature instead of
	// Parameter[]&Function

	private GeneralizedFunctionEvaluator(final Engine engine, final Function function) {
		this.function = function;
		this.engine = engine;
	}

	public GeneralizedFunctionEvaluator(final Engine engine, final Function function,
			final List<Parameter> parameters) throws JoinFilterException {
		this(engine, function);
		final Parameter[] params = new Parameter[0];
		this.parameters = parameters.toArray(params);
	}

	public GeneralizedFunctionEvaluator(final Engine engine, final Function function,
			final Parameter[] parameters) throws JoinFilterException {
		this(engine, function);
		this.parameters = parameters;
	}

	private void substitute(final Parameter[] params, FactTuple t) throws FieldAddressingException,
			EvaluationException {
		for (int i = 0; i < params.length; i++) {
			final Parameter p = params[i];
			if (p instanceof LeftFieldAddress) {
				params[i] = ((FieldAddress)p).getIndexedValue(t);
			} else if (p instanceof Signature) {
				final Signature sig = (Signature) p;
				substitute(sig.getParameters(), t);
			}
		}
	}

	private Parameter[] semicloneParameters(final Parameter[] orig) {
		final Parameter[] clone = orig.clone();

		for (int i = 0; i < clone.length; i++)
			if (clone[i] instanceof Signature) {
				final Signature s = (Signature) clone[i];
				final Signature sigClone = (Signature) s.clone();
				sigClone.setParameters(semicloneParameters(sigClone
						.getParameters()));
				clone[i] = sigClone;
			}

		return clone;
	}

	public boolean evaluate(FactTuple t, final Engine engine) throws JoinFilterException,
			EvaluationException {
		final Parameter[] callParams = semicloneParameters(parameters);
		substitute(callParams, t);

		try {
			return function.executeFunction(engine, callParams)
					.getBooleanValue();
		} catch (final EvaluationException e) {
			return false;
		}
	}

	public String toPPString() {
		/* TODO thats not really good since FieldAdresses only were printed if
	 	in first level since the clips-formatter doesn't format it */
		final StringBuffer result = new StringBuffer();
		result.append("test: ");
		result.append(function.getName());
		result.append("(");
		for (int i = 0; i < parameters.length; i++) {
			final Parameter param = parameters[i];
			if (i > 0)
				result.append(", ");
			result.append(param.getExpressionString());
		}
		result.append(")");
		return result.toString();
	}

}
