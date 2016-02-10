/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.dn.memory;

import lombok.Getter;
import lombok.ToString;

import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.visitor.Visitable;
import org.jamocha.visitor.Visitor;

/**
 * {@link MemoryHandlerTerminal}s are used for {@link TerminalNode}s instead of a {@link MemoryHandlerMain}. They wrap
 * incoming {@link MemoryHandlerPlusTemp}s together with their target {@link TerminalNode} into {@link Assert}s. These
 * are pushed back to the {@link TerminalNode}, which adds them to the {@link org.jamocha.dn.ConflictSet}. Furthermore
 * the {@link Assert}s are cached in the {@link MemoryHandlerTerminal}. Incoming {@link MemoryHandlerMinusTemp}s are
 * similarly wrapped into {@link Retract}s. The cached {@link Assert}s are searched and revoked if the Retract deleted
 * the facts in an unprocessed {@link Assert}. Revoked {@link Assert}s are removed from the cache.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see TerminalNode
 * @see Assert
 * @see Retract
 * @see org.jamocha.dn.ConflictSet
 */
public interface MemoryHandlerTerminal extends Iterable<Assert> {

    /**
     * Extension of the visitor interface for Asserts and Retracts.
     *
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    interface AssertOrRetractVisitor extends Visitor {
        void visit(final Assert mem);

        void visit(final Retract mem);
    }

    /**
     * Base class for {@link Assert}s and {@link Retract}s. Provides access to the included memory in a manner similar
     * to {@link MemoryHandler}s (without the row index parameter as there is only one row in every {@link Assert} or
     * {@link Retract}).
     *
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @Getter
    @ToString
    abstract class AssertOrRetract<T extends AssertOrRetract<?>> implements Visitable<AssertOrRetractVisitor> {
        protected final MemoryHandler mem;

        public AssertOrRetract(final MemoryHandler mem) {
            assert mem.size() == 1;
            this.mem = mem;
        }

        /**
         * Returns the negated boolean value of {@link #isRevokedOrMinus()}, but also sets the revoking {@link Retract}
         * to be the one passed if this INSTANCE of {@link AssertOrRetract} is an {@link Assert} without a corresponding
         * {@link Retract}.
         *
         * @param minus
         *         {@link Retract} to be set as the revoking one for the current INSTANCE in case it is an {@link
         *         Assert}
         * @return !{@link #isRevokedOrMinus()}
         */
        public boolean setFollowingRetract(final Retract minus) {
            return !isRevokedOrMinus();
        }

        /**
         * Returns true iff this INSTANCE of {@link AssertOrRetract} is an {@link Assert} with a {@link Retract}
         * revoking the facts in the {@link Assert} or this INSTANCE is a {@link Retract}.
         *
         * @return true iff conditions named above hold
         */
        public boolean isRevokedOrMinus() {
            return true;
        }

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
         * Fetches a value from the memory fully identified by a {@link FactAddress}, a {@link SlotAddress}.
         *
         * @param address
         *         a {@link FactAddress} identifying the fact the wanted value is in
         * @param slot
         *         a {@link SlotAddress} identifying the slot the wanted value is in
         * @return a value from the memory identified by the given parameters
         * @see FactAddress
         * @see SlotAddress
         */
        public Object getValue(final FactAddress address, final SlotAddress slot) {
            return this.mem.getValue(address, slot, 0);
        }

        /**
         * Returns an array of fact identifiers corresponding to the facts in the token. Null entries indicate
         * existential parts.
         *
         * @return fact identifiers corresponding to the facts in the token
         */
        public FactIdentifier[] getFactIdentifiers() {
            return this.mem.getFactIdentifiers(0);
        }
    }

    /**
     * A class containing a single fact tuple, that is valid iff there is no dual {@link Retract} revoking the fact
     * tuple contained.
     *
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @ToString(callSuper = true)
    class Assert extends AssertOrRetract<Retract> {
        protected Retract dual = null;

        public Assert(final MemoryHandler mem) {
            super(mem);
        }

        @Override
        public boolean setFollowingRetract(final Retract minus) {
            if (isRevokedOrMinus()) return false;
            this.dual = minus;
            return true;
        }

        @Override
        public boolean isRevokedOrMinus() {
            return null != this.dual;
        }

        @Override
        public <V extends AssertOrRetractVisitor> V accept(final V visitor) {
            visitor.visit(this);
            return visitor;
        }
    }

    /**
     * A class containing a single fact tuple meant to invalidate a corresponding {@link Assert}.
     *
     * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
     */
    @ToString(callSuper = true)
    class Retract extends AssertOrRetract<Assert> {
        public Retract(final MemoryHandler mem) {
            super(mem);
        }

        @Override
        public <V extends AssertOrRetractVisitor> V accept(final V visitor) {
            visitor.visit(this);
            return visitor;
        }
    }

    /**
     * Adds the given {@link MemoryHandlerPlusTemp} to the {@link MemoryHandlerTerminal} as one or several {@link
     * Assert}s each containing one row of the plus token. The {@link Assert}s are passed to {@link
     * TerminalNode#enqueueAssert(Assert)}.
     *
     * @param terminalNode
     *         target terminal node
     * @param mem
     *         memory to wrap
     */
    void addPlusMemory(final TerminalNode terminalNode, final MemoryHandlerPlusTemp mem);

    /**
     * Adds the given {@link MemoryHandlerMinusTemp} to the {@link MemoryHandlerTerminal} as one or several {@link
     * Retract}s each containing one row of the plus token. Possible {@link Assert}s retracted by the {@link Retract}s
     * created are revoked. The {@link Retract}s are passed to {@link TerminalNode#enqueueRetract(Retract)}.
     *
     * @param terminalNode
     *         target terminal node
     * @param mem
     *         memory to wrap
     */
    void addMinusMemory(final TerminalNode terminalNode, final MemoryHandlerMinusTemp mem);

    /**
     * Returns true iff there are unrevoked tokens in the memory (i.e. instances of {@link AssertOrRetract} that return
     * false on {@link AssertOrRetract#isRevokedOrMinus()}).
     *
     * @return true iff there are unrevoked tokens in the memory
     */
    boolean containsUnrevokedTokens();

    /**
     * Flushes the cache of {@link Assert}s in this {@link MemoryHandlerTerminal}.
     */
    void flush();

}
