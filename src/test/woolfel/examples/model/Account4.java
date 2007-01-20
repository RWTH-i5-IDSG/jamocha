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
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Peter Lin
 *
 * A simple test bean that represents a generic account. It could be
 * a bank account, shopping card account, or any type of membership
 * account with a nationwide company.
 */
public class Account4 implements Serializable {

    protected String first = null;
    protected String middle = null;
    protected String last = null;
    /**
     * mr, mrs, ms, junior, etc
     */
    protected String title = null;
    protected String accountId = null;
    protected String accountType = null;
    protected String status = null;
    protected String username = null;
    protected String countryCode = null;
    protected double cash;
    
    protected ArrayList listeners = new ArrayList();
    
	/**
	 * 
	 */
	public Account4() {
		super();
	}

    public void setTitle(String val){
        if (!val.equals(this.title)){
            String old = this.title;
            this.title = val;
            notifyListener("title",old,this.title);
        }
    }
    
    public String getTitle(){
        return this.title;
    }
    
    public void setFirst(String val){
        if (!val.equals(this.first)){
            String old = this.first;
            this.first = val;
            notifyListener("first",old,this.first);
        }
    }
    
    public String getFirst(){
        return this.first;
    }
    
    public void setLast(String val){
        if (!val.equals(this.last)){
            String old = this.last;
            this.last = val;
            notifyListener("last",old,this.last);
        }
    }
    
    public String getLast(){
        return this.last;
    }
    
    public void setMiddle(String val){
        if (!val.equals(this.middle)){
            String old = this.middle;
            this.middle = val;
            notifyListener("middle",old,this.middle);
        }
    }
    
    public String getMiddle(){
        return this.middle;
    }
    
    public void setStatus(String val){
        if (!val.equals(this.status)){
            String old = this.status;
            this.status = val;
            notifyListener("status",old,this.status);
        }
    }
    
    public String getStatus(){
        return this.status;
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
    
    public void setAccountType(String val){
        if (!val.equals(this.accountType)){
            String old = this.accountType;
            this.accountType = val;
            notifyListener("accountType",old,this.accountType);
        }
    }
    
    public String getAccountType(){
        return this.accountType;
    }
    
    public void setUsername(String val){
        if (!val.equals(this.username)){
            String old = this.username;
            this.username = val;
            notifyListener("username",old,this.username);
        }
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public void setCountryCode(String val){
        if (!val.equals(this.countryCode)){
            String old = this.countryCode;
            this.countryCode = val;
            notifyListener("countryCode",old,this.countryCode);
        }
    }
    
    public String getCountryCode(){
        return this.countryCode;
    }

    public void setCash(double value) {
    	if (value != this.cash) {
    		Double old = new Double(this.cash);
    		this.cash = value;
    		this.notifyListener("cash",old,new Double(this.cash));
    	}
    }
    
    public double getCash() {
    	return this.cash;
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
