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

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author Peter Lin
 *
 * The ReturnVector can contain one or more items from a function.
 * Functions can return a specific implementation of ReturnVector.
 * This makes it easier to customize functions and process the
 * results of a function.
 * The interface extends Iterator, so 
 */
public interface ReturnVector extends Serializable {
    
    /**
     * the number of items returned by the function
     * @return
     */
    int size();
    /**
     * Class implementing the method should return itself, since
     * ReturnVector extends Iterator.
     * @return
     */
    Iterator getIterator();

	ReturnValue firstReturnValue();
	
    /**
     * Fucntions should add Return values in sequence using this method.
     * @param val
     */
    void addReturnValue(ReturnValue val);
}
