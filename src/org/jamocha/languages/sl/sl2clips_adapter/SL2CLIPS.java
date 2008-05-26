package org.jamocha.languages.sl.sl2clips_adapter;

import org.jamocha.languages.sl.sl2clips_adapter.performative.SLPerformativeFactory;
import org.jamocha.languages.sl.sl2clips_adapter.performative.SLPerformativeTranslator;


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
