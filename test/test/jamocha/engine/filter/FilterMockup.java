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
package test.jamocha.engine.filter;

import java.util.Set;

import org.jamocha.engine.memory.SlotType;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.SlotInFactAddress;
import org.jamocha.filter.Filter;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathTransformation;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class FilterMockup extends Filter {
	public FilterMockup(final boolean returnValue) {
		super(new FilterElement[] { new FilterElement(
				new FunctionWithArguments() {
					@Override
					public SlotType returnType() {
						return SlotType.BOOLEAN;
					}

					@Override
					public SlotType[] paramTypes() {
						return SlotType.empty;
					}

					@Override
					public Object evaluate(final Object... params) {
						return returnValue;
					}

					@Override
					public FunctionWithArguments translatePath(
							final PathTransformation translation, Node childNode) {
						return this;
					}

					@Override
					public void gatherPaths(final Set<Path> paths) {
					}
				}, new SlotInFactAddress[] {}) });
	}

	public static FilterMockup alwaysTrue() {
		return new FilterMockup(true);
	}

	public static FilterMockup alwaysFalse() {
		return new FilterMockup(true);
	}

}
