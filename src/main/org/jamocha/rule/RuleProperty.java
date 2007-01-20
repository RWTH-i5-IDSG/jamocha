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
package org.jamocha.rule;

/**
 * @author Peter Lin
 *
 * Version is a generic object for version information. For now, the purpose
 * is for rule version information. Since RuleML supports the notion of rule
 * version and CLIPS doesn't, this is an extension.
 */
public class RuleProperty {

    public static final String AUTO_FOCUS = "auto-focus";
    /**
     * This a rule property specific to Sumatra and is an extension
     */
    public static final String VERSION = "rule-version";
    
    /**
     * Salience defines the priority of a rule. It's a concept
     * from CLIPS, ART and OPS5
     */
    public static final String SALIENCE = "salience";
    
    /**
     * The alpha memories can be explicitly turned off by the user
     */
    public static final String REMEMBER_MATCH = "remember-match";

    /**
     * A rule can have a direction declaration. Although backward
     * chaining isn't implemented yet, it's here for the future
     */
    public static final String DIRECTION = "chaining-direction";
    
    /**
     * if a rule has no-agenda set to true, it will skip the agenda
     * and fire immediately.
     */
    public static final String NO_AGENDA = "no-agenda";
    
    public static final String EFFECTIVE_DATE = "effective-date";
    public static final String EXPIRATION_DATE = "expiration-date";
    
    private String name = null;
    private String value = null;
    private int intVal = 0;
    private boolean boolVal = true;
    
	/**
	 * 
	 */
	public RuleProperty() {
		super();
	}
    
    public RuleProperty(String name, String ver) {
        this.name = name;
        this.value = ver;
    }

    public RuleProperty(String name, int val) {
        this.name = name;
        this.intVal = val;
    }
    
    public RuleProperty(String name, boolean val) {
        this.name = name;
        this.boolVal = val;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String text) {
        this.name = text;
    }
    
    public String getValue() {
        return this.value;
    }

    public void setValue(String ver) {
        this.value = ver;
    }
    
    public int getIntValue() {
        return this.intVal;
    }
    
    public void setIntValue(int val) {
        this.intVal = val;
    }
    
    public boolean getBooleanValue() {
        return this.boolVal;
    }
    
    public void setBooleanValue(boolean val) {
        this.boolVal = val;
    }
}
