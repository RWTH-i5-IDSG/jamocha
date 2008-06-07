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

package org.jamocha.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpressionCollection implements Parameter {

	protected ArrayList<Parameter> parameterList = new ArrayList<Parameter>();

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(); // abstract class!
	}

	public ExpressionCollection() {
		super();
	}

	public boolean isFactBinding() {
		boolean objectBinding = false;
		for (int i = 0; i < parameterList.size() && !objectBinding; ++i)
			objectBinding |= parameterList.get(i).isFactBinding();
		return objectBinding;
	}

	public boolean add(Parameter o) {
		return parameterList.add(o);
	}

	public Parameter get(int index) {
		return parameterList.get(index);
	}

	public void toArray(Parameter[] params) {
		parameterList.toArray(params);
	}

	public List<Parameter> getList() {
		return parameterList;
	}

	public int size() {
		return parameterList.size();
	}

}