package org.jamocha.adapter.sl.performative;

import java.util.List;

import org.jamocha.adapter.AdapterTranslationException;
import org.jamocha.adapter.sl.configurations.SLConfiguration;

public abstract class SLPerformativeTranslator {

	private static int uniqueId = 0;

	/**
	 * This function checks if the <code> list </code> of Configurations has the
	 * same quantity as the integer <code>hasToBe</code>. If not an Exception
	 * is thrown.
	 * 
	 * @param list
	 *            The list to check.
	 * @param hasToBe
	 *            The number of items the <code>list</code> should contain.
	 * @throws WrongContentItemCountException
	 *             Is thrown if <code>list.size() != hasToBe</code>.
	 */
	protected final void checkContentItemCount(List<SLConfiguration> list,
			int hasToBe) throws WrongContentItemCountException {
		if (list.size() != hasToBe) {
			throw new WrongContentItemCountException(list.size(), hasToBe);
		}
	}

	/**
	 * Returns a (mostly) unique integer for use in identifiers.
	 * 
	 * @return A (pseudo) unique integer.
	 */
	protected final synchronized int getUniqueId() {
		// Prevent from overflow and negative numbers. This should be really
		// enough ... If not I'll spend you a beer at Joe's Garage ...
		uniqueId = (uniqueId + 1) % Integer.MAX_VALUE;
		return uniqueId;
	}

	public abstract String getCLIPS(String slCode)
			throws AdapterTranslationException;

}
