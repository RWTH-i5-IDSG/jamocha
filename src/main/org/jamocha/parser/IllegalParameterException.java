/*
 * Copyright 2007 Christoph Emonds
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
package org.jamocha.parser;

public class IllegalParameterException extends EvaluationException {

	private static final long serialVersionUID = 1L;

	public IllegalParameterException(int parameterCount) {
		this(parameterCount,false);
	}
	

	public IllegalParameterException(int parameterCount, boolean orMore) {
		super("Expected parameter count "+parameterCount+(orMore?"+":""));
	}
	
	public IllegalParameterException(int parameterCount, String ParameterName){
		super("Expected parameter " + ParameterName + " at position " + parameterCount);
	}

}
