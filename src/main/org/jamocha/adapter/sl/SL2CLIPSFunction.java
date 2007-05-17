/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.adapter.sl;

import java.io.Serializable;

import org.jamocha.adapter.AdapterTranslationException;
import org.jamocha.adapter.sl.performative.AcceptProposal;
import org.jamocha.adapter.sl.performative.Agree;
import org.jamocha.adapter.sl.performative.Cancel;
import org.jamocha.adapter.sl.performative.Cfp;
import org.jamocha.adapter.sl.performative.Confirm;
import org.jamocha.adapter.sl.performative.Disconfirm;
import org.jamocha.adapter.sl.performative.Failure;
import org.jamocha.adapter.sl.performative.Inform;
import org.jamocha.adapter.sl.performative.InformIf;
import org.jamocha.adapter.sl.performative.InformRef;
import org.jamocha.adapter.sl.performative.NotUnderstood;
import org.jamocha.adapter.sl.performative.Propagate;
import org.jamocha.adapter.sl.performative.Propose;
import org.jamocha.adapter.sl.performative.Proxy;
import org.jamocha.adapter.sl.performative.QueryIf;
import org.jamocha.adapter.sl.performative.QueryRef;
import org.jamocha.adapter.sl.performative.Refuse;
import org.jamocha.adapter.sl.performative.RejectProposal;
import org.jamocha.adapter.sl.performative.Request;
import org.jamocha.adapter.sl.performative.RequestWhen;
import org.jamocha.adapter.sl.performative.RequestWhenever;
import org.jamocha.adapter.sl.performative.Subscribe;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * Translates SL-Code to CLIPS-Code
 * 
 * @author Alexander Wilden
 */
public class SL2CLIPSFunction implements Function, Serializable {

	private static final class SL2CLIPSFunctionDescription implements
			FunctionDescription {

		public String getDescription() {
			return "Translates SL-Code to CLIPS-Code which then will be returned as a String.";
		}

		public int getParameterCount() {
			return 2;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "Performative that is used.";
			case 1:
				return "String that should be translated to CLIPS-Code.";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "performative";
			case 1:
				return "string";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.STRINGS;
			case 1:
				return JamochaType.STRINGS;
			}
			return null;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.STRINGS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}

		public String getExample() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final FunctionDescription DESCRIPTION = new SL2CLIPSFunctionDescription();

	private static final long serialVersionUID = 1L;

	public static final String NAME = "sl2clips";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
	}

	public JamochaType getReturnType() {
		return JamochaType.STRING;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {
		String clipsCode = "";
		if (params != null && params.length == 2) {
			String performative = params[0].getValue(engine).getStringValue();
			String slCode = params[1].getValue(engine).getStringValue();
			try {
				if (performative.equalsIgnoreCase("accept-proposal")) {
					clipsCode = AcceptProposal.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("agree")) {
					clipsCode = Agree.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("cancel")) {
					clipsCode = Cancel.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("cfp")) {
					clipsCode = Cfp.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("confirm")) {
					clipsCode = Confirm.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("disconfirm")) {
					clipsCode = Disconfirm.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("failure")) {
					clipsCode = Failure.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("inform")) {
					clipsCode = Inform.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("inform-if")) {
					clipsCode = InformIf.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("inform-ref")) {
					clipsCode = InformRef.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("not-understood")) {
					clipsCode = NotUnderstood.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("propagate")) {
					clipsCode = Propagate.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("propose")) {
					clipsCode = Propose.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("proxy")) {
					clipsCode = Proxy.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("query-if")) {
					clipsCode = QueryIf.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("query-ref")) {
					clipsCode = QueryRef.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("refuse")) {
					clipsCode = Refuse.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("reject-proposal")) {
					clipsCode = RejectProposal.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("request")) {
					clipsCode = Request.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("request-when")) {
					clipsCode = RequestWhen.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("request-whenever")) {
					clipsCode = RequestWhenever.getCLIPS(slCode);
				} else if (performative.equalsIgnoreCase("subscribe")) {
					clipsCode = Subscribe.getCLIPS(slCode);
				}
			} catch (AdapterTranslationException e) {
				throw new EvaluationException(
						"Error while translating from SL to CLIPS.", e);
			}
		} else {
			throw new IllegalParameterException(2);
		}
		return JamochaValue.newString(clipsCode);
	}
}
