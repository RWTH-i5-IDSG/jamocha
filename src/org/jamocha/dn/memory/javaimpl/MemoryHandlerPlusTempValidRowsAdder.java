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
package org.jamocha.dn.memory.javaimpl;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MemoryHandlerPlusTempValidRowsAdder
		extends
		MemoryHandlerPlusTempGenericValidRowsAdder<MemoryHandlerMain, MemoryHandlerPlusTempGenericValidRowsAdder.Data> {

	protected MemoryHandlerPlusTempValidRowsAdder(final MemoryHandlerMain originatingMainHandler,
			final MemoryHandlerPlusTempGenericValidRowsAdder.Data original, final int numChildren,
			final boolean empty, final boolean omitSemaphore) {
		super(originatingMainHandler, original, numChildren, empty, omitSemaphore);
	}

	protected MemoryHandlerPlusTempValidRowsAdder(final MemoryHandlerMain originatingMainHandler,
			final ArrayList<Row> newValidRows, final int numChildren, final boolean omitSemaphore) {
		this(originatingMainHandler, new Data(newValidRows), numChildren, newValidRows.isEmpty(),
				omitSemaphore);
	}

	@Override
	public <V extends MemoryHandlerPlusTempVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	void setFilteredData(final ArrayList<Row> newValidRows) {
		this.filtered = Optional.of(new Data(newValidRows));
	}
}
