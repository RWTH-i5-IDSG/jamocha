package org.jamocha.rete.nodes;

import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.memory.WorkingMemory;
import org.jamocha.rete.memory.WorkingMemoryElement;
import org.jamocha.rete.nodes.joinfilter.JoinFilter;
import org.jamocha.rete.nodes.joinfilter.JoinFilterException;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> This class provides an
 *         abstract node type, which has two inputs and a set of join filters.
 */
public abstract class AbstractBetaFilterNode extends TwoInputNode {

	protected JoinFilter[] filters;

	public AbstractBetaFilterNode(int id, WorkingMemory memory, ReteNet net) {
		this(id, memory, net, null);
	}

	public AbstractBetaFilterNode(int id, WorkingMemory memory, ReteNet net,
			JoinFilter[] filters) {
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
	protected boolean applyFilters(WorkingMemoryElement alpha,
			WorkingMemoryElement beta) throws JoinFilterException,
			EvaluationException {
		for (JoinFilter f : getFilters()) {
			if (!f.evaluate(alpha.getFirstFact(), beta.getFactTuple(), net
					.getEngine())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void addWME(WorkingMemoryElement newElem) throws NodeException {
		try {
			if (newElem.isStandaloneFact()) {
				addAlpha(newElem);
			} else {
				addBeta(newElem);
			}
		} catch (Exception e) {
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
	public void removeWME(WorkingMemoryElement oldElem) throws NodeException {
		try {
			if (oldElem.isStandaloneFact()) {
				removeAlpha(oldElem);
			} else {
				removeBeta(oldElem);
			}
		} catch (Exception e) {
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
		JoinFilter[] empty = {};
		return empty;
	}

	@Deprecated
	/**
	 * this method should never be called! Only exception is the usage from the
	 * old SFRuleCompiler. New rule compilers must determine the filter array
	 * before and use the constructor argument for setting it
	 */
	public void setFilter(JoinFilter[] filter) {
		filters = filter;
	}

	@Deprecated
	/**
	 * this method should never be called! Only exception is the usage from the
	 * old SFRuleCompiler. New rule compilers must determine the filter array
	 * before and use the constructor argument for setting it
	 */
	public void setFilter(List<JoinFilter> filter) {
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
	public void addFilter(JoinFilter filter) {
		JoinFilter[] tmpFilters = getFilters();
		JoinFilter[] arr = new JoinFilter[tmpFilters.length + 1];
		if (tmpFilters.length > 0) {
			System.arraycopy(tmpFilters, 0, arr, 0, tmpFilters.length);
		}
		arr[tmpFilters.length] = filter;
		setFilter(arr);
	}

	@Override
	public boolean outputsBeta() {
		return true;
	}

}
