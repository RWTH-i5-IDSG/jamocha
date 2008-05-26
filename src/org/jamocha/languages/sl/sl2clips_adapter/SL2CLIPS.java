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
