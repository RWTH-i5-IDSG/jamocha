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
	public abstract class AssertOrRetract<T extends AssertOrRetract<?>> {
		protected final MemoryHandlerTemp mem;
		protected T dual = null;

		public boolean setDual(final Assert dual) {
			return false;
		}

		public boolean setDual(final Retract dual) {
			return false;
		}

		abstract public void accept(final TerminalNode node, final AssertOrRetractVisitor visitor);
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	public class Assert extends AssertOrRetract<Retract> {
		public Assert(final MemoryHandlerTemp mem) {
			super(mem);
		}

		@Override
		public boolean setDual(final Retract dual) {
			if (null != this.dual)
				return false;
			this.dual = dual;
			return true;
		}

		@Override
		public void accept(final TerminalNode node, final AssertOrRetractVisitor visitor) {
			visitor.visit(node, this);
		}
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	public class Retract extends AssertOrRetract<Assert> {
		public Retract(final MemoryHandlerTemp mem) {
			super(mem);
		}

		@Override
		public boolean setDual(final Assert dual) {
			if (null != this.dual)
				return false;
			this.dual = dual;
			return true;
		}

		@Override
		public void accept(final TerminalNode node, final AssertOrRetractVisitor visitor) {
			visitor.visit(node, this);
		}
	}

	public Assert addPlusMemory(final MemoryHandlerTemp mem);

	public Retract addMinusMemory(final MemoryHandlerTemp mem);

	public boolean containsUnrevokedTokens();

}
