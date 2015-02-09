/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.engine.nodes;

import java.util.HashMap;
import java.util.Map;

import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.nodes.joinfilter.GeneralizedJoinFilter;
import org.jamocha.engine.nodes.joinfilter.JoinFilterException;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.parser.EvaluationException;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de> a
 *         QuantorBetaFilterNode is a BetaFilterNode, which has a slightly
 *         different output. for each beta-input, it determines, whether there
 *         exists at least one alpha-input, which is accepted by the given
 *         filters. if so, it outputs this beta-wme _only once_. this node can
 *         also be negated. a negated QuantorBetaFilterNode outputs a beta-wme,
 *         iff there is _no_ alpha-input accepted by the given filters.
 */
public class QuantorBetaFilterNode extends SimpleBetaFilterNode {

	protected boolean negated;

	protected class Counter<K> {

		private class Int {
			int i;
		}

		private final Map<K, Int> cnt;

		public Counter() {
			cnt = new HashMap<K, Int>();
		}

		public int getCount(final K k) {
			final Int i = cnt.get(k);
			return i != null ? i.i : 0;
		}

		private Int get(final K k) {
			Int i = cnt.get(k);
			if (i != null)
				return i;
			i = new Int();
			cnt.put(k, i);
			return i;
		}

		public int dec(final K k) {
			return --get(k).i;
		}

		public int inc(final K k) {
			return ++get(k).i;
		}

		public int add(final K k, final int val) {
			final Int i = get(k);
			i.i += val;
			return i.i;
		}

		public void set(final K k, final int val) {
			final Int i = get(k);
			i.i = val;
		}

	}

	private final Counter<WorkingMemoryElement> counter;

	public QuantorBetaFilterNode(final Engine engine, final boolean negated) {
		this(engine.getNet().nextNodeId(), engine.getWorkingMemory(), engine
				.getNet(), negated);
	}

	public QuantorBetaFilterNode(final int id, final WorkingMemory memory,
			final ReteNet net, final boolean negated) {
		this(id, memory, net, null, negated);
	}

	@Deprecated
	public QuantorBetaFilterNode(final int id, final WorkingMemory memory,
			final ReteNet net, final GeneralizedJoinFilter[] filter,
			final boolean negated) {
		super(id, memory, net, filter);
		this.negated = negated;
		counter = new Counter<WorkingMemoryElement>();
	}

	protected void itExists(final WorkingMemoryElement w) throws NodeException {
		if (negated)
			removeAndPropagate(w);
		else
			addAndPropagate(w);
	}

	protected void itDoesntExist(final WorkingMemoryElement w)
			throws NodeException {
		if (negated)
			addAndPropagate(w);
		else
			removeAndPropagate(w);
	}

	protected void evAlpha(final WorkingMemoryElement elem, final int direction)
			throws JoinFilterException, EvaluationException, NodeException {
		assert direction == -1 || direction == 1;
		final Iterable<WorkingMemoryElement> wmes = beta();
		if (wmes != null)
			for (final WorkingMemoryElement wme : wmes) {
				final boolean match = applyFilters(elem, wme);
				if (match) {
					final int now = counter.add(wme, direction);
					assert now >= 0;
					if (direction > 0 && now == 1)
						itExists(wme.getFactTuple().appendFact(
								net.getEngine().getInitialFact()));
					else if (direction < 0 && now == 0)
						itDoesntExist(wme.getFactTuple().appendFact(
								net.getEngine().getInitialFact()));
				}
			}
	}

	@Override
	protected void addAlpha(final WorkingMemoryElement newElem)
			throws JoinFilterException, EvaluationException, NodeException {
		evAlpha(newElem, 1);
	}

	@Override
	protected void addBeta(final WorkingMemoryElement newElem)
			throws JoinFilterException, EvaluationException, NodeException {
		int cn = 0;
		for (final WorkingMemoryElement wme : alpha())
			if (applyFilters(wme, newElem))
				cn++;
		if (cn > 0) {
			counter.set(newElem, cn);
			itExists(newElem.getFactTuple().appendFact(
					net.getEngine().getInitialFact()));
		} else
			itDoesntExist(newElem.getFactTuple().appendFact(
					net.getEngine().getInitialFact()));
	}

	@Override
	protected void removeAlpha(final WorkingMemoryElement oldElem)
			throws JoinFilterException, EvaluationException, NodeException {
		evAlpha(oldElem, -1);
	}

	@Override
	protected void removeBeta(final WorkingMemoryElement oldElem)
			throws JoinFilterException, EvaluationException, NodeException {
		removeAndPropagate(oldElem.getFactTuple().appendFact(
				net.getEngine().getInitialFact()));
		counter.set(oldElem, 0);
	}

	@Override
	public void accept(final NodeVisitor visitor) {
		visitor.visit(this);
	}

	public boolean isNegated() {
		return negated;
	}
}
