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
package org.jamocha.languages.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * Gathers relevant information about a variable. A null SlotAddress indicates this SingleVariable
 * is a Fact-Variable e.g. originating from an AssignedPatternCE.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@AllArgsConstructor
public class SingleVariable {
	final Symbol image;
	final Template template;
	final SlotAddress slot;

	boolean isFactVariable() {
		return null == slot;
	}
}
