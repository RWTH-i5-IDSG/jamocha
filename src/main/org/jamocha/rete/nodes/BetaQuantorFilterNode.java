package org.jamocha.rete.nodes;

import java.util.HashMap;
import java.util.Map;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

public class BetaQuantorFilterNode extends BetaFilterNode {

	class Marker<K> {

		protected Map<K, Boolean> map = null;

		boolean def;

		public Marker(boolean def) {
			map = new HashMap<K, Boolean>();
			this.def = def;
		}

		public void set(K v, boolean b) {
			map.put(v, b);
		}

		public void mark(K v) {
			set(v, true);
		}

		public void unmark(K v) {
			set(v, false);
		}

		public boolean isMarked(K v) {
			Boolean res = map.get(v);
			if (res == null)
				return def;
			return res;
		}
	}

	private static final long serialVersionUID = 1L;

	protected boolean negated;

	protected Marker<FactTuple> propagatedMarker = new Marker<FactTuple>(false);

	public BetaQuantorFilterNode(int id, boolean negated) {
		super(id);
		this.negated = negated;
	}

	public BetaQuantorFilterNode(int id) {
		this(id, false);
	}

	protected void propagateNewTuple(FactTuple t, ReteNet net)
			throws AssertException {
		FactTuple newTuple = t.addFact(net.getEngine().getInitialFact());
		propagatedMarker.mark(t);
		mergeMemory.add(newTuple);
		propogateAssert(newTuple, net);
	}

	protected void unPropagateNewTuple(FactTuple t, ReteNet net)
			throws RetractException {
		propagatedMarker.unmark(t);

		for (FactTuple tuple : mergeMemory.getPrefixMatchingTuples(t)) {
			mergeMemory.remove(tuple);
			propogateRetract(tuple, net);
		}
	}

	protected void evaluateBeta(FactTuple tuple, ReteNet net)
			throws AssertException {
		// iterate over alpha memory.
		// check, whether there is a fact, which matches the filters.
		// propagate them, depending on "negated" and the matching above and
		// mark tuple as propagated somewhere

		boolean thereIsAFact = false;
		for (Fact alphaFact : alphaMemory) {
			if (evaluate(tuple, alphaFact, net.getEngine())) {
				thereIsAFact = true;
				break;
			}
		}
		boolean propagateIt = (thereIsAFact != negated);
		if (propagateIt) {
			propagateNewTuple(tuple, net);
		}
	}

	public void assertLeft(FactTuple tuple, ReteNet net) throws AssertException {
		betaMemory.add(tuple);
		if (!activated)
			return;

		evaluateBeta(tuple, net);
	}

	public void assertRight(Fact fact, ReteNet net) throws AssertException {

		alphaMemory.add(fact);
		if (!activated)
			return;

		// iterate over beta memory (for "negated" over the already propagated,
		// else over the not propagated). check for each tuple, whether the
		// propagation status must be changed and do it, if so.
		for (FactTuple tuple : betaMemory) {
			if (propagatedMarker.isMarked(tuple) != negated)
				continue;
			boolean matchesToNewAlpha = evaluate(tuple, fact, net.getEngine());
			if (!matchesToNewAlpha)
				continue;
			if (negated /* && propagated */) {
				try {
					unPropagateNewTuple(tuple, net);
				} catch (RetractException e) {
					throw new AssertException(e);
				}
			} else /* if (!negated && !propagated ) */{
				propagateNewTuple(tuple, net);
			}
		}
	}

	public void retractLeft(FactTuple tuple, ReteNet net)
			throws RetractException {
		// simply retract it ;)
		if (propagatedMarker.isMarked(tuple))
			unPropagateNewTuple(tuple, net);
		betaMemory.remove(tuple);
	}

	public void retractRight(Fact fact, ReteNet net) throws RetractException {
		// retract it and check for all not propagated (or for "negated" all
		// propagated), whether this status must be changed. if so, do it.
		for (FactTuple tuple : betaMemory) {
			if (propagatedMarker.isMarked(tuple) != negated)
				continue;

			boolean matchBeforeRetracting = false;
			boolean matchAfterRetracting = false;
			for (Fact alphaFact : alphaMemory) {
				if (evaluate(tuple, alphaFact, net.getEngine())) {
					matchBeforeRetracting = true;
					if (alphaFact != fact)
						matchAfterRetracting = true;
				}
				if (matchAfterRetracting && matchBeforeRetracting)
					break;
			}
			if (matchBeforeRetracting && !matchAfterRetracting) {
				if (negated) {
					try {
						propagateNewTuple(tuple, net);
					} catch (AssertException e) {
						throw new RetractException(e);
					}
				} else {
					unPropagateNewTuple(tuple, net);
				}
			}
		}
		alphaMemory.remove(fact);
	}

	public void activate(ReteNet net) throws AssertException {

		if (!activated) {
			activated = true;
			for (FactTuple tuple : betaMemory) {
				evaluateBeta(tuple, net);
			}
		}
		for (BaseNode b : parentNodes) {
			if (b instanceof AbstractBeta) {
				AbstractBeta beta = (AbstractBeta) b;
				beta.activate(net);
			}
		}
	}

}
