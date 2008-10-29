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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.application.gui.retevisualisation.nodedrawers.MultiBetaFilterNodeDrawer;
import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.nodes.joinfilter.GeneralizedJoinFilter;
import org.jamocha.engine.nodes.joinfilter.JoinFilterException;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.parser.EvaluationException;

/**
 * it makes something like the traditional join filter node, but with more than
 * two inputs.
 *
 * WARNING: this node type does NOT replace the traditional 2-input-joinnode.
 *  the traditional one is much more in the sense of rete and its 
 *  implementation is much smarter. so, whenever it is possible, the rule
 *  compiler should really really use the traditional join node.
 *  
 * @author Josef Alexander Hahn
 */
public class MultiBetaJoinNode extends AbstractBetaFilterNode {

	private List<Node> inputs;
	
	@Deprecated
	public MultiBetaJoinNode(int id, WorkingMemory memory, ReteNet net) {
		super(id, memory, net);
		inputs = new ArrayList<Node>();
	}
	
	public MultiBetaJoinNode(Engine e) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), e.getNet());
	}

	protected boolean applyFilters(WorkingMemoryElement t) throws JoinFilterException,
			EvaluationException {
		for (final GeneralizedJoinFilter f : filters)
			if (!f.evaluate(t.getFactTuple(), net.getEngine() )) return false;
		return true;
	}

	
	
	protected List<WorkingMemoryElement> getResultedTuples(Node sender, WorkingMemoryElement elem) {
		List<WorkingMemoryElement> result = new ArrayList<WorkingMemoryElement>();
		List<Object> otherNodes = new ArrayList<Object>();
		for (Node n : inputs) {
			if (n != sender) {
				otherNodes.add(n);
			} else {
				otherNodes.add(elem);
			}
		}
		cartesian(otherNodes, result);	
		return result;
	}
	
	@Override
	public void addWME(Node sender, WorkingMemoryElement newElem) throws NodeException {
		/* so, now we have to generate Cartesian product from the new WME
		 * and all WMEs from all other inputs != sender
		 */
		if (!isActivated())
			return;
		List<WorkingMemoryElement> result = getResultedTuples(sender,newElem);
		for (WorkingMemoryElement wme : result) {
			try {
				if (applyFilters(wme)) addAndPropagate(wme);
			} catch (JoinFilterException e) {
				Logging.logger(this.getClass()).fatal(e);
			} catch (EvaluationException e) {
				Logging.logger(this.getClass()).fatal(e);
			}
		}
	}



	private void cartesian(List<Object> tupleEntries,	List<WorkingMemoryElement> result) {
		/* we search for two indices of node entries. if there is only one,
		 * our recursion anchor is reached. if we find a second one, we have to
		 * reduce the problem
		 */
		int pos=-1;
		int i=0;
		for (Object o : tupleEntries) {
			if (o instanceof Node) {
				if (pos == -1) {
					pos = i;
				} else {
					/* we have found a second node here. we have to reduce the
					 * problem
					 */
					Node fixNode = (Node) tupleEntries.get(pos);
					
					for (WorkingMemoryElement fixedWme : fixNode.memory()) {
						List<Object> l = new ArrayList<Object>();
						for (Object o1 : tupleEntries) l.add(o1);
						l.set(pos, fixedWme);
						cartesian(l, result);					
					}
					return;
				}
			} else {
				assert (o instanceof WorkingMemoryElement);
			}
			i++;
		}
		/* so, we only have one node and some wme's. thats our recursion
		 * anchor and we can generate new tuples now
		 */
		
		int len = tupleEntries.size();
		
		Node fixNode = (Node) tupleEntries.get(pos);
		for (WorkingMemoryElement elem : fixNode.memory() ){
			FactTuple tup = new FactTupleImpl(new Fact[0]);
			for (int idx=0; idx<len; idx++) {
				WorkingMemoryElement wmeHere;
				
				if (idx == pos) {
					wmeHere = elem;
				} else {
					wmeHere = (WorkingMemoryElement) tupleEntries.get(idx);
				}
				
				for (Fact factInWme : wmeHere.getFactTuple().getFacts()) {
					tup = tup.appendFact(factInWme);
				}
								
			}
			result.add(tup);
		}
		
	}

	@Override
	public Node[] getParentNodes() {
		Node[] result = new Node[inputs.size()];
		result = inputs.toArray(result);
		return result;
	}

	@Override
	protected NodeDrawer newNodeDrawer() {
		return new MultiBetaFilterNodeDrawer(this);
	}

	@Override
	public boolean outputsBeta() {
		return true;
	}

	@Override
	protected Node registerParent(Node n) throws NodeException {
		inputs.add(n);
		return this;
	}

	@Override
	public void removeWME(Node sender, WorkingMemoryElement oldElem) throws NodeException {
		/* so, now we have to generate Cartesian product from the new WME
		 * and all WMEs from all other inputs != sender
		 */
		if (!isActivated())
			return;
		List<WorkingMemoryElement> result = getResultedTuples(sender,oldElem);
		for (WorkingMemoryElement wme : result) {
			try {
				if (applyFilters(wme)) removeAndPropagate(wme);
			} catch (JoinFilterException e) {
				Logging.logger(this.getClass()).fatal(e);
			} catch (EvaluationException e) {
				Logging.logger(this.getClass()).fatal(e);
			}
		}
	}

	@Override
	protected void unbindFromParents() {
		inputs = new ArrayList<Node>();
	}

}
