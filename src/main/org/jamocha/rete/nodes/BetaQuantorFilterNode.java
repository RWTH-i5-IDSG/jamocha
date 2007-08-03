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

	protected void propagateNewTuple(FactTuple t, Rete e) throws AssertException {
		FactTuple newTuple = t.addFact(e.getInitialFact());
		propagatedMarker.mark(t);
		mergeMemory.add(newTuple);
		propogateAssert(newTuple, e);
	}

	protected void unPropagateNewTuple(FactTuple t, Rete e) throws RetractException {
		propagatedMarker.unmark(t);
		
		for (FactTuple tuple: mergeMemory.getPrefixMatchingTuples(t)){
			mergeMemory.remove(tuple);
			propogateRetract(tuple, e);
		}
	}

	protected void evaluateBeta(FactTuple tuple, Rete engine) throws AssertException {
		// iterate over alpha memory.
		// check, whether there is a fact, which matches the filters.
		// propagate them, depending on "negated" and the matching above and
		// mark tuple as propagated somewhere

		boolean thereIsAFact = false;
		for (Fact alphaFact : alphaMemory) {
			if (evaluate(tuple, alphaFact)) {
				thereIsAFact = true;
				break;
			}
		}
		boolean propagateIt = (thereIsAFact != negated);
		if (propagateIt) {
			propagateNewTuple(tuple, engine);
		}
	}

	public void assertLeft(FactTuple tuple, Rete engine) throws AssertException {
		betaMemory.add(tuple);
		if (!activated)
			return;

		evaluateBeta(tuple, engine);
	}

	public void assertRight(Fact fact, Rete engine) throws AssertException {

		alphaMemory.add(fact);
		if (!activated)
			return;

		// iterate over beta memory (for "negated" over the already propagated,
		// else over the not propagated). check for each tuple, whether the
		// propagation status must be changed and do it, if so.
		for (FactTuple tuple : betaMemory) {
			if (propagatedMarker.isMarked(tuple) != negated)
				continue;
			boolean matchesToNewAlpha = evaluate(tuple, fact);
			if (!matchesToNewAlpha)
				continue;
			if (negated /* && propagated */) {
				try {
					unPropagateNewTuple(tuple, engine);
				} catch (RetractException e) {
					throw new AssertException(e);
				}
			} else /* if (!negated && !propagated ) */{
				propagateNewTuple(tuple, engine);
			}
		}
	}

	public void retractLeft(FactTuple tuple, Rete engine) throws RetractException {
		// simply retract it ;)
		if (propagatedMarker.isMarked(tuple))
			unPropagateNewTuple(tuple, engine);
		betaMemory.remove(tuple);
	}

	public void retractRight(Fact fact, Rete engine) throws RetractException {
		// retract it and check for all not propagated (or for "negated" all
		// propagated), whether this status must be changed. if so, do it.
		for (FactTuple tuple : betaMemory) {
			if (propagatedMarker.isMarked(tuple) != negated)
				continue;

			boolean matchBeforeRetracting = false;
			boolean matchAfterRetracting = false;
			for (Fact alphaFact : alphaMemory) {
				if (evaluate(tuple, alphaFact)) {
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
						propagateNewTuple(tuple, engine);
					} catch (AssertException e) {
						throw new RetractException(e);
					}
				} else {
					unPropagateNewTuple(tuple, engine);
				}
			}
		}
		alphaMemory.remove(fact);
	}

	public void activate(Rete engine) throws AssertException {

		if (!activated) {
			activated = true;
			for (FactTuple tuple : betaMemory) {
				evaluateBeta(tuple, engine);
			}
		}
		for (BaseNode b : parentNodes) {
			if (b instanceof AbstractBeta) {
				AbstractBeta beta = (AbstractBeta)b;
				beta.activate(engine);
			}
		}
	}

}
