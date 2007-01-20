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
package woolfel.examples.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * @author Peter Lin
 *
 * Alternate version of TestBean that does implement add/remove
 * PropertyChangeListener. This version implements notify method
 * to notify the listeners.
 */
public class TestBean3 {

    protected String attr1 = null;
    protected int attr2;
    protected short attr3;
    protected long attr4;
    protected float attr5;
    protected double attr6;
    
    protected ArrayList listeners = new ArrayList();
    
	/**
	 * 
	 */
	public TestBean3() {
		super();
	}

    public void setName(String val){
        this.attr1 = val;
    }
    
    public String getName(){
        return this.attr1;
    }
    
    public void setCount(int val){
        this.attr2 = val;
    }
    
    public int getCount(){
        return this.attr2;
    }
    
    public void setShort(short val){
        this.attr3 = val;
    }
    
    public short getShort(){
        return this.attr3;
    }
    
    public void setLong(long val){
        this.attr4 = val;
    }
    
    public long getLong(){
        return this.attr4;
    }
    
    public void setFloat(float val){
        this.attr5 = val;
    }
    
    public float getFloat(){
        return this.attr5;
    }
    
    public void setDouble(double val){
        this.attr6 = val;
    }
    
    public double getDouble(){
        return this.attr6;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.listeners.add(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener){
        this.listeners.remove(listener);
    }
    
    protected void notifyListener(String field, Object oldValue, Object newValue){
        if (listeners == null || listeners.size() == 0) {
			return;
		} else {
			PropertyChangeEvent event = new PropertyChangeEvent(this, field,
					oldValue, newValue);

			for (int i = 0; i < listeners.size(); i++) {
				((java.beans.PropertyChangeListener) listeners.get(i))
						.propertyChange(event);
			}
		}
        
    }
}
