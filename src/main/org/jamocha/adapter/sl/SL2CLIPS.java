package org.jamocha.adapter.sl;

import org.jamocha.adapter.AdapterTranslationException;
import org.jamocha.adapter.sl.performative.SLPerformativeFactory;
import org.jamocha.adapter.sl.performative.SLPerformativeTranslator;

public class SL2CLIPS {

	public static String getCLIPS(String performative, String slCode)
			throws AdapterTranslationException {
		SLPerformativeTranslator translator = SLPerformativeFactory
				.getSLPerformativeTranslator(performative);
		if (translator != null) {
			return translator.getCLIPS(slCode);
		}
		throw new AdapterTranslationException(
				"No suitable SLPerformativeTranslator found for performative "
						+ performative + ".");
	}

}
