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
package org.jamocha.filter;

import lombok.RequiredArgsConstructor;

import org.jamocha.engine.memory.SlotType;
import org.jamocha.engine.nodes.Node;

/**
 * @author Fabian Ohler
 * 
 */
public interface FunctionWithArguments extends Function {

	/**
	 * 
	 * @param translation
	 * @param childNode
	 * @return
	 */
	public FunctionWithArguments translatePath(
			final PathTranslation translation, final Node childNode);
	
	@RequiredArgsConstructor
	public class FunctionWithArgumentsFullJoinDummy implements FunctionWithArguments {
		
		SlotType [] paramTypes;
		
		@Override
		public SlotType[] paramTypes() {
			return paramTypes;
		}

		@Override
		public SlotType returnType() {
			return SlotType.BOOLEAN;
		}

		@Override
		public Object evaluate(Object... params) {
			return true;
		}

		@Override
		public FunctionWithArguments translatePath(PathTranslation translation,
				Node childNode) {
			// TODO Auto-generated method stub
			return this;
		}
		
	}
}

