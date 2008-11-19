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

import java.util.List;

import org.jamocha.engine.ReteNet;
import org.jamocha.engine.nodes.joinfilter.GeneralizedJoinFilter;
import org.jamocha.engine.nodes.joinfilter.JoinFilterException;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.parser.EvaluationException;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de> This class provides
 *         an abstract node type, which has two inputs and a set of join
 *         filters.
 */
public abstract class AbstractBetaFilterNode extends TwoInputNode {

	protected GeneralizedJoinFilter[] filters;

	public AbstractBetaFilterNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		this(id, memory, net, new GeneralizedJoinFilter[0]);
	}

	public AbstractBetaFilterNode(final int id, final WorkingMemory memory,
			final ReteNet net, final GeneralizedJoinFilter[] filters) {
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
		for (final GeneralizedJoinFilter f : getFilters())
			if (!f.evaluate(alpha.getFirstFact(), beta.getFactTuple(), net
					.getEngine()))
				return false;
		return true;
	}

	public abstract void addWME(Node sender, final WorkingMemoryElement newElem) throws NodeException;

	public abstract void removeWME(Node sender, final WorkingMemoryElement oldElem) throws NodeException;
	

	protected GeneralizedJoinFilter[] getFilters() {
		if (filters != null)
			return filters;
		final GeneralizedJoinFilter[] empty = {};
		return empty;
	}

	public void setFilter(final GeneralizedJoinFilter[] filter) {
		filters = filter;
	}


	public void setFilter(final List<GeneralizedJoinFilter> filter) {
		GeneralizedJoinFilter[] arr = new GeneralizedJoinFilter[filter.size()];
		arr = filter.toArray(arr);
		setFilter(arr);
	}


	public void addFilter(final GeneralizedJoinFilter filter) {
		final GeneralizedJoinFilter[] tmpFilters = getFilters();
		final GeneralizedJoinFilter[] arr = new GeneralizedJoinFilter[tmpFilters.length + 1];
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
