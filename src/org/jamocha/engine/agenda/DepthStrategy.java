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

package org.jamocha.engine.agenda;

public class DepthStrategy extends ConflictResolutionStrategy {

	public int compare(Activation act1, Activation act2) {
		int sal1 = act1.getRule().getSalience();
		int sal2 = act2.getRule().getSalience();
		if (sal1 == sal2) {
			long time1 = act1.getTuple().getAggregateCreationTimestamp();
			long time2 = act2.getTuple().getAggregateCreationTimestamp();
			if (time1 > time2)
				return -1;
			else if (time1 < time2)
				return 1;
			else
				return 0;
		} else if (sal1 > sal2)
			return -1;
		else
			return 1;
	}

	@Override
	public String getName() {
		return getNameStatic();
	}

	public static String getNameStatic() {
		return "DepthStrategy";
	}

}
