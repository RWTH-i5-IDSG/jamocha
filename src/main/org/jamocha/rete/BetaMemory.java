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
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Peter Lin
 *
 * Basic interface for BetaMemory. I created the interface after I coded up
 * the implementation, so the interface has the important methods. Hopefully
 * I won't need to change this interface much in the future.
 */
public interface BetaMemory extends Serializable {
	/**
	 * clear the beta memory
	 */
    void clear();
    /**
     * get the index for the beta memory.
     * @return
     */
    Index getIndex();
    /**
     * classes implementing the interface should get the
     * Fact[] from the index
     * @return
     */
    Fact[] getLeftFacts();
    /**
     * Get the facts that match from the right side
     * @return
     */
    Iterator iterateRightFacts();
    /**
     * Get the match count
     * @return
     */
    int matchCount();
    /**
     * check if a fact already matched
     * @param rightfact
     * @return
     */
    boolean matched(Fact rightfact);
    /**
     * add a match
     * @param rightfact
     */
    void addMatch(Fact rightfact);
    /**
     * remove a matched fact
     * @param rightfact
     */
    void removeMatch(Fact rightfact);
    /**
     * the implementing class needs to decide to format the
     * matches in the beta memory
     * @return
     */
    String toPPString();
}
