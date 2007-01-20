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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rule.MultiValue;


/**
 * @author Peter Lin
 *
 * Slot2 is used for conditions that evaluate against multiple values.
 * For example: (attr2 "me" | "you" | ~"her" | ~"she")
 * <br>
 * Rather than evaluate a long sequence of equal/not equal sequentially,
 * we use two lists: equal and notequal. Slot2 is used exclusive for
 * a sequence of "or" value comparisons.
 */
public class Slot2 extends Slot {

    private List equalsList = new ArrayList();
    private List notEqualList = new ArrayList();

    public Slot2(){
    }
    
    public Slot2(String name) {
    	this.setName(name);
    }
    
    /**
     * method will check to see if the object is a collection. if it is,
     * it will iterate over the collection and add each one to the right
     * list.
     * @param val
     */
    public void setValue(Object val) {
    	if (val instanceof Collection) {
    		Iterator itr = ((Collection)val).iterator();
    		while (itr.hasNext()) {
    			MultiValue mv = (MultiValue)itr.next();
    			if (mv.getNegated()) {
    				notEqualList.add(mv.getValue());
    			} else {
    				equalsList.add(mv.getValue());
    			}
    		}
    	}
    }
    
    /**
     * the method doesn't apply to slot2
     */
    public Object getValue() {
    	return null;
    }
    
    /**
     * get the list of values the slot should equal to
     * @return
     */
    public List getEqualList(){
        return this.equalsList;
    }

    /**
     * set the values the slot should equal to 
     * @param val
     */
    public void setEqualList(List val){
        this.equalsList = val;
    }

    /**
     * get the list of values the slot should not equal to
     * @return
     */
    public List getNotEqualList(){
        return this.notEqualList;
    }
    
    /**
     * set the list of values the slot should not equal to
     * @param val
     */
    public void setNotEqualList(List val){
        this.notEqualList = val;
    }
    
    public String toString(String andOr) {
    	StringBuffer buf = new StringBuffer();
    	if (this.equalsList.size() > 0) {
    		Iterator itr = this.equalsList.iterator();
    		buf.append( itr.next().toString() );
    		while (itr.hasNext()) {
        		buf.append(andOr + itr.next().toString() );
    		}
    	}
    	if (this.notEqualList.size() > 0) {
    		Iterator itr = this.notEqualList.iterator();
    		buf.append( itr.next().toString() );
    		while (itr.hasNext()) {
    			buf.append(andOr + itr.next().toString());
    		}
    	}
    	return buf.toString();
    }
    
    /**
     * A convienance method to clone slots
     */
    public Object clone(){
        Slot2 newslot = new Slot2();
        newslot.setId(this.getId());
        newslot.setName(this.getName());
        newslot.setEqualList(this.getEqualList());
        newslot.setNotEqualList(this.getNotEqualList());
        newslot.setValueType(this.getValueType());
        return newslot;
    }
}
