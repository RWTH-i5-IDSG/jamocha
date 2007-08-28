package org.jamocha.rete.nodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.jamocha.rete.Deffact;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.memory.AlphaMemory;

public class RIANode extends AbstractBeta {

	private static final long serialVersionUID = 1L;

	public RIANode(int id) {
		super(id);
		mapping = new HashMap<FactTuple, Fact>();
		mergeMemory = new AlphaMemory();
	}

	protected Map<FactTuple, Fact> mapping;

	protected AlphaMemory mergeMemory = null;

	// protected abstract void evaluateBeta(FactTuple tuple, Rete engine) throws
	// AssertException;

	protected Fact addToMergeMemory(FactTuple ft, ReteNet net) {
		Fact f = new Deffact(net.getEngine().findTemplate("_initialFact"),
				null, new Slot[0]);
		mapping.put(ft, f);
		mergeMemory.add(f);
		return f;
	}

	protected Fact removeFromMergeMemory(FactTuple ft, ReteNet net) {
		Fact f = mapping.get(ft);
		mapping.remove(ft);
		mergeMemory.remove(f);
		return f;
	}

	protected void evaluateBeta(FactTuple tuple, ReteNet net)
			throws AssertException {
		Fact f = addToMergeMemory(tuple, net);
		this.propogateAssert(f, net);
	}

	/**
	 * s assertLeft takes an array of facts. Since the next join may be joining
	 * against one or more objects, we need to pass all previously matched
	 * facts.
	 * 
	 * @param factInstance
	 * @param engine
	 */
	public void assertLeft(FactTuple tuple, ReteNet net) throws AssertException {
		betaMemory.add(tuple);
		// only if activated:
		if (activated) {
			evaluateBeta(tuple, net);
		}
	}

	public void assertFact(Assertable fact, ReteNet net, BaseNode sender)
			throws AssertException {
		if (sender.isRightNode()) {
			assertRight((Fact) fact, net);
		} else
			assertLeft((FactTuple) fact, net);
	}

	public void assertRight(Fact fact, Rete engine) throws AssertException {
		// nothing, since we only have beta input
	}

	@Override
	public void retractFact(Assertable fact, ReteNet net, BaseNode sender)
			throws RetractException {
		if (sender.isRightNode()) {
			retractRight((Fact) fact, net);
		} else
			retractLeft((FactTuple) fact, net);
	}

	/**
	 * clear will clear the lists
	 */
	public void clear() {
		alphaMemory.clear();
		betaMemory.clear();
		mergeMemory.clear();
	}

	public Vector<FactTuple> mergeMemorygetPrefixMatchingTuples(FactTuple t) {
		Vector<FactTuple> result = new Vector<FactTuple>();
		for (FactTuple idx : mapping.keySet()) {
			if (idx.isMySubTuple(t)) {
				result.add(idx);
			}
		}
		return result;
	}

	/**
	 * Retracting from the left requires that we propogate the
	 * 
	 * @param factInstance
	 * @param engine
	 */
	public void retractLeft(FactTuple tuple, ReteNet net)
			throws RetractException {
		if (betaMemory.contains(tuple)) {
			betaMemory.remove(tuple);
			// now we propogate the retract. To do that, we have
			// merge each item in the list with the Fact array
			// and call retract in the successor nodes
			Vector<FactTuple> matchings = mergeMemorygetPrefixMatchingTuples(tuple);
			for (FactTuple toRemove : matchings) {
				Fact f = removeFromMergeMemory(tuple, net);
				propogateRetract(f, net);
			}

		}
	}

	/**
	 * Retract from the right works in the following order. 1. remove the fact
	 * from the right memory 2. check which left memory matched 3. propogate the
	 * retract
	 * 
	 * @param factInstance
	 * @param engine
	 */
	public void retractRight(Fact fact, Rete engine) throws RetractException {
		// nothing
	}

	public boolean isRightNode() {
		return true;
	}

	@Override
	protected boolean evaluate(FactTuple tuple, Fact rfcts, Rete engine) {
		return true;
	}

	@Override
	public boolean mergableTo(BaseNode other) {
		// TODO Auto-generated method stub
		return false;
	}

	// TODO: output is not display correctly. it is displayed empty every time
	// public String toPPString() {
	// StringBuffer sb = new StringBuffer();
	// sb.append(super.toPPString());
	// if (!activated) sb.append("not ");
	// sb.append("activated\n");
	// sb.append("Alpha-Input: ");
	// sb.append(alphaMemory.toPPString(5));
	// sb.append("\nBeta-Input: ");
	// sb.append(betaMemory.toPPString(5));
	// sb.append("\nOutput: ");
	// sb.append(mergeMemory.toPPString(5));
	// return sb.toString();
	// }

}
