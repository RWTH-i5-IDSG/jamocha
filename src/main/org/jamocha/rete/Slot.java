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

/**
 * @author Peter Lin
 *
 * Slot is similar to CLIPS slots, though slightly different.
 * 
 */
public class Slot extends AbstractSlot {

    protected Object value = Constants.NIL_SYMBOL;

    public Slot(){
    }
    
    /**
     * Create a new instance with a given name
     * @param name
     */
    public Slot(String name){
        this.setName(name);
    }
    
    /**
     * For convenience you can create here a slot with a given value directly
     */
    public Slot( String name, Object value ) {
    	this(name);
    	this.value = value;
    }
    
    /**
     * get the value of the slot
     * @return
     */
    public Object getValue(){
        return this.value;
    }

    /**
     * set the value of the slot
     * @param val
     */
    public void setValue(Object val){
        this.value = val;
        if (this.getValueType() < 0) {
            inspectType();
        }
    }
    
    /**
     * In some cases, a deftemplate can be define with a
     * default value.
     * @param value
     */
    public void setDefaultValue(Object value){
        this.value = value;
    }
    
    /**
     * method will look at the value and set the int type
     */
    protected void inspectType() {
        if (this.value instanceof Double) {
            this.setValueType(Constants.DOUBLE_PRIM_TYPE);
        } else if (this.value instanceof Long) {
            this.setValueType(Constants.LONG_PRIM_TYPE);
        } else if (this.value instanceof Float) {
            this.setValueType(Constants.FLOAT_PRIM_TYPE);
        } else if (this.value instanceof Short) {
            this.setValueType(Constants.SHORT_PRIM_TYPE);
        } else if (this.value instanceof Integer) {
            this.setValueType(Constants.INT_PRIM_TYPE);
        } else {
            this.setValueType(Constants.OBJECT_TYPE);
        }
    }
    
    /**
     * A convienance method to clone slots
     */
    public Object clone(){
        Slot newslot = new Slot();
        newslot.setId(this.getId());
        newslot.setName(this.getName());
        newslot.value = this.value;
        newslot.setValueType(this.getValueType());
        return newslot;
    }
    
	public String valueToString() {
		if (this.getValueType() == Constants.STRING_TYPE) {
			return "\"" + this.value.toString() + "\"";
		} else {
			return this.value.toString();
		}
	}
}
