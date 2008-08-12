/*
 * Copyright 2002-2008 The Jamocha Team
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

/**
 * 
 */
package org.jamocha.rules;

/**
*
* @author Josef Hahn
* @author Karl-Heinz Krempels
* @author Janno von Stuelpnagel
* @author Christoph Terwelp
*/
public interface ConstraintVisitor <T extends Object, S extends Object> {
	
	public S visit(AndConnectedConstraint c, T data);

	public S visit(BoundConstraint c, T data);

	public S visit(LiteralConstraint c, T data);

	public S visit(OrConnectedConstraint c, T data);
	
	public S visit(OrderedFactConstraint c, T data);
	
	public S visit(PredicateConstraint c, T data);
	
	public S visit(ReturnValueConstraint c, T data);

}
