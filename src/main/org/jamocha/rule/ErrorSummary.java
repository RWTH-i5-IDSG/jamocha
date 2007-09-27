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

import org.jamocha.Constants;

/**
 * @author Peter Lin
 *
 */
public class ErrorSummary implements Summary {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] errors = new String[0];
	
	/**
	 * 
	 */
	public ErrorSummary() {
	}

	public void addMessage(String reason) {
		int len = this.errors.length;
		String[] newerr = new String[len + 1];
		System.arraycopy(this.errors, 0, newerr, 0, this.errors.length);
		newerr[len] = reason;
		this.errors = newerr;
	}

	public String getMessage() {
		StringBuffer buf = new StringBuffer();
		for (int idx=0; idx < this.errors.length; idx++) {
			buf.append(this.errors[idx] + Constants.LINEBREAK);
		}
		return buf.toString();
	}

	public String[] getMessages() {
		return this.errors;
	}

}
