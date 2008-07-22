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

package org.jamocha.engine.workingmemory.elements;

import org.jamocha.Constants;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.parser.EvaluationException;

/**
 * @author Peter Lin
 * 
 * InitialFact is used for rules without conditions and cases where a rule
 * starts with exist or not.
 */
public class InitialFact extends Deftemplate {

	private Fact factInstance = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InitialFact() {
		super(Constants.INITIAL_FACT);
		slots = new TemplateSlot[0];
	}

	public Fact getInitialFact() {
		if (factInstance == null)
			try {
				final SlotConfiguration[] noSlots = {};
				factInstance = createFact(noSlots, null);
			} catch (final EvaluationException e) {
				e.printStackTrace();
			}
		return factInstance;
	}
}
