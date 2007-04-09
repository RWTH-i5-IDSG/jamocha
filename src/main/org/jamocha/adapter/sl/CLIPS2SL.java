/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jamocha.adapter.sl;

import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Template;

/**
 * This class translates CLIPS-Code resp. JamochaValues to SL.
 * 
 * @author Alexander Wilden
 * 
 */
public class CLIPS2SL {

	/**
	 * A private constructor to force access only in a static way.
	 * 
	 */
	private CLIPS2SL() {
	}

	/**
	 * Translates a JamochaValue into SL-Code.
	 * 
	 * @param value
	 *            A JamochaValue that should be translated.
	 * @return The result of the translation.
	 */
	public static String getSL(JamochaValue value) {
		StringBuilder res = new StringBuilder();
		if (value.getType().equals(JamochaType.BOOLEAN)
				|| value.getType().equals(JamochaType.DATETIME)
				|| value.getType().equals(JamochaType.DOUBLE)
				|| value.getType().equals(JamochaType.LONG)
				|| value.getType().equals(JamochaType.STRING)) {
			res.append(value.toString());
		} else if (value.getType().equals(JamochaType.LIST)) {
			res.append("(sequence \n");
			for (int i = 0; i < value.getListCount(); ++i) {
				res.append(" " + getSL(value.getListValue(i)) + " "); // recursion
			}
			res.append(")");
		} else if (value.getType().equals(JamochaType.FACT)) {
			Fact fact = value.getFactValue();
			Template tmpl = fact.getTemplate();
			res.append("(" + tmpl.getName() + "\n");
			for (int i = 0; i < tmpl.getNumberOfSlots(); i++) {
				res.append("		:" + tmpl.getSlot(i).getName() + " "
						+ getSL(fact.getSlotValue(i)));
			}
			res.append(" )\n");
		}
		return res.toString();
	}

}
