/*
 * Copyright 2002-2013 The Jamocha Team
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

/**
 * @author Fabian Ohler (ohler@dbis.rwth-aachen.de)
 */
package org.jamocha.filter.impls.predicates;

import org.jamocha.filter.Predicate;
import org.jamocha.filter.SlotType;
import org.jamocha.filter.TODODatenkrakeFunktionen;

public class Equals {
	static {
		TODODatenkrakeFunktionen.addImpl(new Predicate() {
			@Override
			public SlotType[] paramTypes() {
				return new SlotType[] { SlotType.LONG, SlotType.LONG };
			}

			@Override
			public String inClips() {
				return "=";
			}

			@Override
			public Boolean evaluate(final Object... params) {
				return (Long) params[0] == (Long) params[1];
			}
		});
		TODODatenkrakeFunktionen.addImpl(new Predicate() {
			@Override
			public SlotType[] paramTypes() {
				return new SlotType[] { SlotType.DOUBLE, SlotType.DOUBLE };
			}

			@Override
			public String inClips() {
				return "=";
			}

			@Override
			public Boolean evaluate(final Object... params) {
				return (Double) params[0] == (Double) params[1];
			}
		});
	}
}
