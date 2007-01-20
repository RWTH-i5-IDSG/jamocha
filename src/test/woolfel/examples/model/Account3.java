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

/**
 * @author Peter Lin
 *
 */
public class Account3 extends Account2 {

    protected String street1 = null;
    protected String stree2 = null;
    protected String city = null;
    protected String state = null;
    protected String country = null;
    
	/**
	 * 
	 */
	public Account3() {
		super();
	}
    
    public String getStreet1() {
        return this.street1;
    }
    
    public void setStreet1(String text) {
        if (!text.equals(this.street1)) {
            String old = this.street1;
            this.street1 = text;
            this.notifyListener("street1",old,this.street1);
        }
    }
    
    public String getStreet2() {
        return this.stree2;
    }
    
    public void setStreet2(String text) {
        if (!text.equals(this.stree2)) {
            String old = this.stree2;
            this.stree2 = text;
            this.notifyListener("stree2",old,this.stree2);
        }
    }
    
    public String getCity() {
        return this.city;
    }
    
    public void setCity(String text) {
        if (!text.equals(this.city)) {
            String old = this.city;
            this.city = text;
            this.notifyListener("city",old,this.city);
        }
    }
    
    public String getState() {
        return this.state;
    }
    
    public void setState(String text) {
        if (!text.equals(this.state)) {
            String old = this.state;
            this.state = text;
            this.notifyListener("state",old,this.state);
        }
    }
    
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(String text) {
        if (!text.equals(this.country)) {
            String old = this.country;
            this.country = text;
            this.notifyListener("country",old,this.country);
        }
    }
}
