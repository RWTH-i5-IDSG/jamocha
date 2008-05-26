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

/**
 * @author Christoph Emonds
 * 
 * Parser is a simple interface defining the basic parser operation
 */
public interface Parser {

	/**
	 * This method returns the next expression, or <code>null</code>, if
	 * there is no more expression in the input.
	 * 
	 * @return The next expression or
	 *         <code>null</null> if no further expression is available.
	 * @throws ParseException if an error occured during parsing the input.
	 */
	Expression nextExpression() throws ParseException;
}
