/*
 * Copyright 2002-2015 The Jamocha Team
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
package org.jamocha.dn.compiler.ecblocks.conflictgraph;

import lombok.Getter;

import org.jamocha.dn.compiler.ecblocks.ECOccurrence;
import org.jamocha.filter.ECFilter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class ExplicitFINode extends ECOccurrenceNode {
	final ECFilter filter;

	public ExplicitFINode(final ECOccurrence occcurrence, final ECFilter filter) {
		super(occcurrence);
		this.filter = filter;
	}
}