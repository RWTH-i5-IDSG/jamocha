package org.jamocha.languages.sl.sl2clips_adapter.performative;

import org.jamocha.languages.sl.sl2clips_adapter.AdapterTranslationException;

public class SLPerformativeTranslatorNotFoundException extends
		AdapterTranslationException {

	private static final long serialVersionUID = 1L;

	public SLPerformativeTranslatorNotFoundException(String performative) {
		super("The SLPerformativeTranslator for the performative "
				+ performative + " could not be found.");
	}

}
