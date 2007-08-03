package org.jamocha.rete.nodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.jamocha.rete.Deffact;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.memory.AlphaMemory;

import com.sun.media.sound.AlawCodec;


public class RIANode extends AbstractBeta {

	public RIANode(int id) {
		super(id);
		mapping = new HashMap<FactTuple, Fact>();
		mergeMemory = new AlphaMemory();
	}

	protected Map<FactTuple,Fact> mapping;
	
	protected AlphaMemory mergeMemory = null;


	// protected abstract void evaluateBeta(FactTuple tuple, Rete engine) throws
	// AssertException;

	protected Fact addToMergeMemory(FactTuple ft, Rete engine) {
		Fact f = new Deffact( engine.findTemplate("_initialFact") , null, new Slot[0], engine.nextFactId());
		mapping.put(ft, f);
		mergeMemory.add(f);
		return f;
	}
	
	protected Fact removeFromMergeMemory(FactTuple ft, Rete engine) {
		Fact f = mapping.get(ft);
		mapping.remove(ft);
		mergeMemory.remove(f);
		return f;
	}
	
	protected void evaluateBeta(FactTuple tuple, Rete engine) throws AssertException {
		Fact f = addToMergeMemory(tuple, engine);
		this.propogateAssert(f, engine);
	}


	/**
	 * assertLeft takes an array of facts. Since the next join may be joining
	 * against one or more objects, we need to pass all previously matched
	 * facts.
	 * 
	 * @param factInstance
	 * @param engine
	 */
	public void assertLeft(FactTuple tuple, Rete engine) throws AssertException {
		betaMemory.add(tuple);
		// only if activated:
		if (activated) {
			evaluateBeta(tuple, engine);
		}
	}


	public void assertFact(Assertable fact, Rete engine, BaseNode sender) throws AssertException {
		if (sender.isRightNode()) {
			assertRight((Fact) fact, engine);
		} else
			assertLeft((FactTuple) fact, engine);
	}
	

	public void assertRight(Fact fact, Rete engine) throws AssertException {
		// nothing, since we only have beta input
	}

	@Override
	public void retractFact(Assertable fact, Rete engine, BaseNode sender) throws RetractException {
		if (sender.isRightNode()) {
			retractRight((Fact) fact, engine);
		} else
			retractLeft((FactTuple) fact, engine);
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
	public void retractLeft(FactTuple tuple, Rete engine) throws RetractException {
		if (betaMemory.contains(tuple)) {
			betaMemory.remove(tuple);
			// now we propogate the retract. To do that, we have
			// merge each item in the list with the Fact array
			// and call retract in the successor nodes
			Vector<FactTuple> matchings = mergeMemorygetPrefixMatchingTuples(tuple);
			for (FactTuple toRemove : matchings) {
				Fact f = removeFromMergeMemory(tuple, engine);
				propogateRetract(f, engine);
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
	protected boolean evaluate(FactTuple tuple, Fact rfcts) {
		return true;
	}
	
	//TODO: output is not display correctly. it is displayed empty every time
//	public String toPPString() {
//		StringBuffer sb = new StringBuffer();
//		sb.append(super.toPPString());
//		if (!activated) sb.append("not ");
//		sb.append("activated\n");
//		sb.append("Alpha-Input: ");
//		sb.append(alphaMemory.toPPString(5));
//		sb.append("\nBeta-Input: ");
//		sb.append(betaMemory.toPPString(5));
//		sb.append("\nOutput: ");
//		sb.append(mergeMemory.toPPString(5));
//		return sb.toString();
//	}

}