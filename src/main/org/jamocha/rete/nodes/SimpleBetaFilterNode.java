package org.jamocha.rete.nodes;

import java.util.Iterator;

import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.nodes.joinfilter.*;
import org.jamocha.rete.visualisation.NodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.RootNodeDrawer;
import org.jamocha.rete.visualisation.nodedrawers.SimpleBetaFilterNodeDrawer;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 * this node has an alpha- and a beta-input. each combination
 * (which means the Cartesian product "BETA-INPUT x ALPHA-INPUT")
 * will be evaluated by the given join filters. if all filters
 * accept a combination, it will pass this node.
 */
public class SimpleBetaFilterNode extends AbstractBetaFilterNode {

	public SimpleBetaFilterNode(int id, WorkingMemory memory, ReteNet net) {
		super(id, memory,net);
	}
	
	public SimpleBetaFilterNode(int id, WorkingMemory memory,ReteNet net, JoinFilter[] filters) {
		super(id, memory,net,filters);
	}

	@Override
	protected void addAlpha(WorkingMemoryElement newElem) throws JoinFilterException, EvaluationException, NodeException {
		for(WorkingMemoryElement beta : betaInput.workingMemory.getMemory(betaInput)) {
			if ( applyFilters(newElem, beta) ) {
				WorkingMemoryElement newTuple = beta.getFactTuple().appendFact(newElem.getFirstFact());
				addAndPropagate(newTuple);
			}
		}
	}



	@Override
	protected void addBeta(WorkingMemoryElement newElem) throws JoinFilterException, EvaluationException, NodeException {
		for(WorkingMemoryElement alpha : alphaInput.workingMemory.getMemory(alphaInput)) {
			if ( applyFilters(alpha, newElem) ) {
				WorkingMemoryElement newTuple = newElem.getFactTuple().appendFact(alpha.getFirstFact());
				addAndPropagate(newTuple);
			}
		}
	}

	@Override
	protected void removeAlpha(WorkingMemoryElement oldElem) throws JoinFilterException, EvaluationException, NodeException {
		Iterator<WorkingMemoryElement> i = memory().iterator();
		while (i.hasNext()){
			WorkingMemoryElement wme = i.next();
			if (wme.getLastFact().equals(oldElem)) {
				i.remove();
				propagateRemoval(wme);
			}
		}
	}

	@Override
	protected void removeBeta(WorkingMemoryElement oldElem) throws JoinFilterException, EvaluationException, NodeException {
		Iterator<WorkingMemoryElement> i = memory().iterator();
		while (i.hasNext()){
			WorkingMemoryElement wme = i.next();
			if ( wme.getFactTuple().isMySubTuple(oldElem.getFactTuple()) ){
				i.remove();
				propagateRemoval(wme);
			}
		}
	}
	
	protected NodeDrawer newNodeDrawer() {
		return new SimpleBetaFilterNodeDrawer(this);
	}

	@Override
	public void getDescriptionString(StringBuilder sb) {
		super.getDescriptionString(sb);
		sb.append("|filters:");
		for (JoinFilter f : getFilters()) sb.append(f.toPPString()+" & ");
	}

}
