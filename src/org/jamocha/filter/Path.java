/*
 * Copyright 2002-2013 The Jamocha Team
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
package org.jamocha.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;

/**
 * A Path describes an element of a condition part of a rule. It "traces" its
 * position in the network during construction time. The class
 * {@link PathTransformation} keeps all the information needed about Paths.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see PathTransformation
 */
@Getter
@RequiredArgsConstructor
public class Path {
	final Template template;

	/**
	 * Extracts the {@link SlotType} of the Slot corresponding to
	 * {@link SlotAddress addr}.
	 * 
	 * @param addr
	 *            SlotAddress of the Slot one is interested in
	 * @return The {@link SlotType} of the Slot corresponding to the parameter
	 *         given
	 */
	public SlotType getTemplateSlotType(final SlotAddress addr) {
		return addr.getSlotType(this.template);
	}
}
