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

package org.jamocha.engine.functions;

import org.jamocha.parser.JamochaType;

public interface FunctionDescription {

	public String getDescription();

	public JamochaType[] getReturnType();

	public int getParameterCount();

	public boolean isParameterCountFixed();

	public boolean isParameterOptional(int parameter);

	public JamochaType[] getParameterTypes(int parameter);

	public String getParameterName(int parameter);

	public String getParameterDescription(int parameter);

	public String getExample();

	public boolean isResultAutoGeneratable();

	public Object getExpectedResult();

}
