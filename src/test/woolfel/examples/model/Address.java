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
 * A simple test bean that represents a generic address. It is meant
 * to be used with Account class.
 */
public class Address {

    protected String street1 = null;
    protected String street2 = null;
    protected String city = null;
    protected String state = null;
    protected String zip = null;
    protected String accountId = null;
    
    protected ArrayList listeners = new ArrayList();
    
	/**
	 * 
	 */
	public Address() {
		super();
	}

    public void setStreet1(String val){
        if (!val.equals(this.street1)){
            String old = this.street1;
            this.street1 = val;
            notifyListener("street1",old,this.street1);
        }
    }
    
    public String getStreet1(){
        return this.street1;
    }
    
    public void setStreet2(String val){
        if (!val.equals(this.street2)){
            String old = this.street2;
            this.street2 = val;
            notifyListener("street2",old,this.street2);
        }
    }
    
    public String getStreet2(){
        return this.street2;
    }
    
    public void setCity(String val){
        if (!val.equals(this.city)){
            String old = this.city;
            this.city = val;
            notifyListener("city",old,this.city);
        }
    }
    
    public String getCity(){
        return this.city;
    }
    
    public void setState(String val){
        if (!val.equals(this.state)){
            String old = this.state;
            this.state = val;
            notifyListener("state",old,this.state);
        }
    }
    
    public String getState(){
        return this.state;
    }
    
    public void setZip(String val){
        if (!val.equals(this.zip)){
            String old = this.zip;
            this.zip = val;
            notifyListener("zip",old,this.zip);
        }
    }
    
    public String getZip(){
        return this.zip;
    }
    
    public void setAccountId(String val){
        if (!val.equals(this.accountId)){
            String old = this.accountId;
            this.accountId = val;
            notifyListener("accountId",old,this.accountId);
        }
    }
    
    public String getAccountId(){
        return this.accountId;
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
