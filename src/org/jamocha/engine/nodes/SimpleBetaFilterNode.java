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

import java.util.Iterator;

import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.application.gui.retevisualisation.nodedrawers.SimpleBetaFilterNodeDrawer;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.nodes.joinfilter.GeneralizedJoinFilter;
import org.jamocha.engine.nodes.joinfilter.JoinFilterException;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.parser.EvaluationException;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> this node has an alpha- and
 *         a beta-input. each combination (which means the Cartesian product
 *         "BETA-INPUT x ALPHA-INPUT") will be evaluated by the given join
 *         filters. if all filters accept a combination, it will pass this node.
 */
public class SimpleBetaFilterNode extends AbstractBetaFilterNode {

	@Deprecated
	public SimpleBetaFilterNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		super(id, memory, net);
	}

	@Deprecated
	public SimpleBetaFilterNode(final int id, final WorkingMemory memory,
			final ReteNet net, final GeneralizedJoinFilter[] filters) {
		super(id, memory, net, filters);
	}
	
	public SimpleBetaFilterNode(Engine e) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), e.getNet());
	}
	
	public SimpleBetaFilterNode(Engine e, GeneralizedJoinFilter[] filters) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), e.getNet(), filters);
	}
	

	protected void addAlpha(final WorkingMemoryElement newElem)
			throws JoinFilterException, EvaluationException, NodeException {
		if (betaInput != null && betaInput.workingMemory != null)
			for (final WorkingMemoryElement beta : betaInput.workingMemory
					.getMemory(betaInput))
				if (applyFilters(newElem, beta)) {
					final WorkingMemoryElement newTuple = beta.getFactTuple()
							.appendFact(newElem.getFirstFact());
					addAndPropagate(newTuple);
				}
	}

	protected void addBeta(final WorkingMemoryElement newElem)
			throws JoinFilterException, EvaluationException, NodeException {
		if (alphaInput != null && alphaInput.workingMemory != null)
			for (final WorkingMemoryElement alpha : alphaInput.workingMemory
					.getMemory(alphaInput))
				if (applyFilters(alpha, newElem)) {
					final WorkingMemoryElement newTuple = newElem
							.getFactTuple().appendFact(alpha.getFirstFact());
					addAndPropagate(newTuple);
				}
	}

	public void removeWME(Node sender, final WorkingMemoryElement oldElem)
			throws NodeException {
		try {
			if (oldElem.isStandaloneFact())
				removeAlpha(oldElem);
			else
				removeBeta(oldElem);
		} catch (final Exception e) {
			throw new NodeException(
					"error while removing working memory element. ", e, this);
		}
	}

	
	protected void removeAlpha(final WorkingMemoryElement oldElem)
			throws JoinFilterException, EvaluationException, NodeException {
		final Iterator<WorkingMemoryElement> i = memory().iterator();
		while (i.hasNext()) {
			final WorkingMemoryElement wme = i.next();
			if (wme.getLastFact().getFactId()==oldElem.getFirstFact().getFactId()) {
				i.remove();
				propagateRemoval(wme);
			}
		}
	}

	protected void removeBeta(final WorkingMemoryElement oldElem)
			throws JoinFilterException, EvaluationException, NodeException {
		final Iterator<WorkingMemoryElement> i = memory().iterator();
		while (i.hasNext()) {
			final WorkingMemoryElement wme = i.next();
			if (wme.getFactTuple().isMySubTuple(oldElem.getFactTuple())) {
				i.remove();
				propagateRemoval(wme);
			}
		}
	}

	@Override
	protected NodeDrawer newNodeDrawer() {
		return new SimpleBetaFilterNodeDrawer(this);
	}

	@Override
	public void getDescriptionString(final StringBuilder sb) {
		super.getDescriptionString(sb);
		sb.append("|filters:");
		for (final GeneralizedJoinFilter f : getFilters())
			sb.append(f.toPPString() + " & ");
	}
	@Override
	public void addWME(Node sender, final WorkingMemoryElement newElem) throws NodeException {
		if (!isActivated())
			return;
		try {
			if (newElem.isStandaloneFact())
				addAlpha(newElem);
			else
				addBeta(newElem);
		} catch (final Exception e) {
			e.printStackTrace();
			throw new NodeException(
					"error while adding working memory element. ", e, this);
		}
	}

}
