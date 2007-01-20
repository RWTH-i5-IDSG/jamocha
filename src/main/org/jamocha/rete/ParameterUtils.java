/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.util.List;

import org.jamocha.rete.SlotParam;


public class ParameterUtils {
	/**
	 * The method takes a list containing Parameters and converts it to
	 * an array of Parameter[]. Do not pass a list with other types
	 * @param list
	 * @return
	 */
	public static Parameter[] convertParameters(java.util.List list) {
		Parameter[] pms = new Parameter[list.size()];
		for (int idx=0; idx < list.size(); idx++) {
			pms[idx] = (Parameter)list.get(idx);
		}
		return pms;
	}
	
	/**
	 * slotToParameters is a convienant utility method that converts
	 * a list containing parameters and Slots to an array of Parameter[].
	 * The method is used by the parser to handle modify statements.
	 * @param list
	 * @return
	 */
	public static Parameter[] slotToParameters(List list) {
		Parameter[] pms = new Parameter[list.size()];
		for (int idx=0; idx < list.size(); idx++) {
			if (list.get(idx) instanceof Slot) {
				pms[idx] = new SlotParam( (Slot)list.get(idx));
			} else {
				pms[idx] = (Parameter)list.get(idx);
			}
		}
		return pms;
	}

}
