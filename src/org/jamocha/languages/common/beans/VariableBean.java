/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * 
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.languages.common.beans;

import org.jamocha.languages.common.BeansVisitor;
import org.jamocha.visitor.Visitable;

import lombok.Data;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
@Data
public class VariableBean implements Visitable<BeansVisitor> {
	public static enum VariableType {
		SINGLE, MULTI, GLOBAL;
	}

	String image;
	VariableType type;

	public void SetSingle() {
		this.type = VariableType.SINGLE;
	}

	public void SetMulti() {
		this.type = VariableType.MULTI;
	}

	public void SetGlobal() {
		this.type = VariableType.GLOBAL;
	}

	@Override
	public <T extends BeansVisitor> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}
}
