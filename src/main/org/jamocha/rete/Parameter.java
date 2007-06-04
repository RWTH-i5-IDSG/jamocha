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

import org.jamocha.parser.Expression;


/**
 * @author Peter Lin
 *
 * Parameter can be a value, a bound variable or the result of a function.
 * It is up to the implementing class to provide the necessary logic.
 */
public interface Parameter extends Expression {
	
    /**
     * If the parameter is an object binding, the method should return true
     * @return
     */
    boolean isObjectBinding();

}
