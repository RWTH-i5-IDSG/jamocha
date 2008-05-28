/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.ReteNet;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.engine.nodes.joinfilter.JoinFilter;
import org.jamocha.engine.nodes.joinfilter.JoinFilterException;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de> This class provides
 *         an abstract node type, which has two inputs and a set of join
 *         filters.
 */
public abstract class AbstractBetaFilterNode extends TwoInputNode {

	protected JoinFilter[] filters;

	public AbstractBetaFilterNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		this(id, memory, net, null);
	}

	public AbstractBetaFilterNode(final int id, final WorkingMemory memory,
			final ReteNet net, final JoinFilter[] filters) {
		super(id, memory, net);
		this.filters = filters;
	}

	/**
	 * returns true, iff the given alpha- and beta-wme combination is accepted
	 * by the filters
	 * 
	 * @param alpha
	 * @param beta
	 * @return
	 * @throws JoinFilterException
	 * @throws EvaluationException
	 */
	protected boolean applyFilters(final WorkingMemoryElement alpha,
			final WorkingMemoryElement beta) throws JoinFilterException,
			EvaluationException {
		for (final JoinFilter f : getFilters())
			if (!f.evaluate(alpha.getFirstFact(), beta.getFactTuple(), net
					.getEngine()))
				return false;
		return true;
	}

	@Override
	public void addWME(final WorkingMemoryElement newElem) throws NodeException {
		if (!isActivated())
			return;
		try {
			Logging.logger(this.getClass()).debug("add wme "+newElem+" to node "+this);
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

	/**
	 * this method is called, when a new beta wme is added.
	 */
	protected abstract void addBeta(WorkingMemoryElement newElem)
			throws JoinFilterException, EvaluationException, NodeException;

	/**
	 * this method is called, when a new alpha wme is added.
	 */
	protected abstract void addAlpha(WorkingMemoryElement newElem)
			throws JoinFilterException, EvaluationException, NodeException;

	@Override
	public void removeWME(final WorkingMemoryElement oldElem)
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

	/**
	 * this method is called, when an alpha wme is removed.
	 */
	protected abstract void removeBeta(WorkingMemoryElement oldElem)
			throws JoinFilterException, EvaluationException, NodeException;

	/**
	 * this method is called, when a beta wme is removed.
	 */
	protected abstract void removeAlpha(WorkingMemoryElement oldElem)
			throws JoinFilterException, EvaluationException, NodeException;

	protected JoinFilter[] getFilters() {
		if (filters != null)
			return filters;
		final JoinFilter[] empty = {};
		return empty;
	}

	@Deprecated
	/**
	 * this method should never be called! Only exception is the usage from the
	 * old SFRuleCompiler. New rule compilers must determine the filter array
	 * before and use the constructor argument for setting it
	 */
	public void setFilter(final JoinFilter[] filter) {
		filters = filter;
	}

	@Deprecated
	/**
	 * this method should never be called! Only exception is the usage from the
	 * old SFRuleCompiler. New rule compilers must determine the filter array
	 * before and use the constructor argument for setting it
	 */
	public void setFilter(final List<JoinFilter> filter) {
		JoinFilter[] arr = new JoinFilter[filter.size()];
		arr = filter.toArray(arr);
		setFilter(arr);
	}

	@Deprecated
	/**
	 * this method should never be called! Only exception is the usage from the
	 * old SFRuleCompiler. New rule compilers must determine the filter array
	 * before and use the constructor argument for setting it
	 */
	public void addFilter(final JoinFilter filter) {
		final JoinFilter[] tmpFilters = getFilters();
		final JoinFilter[] arr = new JoinFilter[tmpFilters.length + 1];
		if (tmpFilters.length > 0)
			System.arraycopy(tmpFilters, 0, arr, 0, tmpFilters.length);
		arr[tmpFilters.length] = filter;
		setFilter(arr);
	}

	@Override
	public boolean outputsBeta() {
		return true;
	}

}
