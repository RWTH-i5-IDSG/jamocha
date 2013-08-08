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

public interface Function {
	/**
	 * @return list of the corresponding parameter types for the function
	 */
	public SlotType[] paramTypes();

	/**
	 * @return return type of the function
	 */
	public SlotType returnType();

	/**
	 * @return name of the corresponding function in CLIPS
	 */
	public String inClips();

	/**
	 * Evaluates the function for the given parameters and returns the result
	 * 
	 * @param params
	 *            parameters for the function call
	 * @return result of the function call
	 */
	public Object evaluate(final Object... params);
}
