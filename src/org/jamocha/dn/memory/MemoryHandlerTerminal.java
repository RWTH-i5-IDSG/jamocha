/*
 * Copyright 2002-2013 The Jamocha Team
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
package org.jamocha.dn.memory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.nodes.TerminalNode;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public interface MemoryHandlerTerminal extends MemoryHandler, Iterable<AssertOrRetract<?>> {

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	public interface AssertOrRetractVisitor {
		void visit(final TerminalNode node, final Assert mem);

		void visit(final TerminalNode node, final Retract mem);
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@Getter
	@RequiredArgsConstructor
	@ToString
	// TODO soll Ctor assert MemoryHandler.size() == 1 aufrufen?
	public abstract class AssertOrRetract<T extends AssertOrRetract<?>> {
		protected final MemoryHandler mem;

		public boolean setFollowingRetract(@SuppressWarnings("unused") final Retract minus) {
			return false;
		}

		public boolean isRevokedOrMinus() {
			return true;
		}

		abstract public void accept(final TerminalNode node, final AssertOrRetractVisitor visitor);
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@ToString
	public class Assert extends AssertOrRetract<Retract> {
		protected Retract dual = null;

		public Assert(final MemoryHandler mem) {
			super(mem);
		}

		@Override
		public boolean setFollowingRetract(final Retract minus) {
			if (null != this.dual)
				return false;
			this.dual = minus;
			return true;
		}

		@Override
		public boolean isRevokedOrMinus() {
			return null != this.dual;
		}

		@Override
		public void accept(final TerminalNode node, final AssertOrRetractVisitor visitor) {
			visitor.visit(node, this);
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@ToString
	public class Retract extends AssertOrRetract<Assert> {
		public Retract(final MemoryHandler mem) {
			super(mem);
		}

		@Override
		public void accept(final TerminalNode node, final AssertOrRetractVisitor visitor) {
			visitor.visit(node, this);
		}
	}

	/**
	 * Adds a memory wrapped into an assert to the terminal memory.
	 * 
	 * @param mem
	 *            memory to wrap and add
	 * @return assert wrapped around the memory
	 */
	public Assert addPlusMemory(final MemoryHandler mem);

	/**
	 * Adds a memory wrapped into a retract to the terminal memory.
	 * 
	 * @param mem
	 *            memory to wrap and add
	 * @return retract wrapped around the memory
	 */
	public Retract addMinusMemory(final MemoryHandler mem);

	/**
	 * Returns true iff there are unrevoked tokens in the memory.
	 * 
	 * @return true iff there are unrevoked tokens in the memory.
	 */
	public boolean containsUnrevokedTokens();

	/**
	 * Handles partial minus memories. Searches for plus memories that would be deleted by the given
	 * minus memory and creates new Retracts using the memory content of the plus memory.
	 * 
	 * @param terminalNode
	 *            target terminal node
	 * @param mem
	 *            minus memory to process
	 */
	public void addPartialMinusMemory(final TerminalNode terminalNode,
			final MemoryHandlerMinusTemp mem);

}
