/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.logging;

import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.Template;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface LogFormatter {

	void messageFactDetails(final SideEffectFunctionToNetwork network, final int id,
			final MemoryFact value);

	void messageTemplateDetails(final SideEffectFunctionToNetwork network, final Template template);

	void messageFactAssertions(final SideEffectFunctionToNetwork network,
			final FactIdentifier[] assertedFacts);

	void messageFactRetractions(final SideEffectFunctionToNetwork network,
			final FactIdentifier[] factsToRetract);

	void messageArgumentTypeMismatch(final SideEffectFunctionToNetwork network,
			final String function, final int paramIndex, final String expected);

	void messageUnknownSymbol(final SideEffectFunctionToNetwork network, final String expectedType,
			final String name);
}
