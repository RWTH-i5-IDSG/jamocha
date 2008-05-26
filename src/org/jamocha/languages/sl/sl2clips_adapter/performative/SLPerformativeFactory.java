/*
 * Copyright 2007 Alexander Wilden
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
 */
package org.jamocha.languages.sl.sl2clips_adapter.performative;


/**
 * A Factory that creates a <code>SLPerformativeTranslator</code> according to
 * a given performative.
 * 
 * @author Alexander Wilden
 */
public class SLPerformativeFactory {

	/**
	 * Force static access.
	 */
	private SLPerformativeFactory() {
	}

	public static SLPerformativeTranslator getSLPerformativeTranslator(
			String performative)
			throws SLPerformativeTranslatorNotFoundException {
		if (performative.equalsIgnoreCase("accept-proposal")) {
			return new AcceptProposal();
		} else if (performative.equalsIgnoreCase("agree")) {
			return new Agree();
		} else if (performative.equalsIgnoreCase("cancel")) {
			return new Cancel();
		} else if (performative.equalsIgnoreCase("cfp")) {
			return new Cfp();
		} else if (performative.equalsIgnoreCase("confirm")) {
			return new Confirm();
		} else if (performative.equalsIgnoreCase("disconfirm")) {
			return new Disconfirm();
		} else if (performative.equalsIgnoreCase("failure")) {
			return new Failure();
		} else if (performative.equalsIgnoreCase("inform")) {
			return new Inform();
		} else if (performative.equalsIgnoreCase("inform-if")) {
			return new InformIf();
		} else if (performative.equalsIgnoreCase("inform-ref")) {
			return new InformRef();
		} else if (performative.equalsIgnoreCase("not-understood")) {
			return new NotUnderstood();
		} else if (performative.equalsIgnoreCase("propagate")) {
			return new Propagate();
		} else if (performative.equalsIgnoreCase("propose")) {
			return new Propose();
		} else if (performative.equalsIgnoreCase("proxy")) {
			return new Proxy();
		} else if (performative.equalsIgnoreCase("query-if")) {
			return new QueryIf();
		} else if (performative.equalsIgnoreCase("query-ref")) {
			return new QueryRef();
		} else if (performative.equalsIgnoreCase("refuse")) {
			return new Refuse();
		} else if (performative.equalsIgnoreCase("reject-proposal")) {
			return new RejectProposal();
		} else if (performative.equalsIgnoreCase("request")) {
			return new Request();
		} else if (performative.equalsIgnoreCase("request-when")) {
			return new RequestWhen();
		} else if (performative.equalsIgnoreCase("request-whenever")) {
			return new RequestWhenever();
		} else if (performative.equalsIgnoreCase("subscribe")) {
			return new Subscribe();
		}
		throw new SLPerformativeTranslatorNotFoundException(performative);
	}

}
