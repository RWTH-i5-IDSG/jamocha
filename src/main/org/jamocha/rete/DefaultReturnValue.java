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

import org.jamocha.parser.JamochaValue;

/**
 * @author Peter Lin
 * 
 * DefaultReturnValue simply extends ValueParam. In many ways, input parameters
 * and return values have similar needs. Both have to contain information about
 * the type of value. ReturnValue defines the basic methods for getting the
 * value and figuring out what type it is.
 * <ol>
 * <li> Parameter interface extends ReturnValue</li>
 * <li> AbstractParam implements Parameter interface</li>
 * <li> ValueParam extends AbstractParam</li>
 * </ol>
 * The convienance methods in ReturnValue should make it easier to access the
 * values.
 */
public class DefaultReturnValue extends ValueParam {

	/**
	 * 
	 */
	public DefaultReturnValue(JamochaValue value) {
		super(value);
	}
}
