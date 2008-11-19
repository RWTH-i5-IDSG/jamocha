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

package org.jamocha.communication.events;

import org.jamocha.rules.Rule;

/**
 * @author Peter Lin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CompileEvent extends AbstractEvent {

	public enum CompileEventType {
		RULE_ADDED, RULE_REMOVED
	}
	
    private static final long serialVersionUID = 1L;

    private String message = "";

    private Rule rule = null;

    private CompileEventType type;
    
    /**
     * @param source
     */
    public CompileEvent(Object source, CompileEventType type) {
        super(source);
        this.type = type;
    }


    public void setMessage(String text) {
        this.message = text;
    }

    public String getMessage() {
        if (this.rule != null) {
            return this.rule.getName() + " " + this.message;
        } else {
            return this.message;
        }
    }

    public void setRule(Rule theRule) {
        this.rule = theRule;
    }

    public Rule getRule() {
        return this.rule;
    }
    
    public CompileEventType getType() {
    	return type;
    }
    
}
