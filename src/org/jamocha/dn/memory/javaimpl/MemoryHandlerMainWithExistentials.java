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

import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.filter.AddressFilter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class MemoryHandlerMainWithExistentials extends MemoryHandlerMain {

	JamochaArray<Row> allRows = new JamochaArray<>();

	MemoryHandlerMainWithExistentials(final Template[] template, final Counter counter,
			final FactAddress[] addresses) {
		super(template, counter, addresses);
	}

	@Override
	public Row newRow(final Fact[] factTuple) {
		assert factTuple.length == this.template.length;
		return new RowWithCounters(factTuple, new int[this.counter.getColumns().length]);
	}

	@Override
	public Row newRow() {
		return newRow(new Fact[template.length]);
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerTemp processTokenInBeta(
			final org.jamocha.dn.memory.MemoryHandlerTemp token, final Edge originIncomingEdge,
			final AddressFilter filter) throws CouldNotAcquireLockException {
		return ((MemoryHandlerTemp) token).newBetaTemp(this, originIncomingEdge, filter);
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerTemp processTokenInAlpha(
			final org.jamocha.dn.memory.MemoryHandlerTemp token, final Edge originIncomingEdge,
			final AddressFilter filter) throws CouldNotAcquireLockException {
		return ((MemoryHandlerTemp) token).newAlphaTemp(this, originIncomingEdge, filter);
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerPlusTemp newPlusToken(final Node otn,
			final org.jamocha.dn.memory.Fact... facts) {
		return MemoryHandlerPlusTemp.newRootTemp(this, otn, facts);
	}

	@Override
	public org.jamocha.dn.memory.MemoryHandlerMinusTemp newMinusToken(
			final org.jamocha.dn.memory.Fact... facts) {
		return MemoryHandlerMinusTemp.newRootTemp(this, facts);
	}

}
