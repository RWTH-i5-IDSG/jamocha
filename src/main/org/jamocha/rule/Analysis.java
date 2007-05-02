/*
 * Copyright 2002-2006 Peter Lin
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
package org.jamocha.rule;

import java.io.Serializable;

/**
 * Validation interface defines 3 methods a basic validation component
 * would need to have. Validation can occur at any time, so it can be
 * used by the rule compiler, an IDE or a parser.
 * 
 * The product of the validation is either it passes, or a summary of
 * the errors and warnings.
 * 
 * @author Peter Lin
 *
 */
public interface Analysis extends Serializable {
	
	public static final int ANALYSIS_COMPLETE = 100;
	public static final int ANALYSIS_INCOMPLETE = 101;
	public static final int VALIDATION_FAILED = 1000;
	public static final int VALIDATION_PASSED = 1001;
	public static final int VALIDATION_WARNING = 1002;

	/**
	 * If the rule passes validation, it should return true. If the rule
	 * was not valid for any reason, return false.
	 * @param rule
	 * @return
	 */
	int analyze(Rule rule);
	Summary getErrors();
	Summary getWarnings();
	void reset();
}
