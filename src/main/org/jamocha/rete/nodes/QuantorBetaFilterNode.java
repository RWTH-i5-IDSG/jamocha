package org.jamocha.rete.nodes;

import java.util.HashMap;
import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.nodes.joinfilter.JoinFilter;
import org.jamocha.rete.nodes.joinfilter.JoinFilterException;
import org.jamocha.rete.visualisation.NodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.QuantorBetaFilterNodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.RootNodeDrawer;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> a QuantorBetaFilterNode is
 *         a BetaFilterNode, which has a slightly different output. for each
 *         beta-input, it determines, whether there exists at least one
 *         alpha-input, which is accepted by the given filters. if so, it
 *         outputs this beta-wme _only once_. this node can also be negated. a
 *         negated QuantorBetaFilterNode outputs a beta-wme, iff there is _no_
 *         alpha-input accepted by the given filters.
 */
public class QuantorBetaFilterNode extends SimpleBetaFilterNode {

	protected boolean negated;

	protected class Counter<K> {

		private class Int {
			int i;
		}

		private Map<K, Int> cnt;

		public Counter() {
			cnt = new HashMap<K, Int>();
		}

		public int getCount(K k) {
			Int i = cnt.get(k);
			return (i != null) ? i.i : 0;
		}

		private Int get(K k) {
			Int i = cnt.get(k);
			if (i != null)
				return i;
			i = new Int();
			cnt.put(k, i);
			return i;
		}

		public int dec(K k) {
			return --(get(k).i);
		}

		public int inc(K k) {
			return ++(get(k).i);
		}

		public int add(K k, int val) {
			Int i = get(k);
			i.i += val;
			return i.i;
		}

		public void set(K k, int val) {
			Int i = get(k);
			i.i = val;
		}

	}

	private Counter<WorkingMemoryElement> counter;

	public QuantorBetaFilterNode(int id, WorkingMemory memory, ReteNet net,
			boolean negated) {
		this(id, memory, net, null, negated);
	}

	public QuantorBetaFilterNode(int id, WorkingMemory memory, ReteNet net,
			JoinFilter[] filter, boolean negated) {
		super(id, memory, net, filter);
		this.negated = negated;
		counter = new Counter<WorkingMemoryElement>();
	}

	protected void itExists(WorkingMemoryElement w) throws NodeException {
		if (negated)
			removeAndPropagate(w);
		else
			addAndPropagate(w);
	}

	protected void itDoesntExist(WorkingMemoryElement w) throws NodeException {
		if (negated)
			addAndPropagate(w);
		else
			removeAndPropagate(w);
	}

	protected void evAlpha(WorkingMemoryElement elem, int direction)
			throws JoinFilterException, EvaluationException, NodeException {
		assert (direction == -1 || direction == 1);
		Iterable<WorkingMemoryElement> wmes = beta();
		if (wmes != null) {
			for (WorkingMemoryElement wme : wmes) {
				boolean match = applyFilters(elem, wme);
				if (match) {
					int now = counter.add(wme, direction);
					assert (now >= 0);
					if (direction > 0 && now == 1) {
						itExists(wme.getFactTuple().appendFact(
								net.engine.getInitialFact()));
					} else if (direction < 0 && now == 0) {
						itDoesntExist(wme.getFactTuple().appendFact(
								net.engine.getInitialFact()));
					}
				}
			}
		}
	}

	@Override
	protected void addAlpha(WorkingMemoryElement newElem)
			throws JoinFilterException, EvaluationException, NodeException {
		evAlpha(newElem, 1);
	}

	@Override
	protected void addBeta(WorkingMemoryElement newElem)
			throws JoinFilterException, EvaluationException, NodeException {
		int cn = 0;
		for (WorkingMemoryElement wme : alpha()) {
			if (applyFilters(wme, newElem))
				cn++;
		}
		if (cn > 0) {
			counter.set(newElem, cn);
			itExists(newElem.getFactTuple().appendFact(
					net.engine.getInitialFact()));
		} else {
			itDoesntExist(newElem.getFactTuple().appendFact(
					net.engine.getInitialFact()));
		}
	}

	@Override
	protected void removeAlpha(WorkingMemoryElement oldElem)
			throws JoinFilterException, EvaluationException, NodeException {
		evAlpha(oldElem, -1);
	}

	@Override
	protected void removeBeta(WorkingMemoryElement oldElem)
			throws JoinFilterException, EvaluationException, NodeException {
		removeAndPropagate(oldElem.getFactTuple().appendFact(
				net.engine.getInitialFact()));
		counter.set(oldElem, 0);
	}

	protected NodeDrawer newNodeDrawer() {
		return new QuantorBetaFilterNodeDrawer(this);
	}

}
