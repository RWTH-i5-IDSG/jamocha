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
import lombok.ToString;

import org.jamocha.dn.ConflictSet;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.nodes.TerminalNode;

/**
 * {@link MemoryHandlerTerminal}s are used for {@link TerminalNode}s instead of a
 * {@link MemoryHandlerMain}. They wrap incoming {@link MemoryHandlerPlusTemp}s together with their
 * target {@link TerminalNode} into {@link Assert}s. These are pushed back to the
 * {@link TerminalNode}, which adds them to the {@link ConflictSet}. Furthermore the {@link Assert}s
 * are cached in the {@link MemoryHandlerTerminal}. Incoming {@link MemoryHandlerMinusTemp}s are
 * similarly wrapped into {@link Retract}s. The cached {@link Assert}s are searched and revoked if
 * the Retract deleted the facts in an unprocessed {@link Assert}. Revoked {@link Assert}s are
 * removed from the cache.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see TerminalNode
 * @see Assert
 * @see Retract
 * @see ConflictSet
 */
public interface MemoryHandlerTerminal extends Iterable<Assert> {

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
	@ToString
	public abstract class AssertOrRetract<T extends AssertOrRetract<?>> {
		protected final MemoryHandler mem;

		public AssertOrRetract(final MemoryHandler mem) {
			assert mem.size() == 1;
			this.mem = mem;
		}

		public boolean setFollowingRetract(@SuppressWarnings("unused") final Retract minus) {
			return false;
		}

		public boolean isRevokedOrMinus() {
			return true;
		}

		abstract public void accept(final TerminalNode node, final AssertOrRetractVisitor visitor);

		/**
		 * Gets the {@link Template} of the facts in the underlying memory.
		 * 
		 * @return the {@link Template} of the facts in the underlying memory.
		 * @see Template
		 */
		public Template[] getTemplate() {
			return this.mem.getTemplate();
		}

		/**
		 * Fetches a value from the memory fully identified by a {@link FactAddress}, a
		 * {@link SlotAddress}.
		 * 
		 * @param address
		 *            a {@link FactAddress} identifying the fact the wanted value is in
		 * @param slot
		 *            a {@link SlotAddress} identifying the slot the wanted value is in
		 * @return a value from the memory identified by the given parameters
		 * @see FactAddress
		 * @see SlotAddress
		 */
		public Object getValue(final FactAddress address, final SlotAddress slot) {
			return this.mem.getValue(address, slot, 0);
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@ToString(callSuper = true)
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
	@ToString(callSuper = true)
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
	 * @param terminalNode
	 *            target terminal node
	 * @param mem
	 *            memory to wrap and add
	 * @return assert wrapped around the memory
	 */
	public void addPlusMemory(final TerminalNode terminalNode, final MemoryHandlerPlusTemp mem);

	/**
	 * Adds a memory wrapped into a retract to the terminal memory.
	 * 
	 * @param terminalNode
	 *            target terminal node
	 * @param mem
	 *            memory to wrap and add
	 * @return retract wrapped around the memory
	 */
	public void addMinusMemory(final TerminalNode terminalNode, final MemoryHandlerMinusTemp mem);

	/**
	 * Returns true iff there are unrevoked tokens in the memory.
	 * 
	 * @return true iff there are unrevoked tokens in the memory.
	 */
	public boolean containsUnrevokedTokens();

}
